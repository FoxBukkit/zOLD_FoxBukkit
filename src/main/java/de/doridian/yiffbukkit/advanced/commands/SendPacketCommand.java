package de.doridian.yiffbukkit.advanced.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.chat.Parser;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.AbusePotential;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_7_R1.ChatBaseComponent;
import net.minecraft.server.v1_7_R1.IntHashMap;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.xml.bind.JAXBException;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Names("sendpacket")
@Help("Sends a packet to the given player (requires technical knowledge).")
@Usage("<name> <id>[ <parameter>=<value>]*")
@Permission("yiffbukkit.sendpacket")
@AbusePotential
public class SendPacketCommand extends ICommand {
	private static final Pattern keyValuePattern = Pattern.compile("^([^=]+)=(.*)$");
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		try {
			final ChatBaseComponent component = Parser.parse(argStr);
			System.out.println(component);
			final PacketPlayOutChat packet1 = new PacketPlayOutChat(component);
			for (Player player : new Player[]{asPlayer(commandSender)}) {
				PlayerHelper.sendPacketToPlayer(player, packet1);
			}
		}
		catch (JAXBException e1) {
			e1.printStackTrace();
			throw new YiffBukkitCommandException("Exception occurred while parsing", e1);
		}
		if (true)
			return;

		if (args.length < 2)
			throw new YiffBukkitCommandException("Too few arguments");

		final Player otherply = playerHelper.matchPlayerSingle(args[0]);
		final int packetId = Integer.parseInt(args[1]);

		final IntHashMap idToClass = Utils.getPrivateValue(Packet.class, null, "l"); // v1_6_R2
		@SuppressWarnings("unchecked")
		final Class<? extends Packet> packetClass = (Class<? extends Packet>) idToClass.get(packetId);

		if (packetClass == null)
			throw new YiffBukkitCommandException("There is no packet with ID "+packetId+".");

		final Packet packet;
		try {
			packet = packetClass.newInstance();
		} catch (InstantiationException e) {
			throw new YiffBukkitCommandException("Could not instantiate packet class "+packetClass.getName()+".", e);
		} catch (IllegalAccessException e) {
			throw new YiffBukkitCommandException("Packet constructor is private (this should never happen)", e);
		}

		final Field field_modifiers;
		try {
			field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);
		} catch (Exception e) {
			throw new YiffBukkitCommandException("Could not make Field.modifiers accessible.", e);
		}

		for (int i = 2; i < args.length; ++i) {
			final String arg = args[i];
			final Matcher matcher = keyValuePattern.matcher(arg);
			if (!matcher.matches())
				throw new YiffBukkitCommandException("Cannot parse key=value pair '"+arg+"'. "+keyValuePattern.pattern());

			final String key = matcher.group(1);
			final String valueString = matcher.group(2);

			final Field f;
			try {
				f = packetClass.getDeclaredField(key);
			} catch (NoSuchFieldException e) {
				throw new YiffBukkitCommandException("Field "+key+" not found.", e);
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
				throw new YiffBukkitCommandException("Field "+key+" of type "+fClassName+" cannot be set.");
			}

			try {
				final int modifiers = field_modifiers.getInt(f);
				if ((modifiers & 0x10) != 0)
					field_modifiers.setInt(f, modifiers & 0xFFFFFFEF);

				f.setAccessible(true);
				f.set(packet, value);
			}
			catch (IllegalAccessException e) {
				throw new YiffBukkitCommandException("Could not access f.modifiers.", e);
			}
		}

		PlayerHelper.sendPacketToPlayer(otherply, packet);
		PlayerHelper.sendDirectedMessage(commandSender, "Sent packet to "+otherply.getName()+".");
	}
}
