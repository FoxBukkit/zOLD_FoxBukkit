/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.advanced.commands;

import com.foxelbox.foxbukkit.chat.HTMLParser;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.AbusePotential;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import com.foxelbox.foxbukkit.main.util.Utils;
import net.minecraft.server.v1_8_R3.IntHashMap;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Names("sendpacket")
@Help("Sends a packet to the given player (requires technical knowledge).")
@Usage("<name> <id>[ <parameter>=<value>]*")
@Permission("foxbukkit.sendpacket")
@AbusePotential
public class SendPacketCommand extends ICommand {
	private static final Pattern keyValuePattern = Pattern.compile("^([^=]+)=(.*)$");
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		HTMLParser.sendToPlayer(commandSender, argStr);
		if (true)
			return;

		if (args.length < 2)
			throw new FoxBukkitCommandException("Too few arguments");

		final Player otherply = playerHelper.matchPlayerSingle(args[0]);
		final int packetId = Integer.parseInt(args[1]);

		final IntHashMap idToClass = Utils.getPrivateValue(Packet.class, null, "l"); // v1_6_R2
		@SuppressWarnings("unchecked")
		final Class<? extends Packet> packetClass = (Class<? extends Packet>) idToClass.get(packetId);

		if (packetClass == null)
			throw new FoxBukkitCommandException("There is no packet with ID "+packetId+".");

		final Packet packet;
		try {
			packet = packetClass.newInstance();
		} catch (InstantiationException e) {
			throw new FoxBukkitCommandException("Could not instantiate packet class "+packetClass.getName()+".", e);
		} catch (IllegalAccessException e) {
			throw new FoxBukkitCommandException("Packet constructor is private (this should never happen)", e);
		}

		final Field field_modifiers;
		try {
			field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);
		} catch (Exception e) {
			throw new FoxBukkitCommandException("Could not make Field.modifiers accessible.", e);
		}

		for (int i = 2; i < args.length; ++i) {
			final String arg = args[i];
			final Matcher matcher = keyValuePattern.matcher(arg);
			if (!matcher.matches())
				throw new FoxBukkitCommandException("Cannot parse key=value pair '"+arg+"'. "+keyValuePattern.pattern());

			final String key = matcher.group(1);
			final String valueString = matcher.group(2);

			final Field f;
			try {
				f = packetClass.getDeclaredField(key);
			} catch (NoSuchFieldException e) {
				throw new FoxBukkitCommandException("Field "+key+" not found.", e);
			}

			final String fClassName = f.getType().getCanonicalName();

			final Object value;
			switch (fClassName) { // TODO: string switch
			case "java.lang.String":
				value = valueString;
				break;

			case "boolean":
			case "java.lang.Boolean":
				value = Boolean.valueOf(valueString);
				break;

			case "byte":
			case "java.lang.Byte":
				value = Byte.valueOf(valueString);
				break;

			case "short":
			case "java.lang.Short":
				value = Byte.valueOf(valueString);
				break;

			case "int":
			case "java.lang.Integer":
				value = Integer.valueOf(valueString);
				break;

			case "long":
			case "java.lang.Long":
				value = Long.valueOf(valueString);
				break;

			case "float":
			case "java.lang.Float":
				value = Float.valueOf(valueString);
				break;

			case "double":
			case "java.lang.Double":
				value = Double.valueOf(valueString);
				break;

			default:
				throw new FoxBukkitCommandException("Field "+key+" of type "+fClassName+" cannot be set.");
			}

			try {
				final int modifiers = field_modifiers.getInt(f);
				if ((modifiers & 0x10) != 0)
					field_modifiers.setInt(f, modifiers & 0xFFFFFFEF);

				f.setAccessible(true);
				f.set(packet, value);
			}
			catch (IllegalAccessException e) {
				throw new FoxBukkitCommandException("Could not access f.modifiers.", e);
			}
		}

		PlayerHelper.sendPacketToPlayer(otherply, packet);
		PlayerHelper.sendDirectedMessage(commandSender, "Sent packet to "+otherply.getName()+".");
	}
}
