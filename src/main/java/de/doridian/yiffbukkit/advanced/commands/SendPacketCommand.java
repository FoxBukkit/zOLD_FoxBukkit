package de.doridian.yiffbukkit.advanced.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_4_R1.Packet;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Names("sendpacket")
@Help("Sends a packet to the given player (requires technical knowledge).")
@Usage("<name> <id>[ <parameter>=<value>]*")
@Permission("yiffbukkit.sendpacket")
public class SendPacketCommand extends ICommand {
	private static final Pattern keyValuePattern = Pattern.compile("^([^=]+)=(.*)$");
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		if (args.length < 2)
			throw new YiffBukkitCommandException("Too few arguments");

		Player otherply = playerHelper.matchPlayerSingle(args[0]);
		int packetId = Integer.parseInt(args[1]);

		Map<Integer, Class<? extends Packet>> idToClass = Utils.getPrivateValue(Packet.class, null, "b");
		Class<? extends Packet> packetClass = idToClass.get(packetId);

		if (packetClass == null)
			throw new YiffBukkitCommandException("There is no packet with ID "+packetId+".");

		Packet packet;
		try {
			packet = packetClass.newInstance();
		} catch (InstantiationException e) {
			throw new YiffBukkitCommandException("Could not instantiate packet class "+packetClass.getName()+".", e);
		} catch (IllegalAccessException e) {
			throw new YiffBukkitCommandException("Packet constructor is private (this should never happen)", e);
		}

		Field field_modifiers;
		try {
			field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);
		} catch (Exception e) {
			throw new YiffBukkitCommandException("Could not make Field.modifiers accessible.", e);
		}

		for (int i = 2; i < args.length; ++i) {
			final String arg = args[i];
			Matcher matcher = keyValuePattern.matcher(arg);
			if (!matcher.matches())
				throw new YiffBukkitCommandException("Cannot parse key=value pair '"+arg+"'. "+keyValuePattern.pattern());

			String key = matcher.group(1);
			String valueString = matcher.group(2);

			final Field f;
			try {
				f = packetClass.getDeclaredField(key);
			} catch (NoSuchFieldException e) {
				throw new YiffBukkitCommandException("Field "+key+" not found.", e);
			}

			final String fClassName = f.getType().getCanonicalName();

			final Object value;
			switch (fClassName.hashCode()) {
			case 1195259493: // String.class.getCanonicalName().hashCode()
				value = valueString;
				break;

			case 64711720: // boolean.class.getCanonicalName().hashCode()
			case 344809556: // Boolean.class.getCanonicalName().hashCode()
				value = Boolean.valueOf(valueString);
				break;

			case 3039496: // byte.class.getCanonicalName().hashCode()
			case 398507100: // Byte.class.getCanonicalName().hashCode()
				value = Byte.valueOf(valueString);
				break;

			case 109413500: // short.class.getCanonicalName().hashCode()
			case -515992664: // Short.class.getCanonicalName().hashCode()
				value = Byte.valueOf(valueString);
				break;

			case 104431: // int.class.getCanonicalName().hashCode()
			case -2056817302: // Integer.class.getCanonicalName().hashCode()
				value = Integer.valueOf(valueString);
				break;

			case 3327612: // long.class.getCanonicalName().hashCode()
			case 398795216: // Long.class.getCanonicalName().hashCode()
				value = Long.valueOf(valueString);
				break;

			case 97526364: // float.class.getCanonicalName().hashCode()
			case -527879800: // Float.class.getCanonicalName().hashCode()
				value = Float.valueOf(valueString);
				break;

			case -1325958191: // double.class.getCanonicalName().hashCode()
			case 761287205: // Double.class.getCanonicalName().hashCode()
				value = Double.valueOf(valueString);
				break;

			default:
				throw new YiffBukkitCommandException("Field "+key+" of type "+fClassName+" cannot be set.");
			}

			try {
				int modifiers = field_modifiers.getInt(f);
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
