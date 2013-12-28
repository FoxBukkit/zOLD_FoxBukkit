package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.chat.ChatChannel;
import de.doridian.yiffbukkit.chat.ChatHelper;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.Utils;
import org.bukkit.entity.Player;

@Names({"channel", "channels", "c"})
@Help("YiffBukkit chat system :3")
@Usage("Read help...")
@Permission("yiffbukkit.channels.main")
public class ChannelCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		final String cmd = args[0].toUpperCase();

		final ChatHelper helper = ChatHelper.getInstance();
		final String plyname = ply.getName().toLowerCase();

		final ChatChannel chan;
		if (cmd.equals("CREATE")) {
			chan = helper.getActiveChannel(ply);
		} else {
			try {
				chan = helper.getChannel(args[1]);
			}
			catch (RuntimeException e) {
				e.printStackTrace();
				throw new YiffBukkitCommandException("Internal error", e);
			}
		}

		switch (cmd) {
		case "JOIN":
			if (args.length > 2) {
				if (!chan.canJoin(ply, args[2]))
					throw new PermissionDeniedException();
			}
			else {
				if (!chan.canJoin(ply))
					throw new PermissionDeniedException();
			}

			helper.joinChannel(ply, chan);
			helper.sendChat(null, ply.getDisplayName() + "\u00a7f joined this channel", false, chan);
			break;

		case "LIST":
			if (args.length >= 2) {
				// List users
				PlayerHelper.sendDirectedMessage(ply, "Channel users: " + Utils.concat(chan.players.keySet(), 0, "No users"));
				return; //prevents saving!
			}

			final StringBuilder sb = new StringBuilder();
			for (ChatChannel channel : helper.container.channels.values()) {
				if (channel.players.containsKey(plyname)) {
					if (channel.players.get(plyname)) {
						sb.append("\u00a72");
					}
					else {
						sb.append("\u00a74");
					}
					sb.append(channel.name);
					sb.append("\u00a7f, ");
				}
			}
			sb.setLength(sb.length() - 4);

			PlayerHelper.sendDirectedMessage(ply, "Current channels: " + sb.toString());
			return; //prevents saving!

		case "INFO":
			PlayerHelper.sendDirectedMessage(ply, "Channel info");
			PlayerHelper.sendDirectedMessage(ply, "Name: " + chan.name);
			PlayerHelper.sendDirectedMessage(ply, "Owner: " + chan.owner);
			PlayerHelper.sendDirectedMessage(ply, "Mode: " + chan.mode.toString());
			PlayerHelper.sendDirectedMessage(ply, "Range: " + chan.range);
			return; //prevents saving!

		case "CREATE":
			if (!ply.hasPermission("yiffbukkit.channels.create"))
				throw new PermissionDeniedException();

			final ChatChannel newChan = helper.addChannel(ply, args[1]);
			helper.joinChannel(ply, newChan);

			PlayerHelper.sendDirectedMessage(ply, "Channel " + newChan.name + " has been created!");
			break;

		case "PASSWORD":
			if (!chan.isOwner(ply))
				throw new PermissionDeniedException();

			chan.password = args[2];
			PlayerHelper.sendDirectedMessage(ply, "Set password for channel " + chan.name);
			break;

		case "MODERATOR":
			if (!chan.isOwner(ply))
				throw new PermissionDeniedException();

			switch (args[2].toLowerCase()) {
			case "add":
				Player plya = plugin.playerHelper.matchPlayerSingle(args[3]);
				chan.addModerator(plya);
				PlayerHelper.sendDirectedMessage(ply, "Added moderator " + plya.getName() + " to channel " + chan.name);
				break;

			case "delete":
			case "remove":
				Player plyb = plugin.playerHelper.matchPlayerSingle(args[3]);
				chan.removeModerator(plyb);
				PlayerHelper.sendDirectedMessage(ply, "Removed moderator " + plyb.getName() + " from channel " + chan.name);
				break;

			case "list":
				PlayerHelper.sendDirectedMessage(ply, "Channel moderators: " + Utils.concat(chan.moderators, 0, "No moderators"));
				return; //prevents saving!

			default:
				throw new YiffBukkitCommandException("Unknown action (add|remove|list)!");
			}
			break;

		case "DROP":
			if (!chan.isOwner(ply))
				throw new PermissionDeniedException();

			helper.removeChannel(chan);
			PlayerHelper.sendDirectedMessage(ply, "Dropped channel " + chan.name);
			break;

		case "MODE":
			if (!chan.isOwner(ply))
				throw new PermissionDeniedException();

			chan.mode = ChatChannel.ChatChannelMode.valueOf(args[2].toUpperCase());
			PlayerHelper.sendDirectedMessage(ply, "Set mode of channel " + chan.name  + " to " + chan.mode.toString());
			break;

		case "RANGE":
			if (!chan.isOwner(ply))
				throw new PermissionDeniedException();

			chan.range = Integer.parseInt(args[2]);
			String rangeStr;
			if (chan.range <= 0) {
				chan.range = 0;
				rangeStr = "Infinite";
			}
			else {
				rangeStr = "" + chan.range;
			}

			PlayerHelper.sendDirectedMessage(ply, "Set range of channel " + chan.name  + " to " + rangeStr);
			break;

		case "KICK":
			if (!chan.isModerator(ply))
				throw new PermissionDeniedException();

			Player plyx = plugin.playerHelper.matchPlayerSingle(args[2]);
			helper.leaveChannel(plyx, chan);
			PlayerHelper.sendDirectedMessage(ply, "Kicked " + plyx.getDisplayName() + " out of " + chan.name);
			PlayerHelper.sendDirectedMessage(plyx, "You got kicked out of " + chan.name);
			helper.sendChat(null, plyx.getDisplayName() + "\u00a7f got kicked out of this channel", false, chan);
			break;

		case "USER":
			if (!chan.isModerator(ply))
				throw new PermissionDeniedException();

			switch (args[2].toLowerCase()) {
			case "add":
				Player plya = plugin.playerHelper.matchPlayerSingle(args[3]);
				chan.addUser(plya);
				PlayerHelper.sendDirectedMessage(ply, "Added user " + plya.getDisplayName() + "\u00a7f to channel " + chan.name);
				break;

			case "delete":
			case "remove":
				Player plyb = plugin.playerHelper.matchPlayerSingle(args[3]);
				chan.removeUser(plyb);
				PlayerHelper.sendDirectedMessage(ply, "Removed user " + plyb.getDisplayName() + "\u00a7f from channel " + chan.name);
				break;

			case "list":
				PlayerHelper.sendDirectedMessage(ply, "Channel users: " + Utils.concat(chan.users, 0, "No users"));
				return; //prevents saving!

			default:
				throw new YiffBukkitCommandException("Unknown action (add|remove|list)!");
			}
			break;

		case "SWITCH":
			helper.setActiveChannel(ply, chan);
			PlayerHelper.sendDirectedMessage(ply, "You switched to channel " + chan.name);
			break;

		case "SAY":
			helper.sendChat(ply, Utils.concatArray(args, 2, null), true, chan);
			return; //prevents saving!

		case "MUTE":
			if (!chan.players.containsKey(plyname))
				throw new YiffBukkitCommandException("You are not in that channel!");

			final boolean state;
			switch (args[2]) {
			case "off":
				state = true;
				break;

			case "on":
				state = false;
				break;

			default:
				state = !chan.players.get(plyname);
			}

			chan.players.put(plyname, state);

			if (state) {
				PlayerHelper.sendDirectedMessage(ply, "Channel " + chan.name + " UNMUTED");
			}
			else {
				PlayerHelper.sendDirectedMessage(ply, "Channel " + chan.name + " MUTED");
			}
			break;

		case "SINGLE":
			if (!chan.players.containsKey(plyname)) {
				throw new YiffBukkitCommandException("You are not in that channel!");
			}

			chan.players.put(plyname, true);

			for (ChatChannel otherchan : helper.container.channels.values()) {
				if (otherchan == chan || !otherchan.players.containsKey(plyname)) continue;
				otherchan.players.put(plyname, false);
			}

			PlayerHelper.sendDirectedMessage(ply, "Muted all channels except " + chan.name);
			break;

		case "LEAVE":
			helper.sendChat(null, ply.getDisplayName() + "\u00a7f left this channel", false, chan);
			helper.leaveChannel(ply, chan);
			break;

		default:
			throw new YiffBukkitCommandException("Unknown command");
		}

		ChatHelper.saveChannels();
	}
}
