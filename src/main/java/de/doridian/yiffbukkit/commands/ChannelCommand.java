package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.chat.ChatChannel;
import de.doridian.yiffbukkit.chat.ChatHelper;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.commands.ICommand.Usage;
import de.doridian.yiffbukkit.util.Utils;
import org.bukkit.entity.Player;

@Names({"channel", "channels", "c"})
@Help("YiffBukkit chat system :3")
@Usage("Read help...")
@Permission("yiffbukkit.channels.main")
public class ChannelCommand extends ICommand {
	private enum SubCommand {
		JOIN, LIST, INFO, //everyone [or yiffbukkit.channels.force.user for forcing join allowance]
		CREATE, //yiffbukkit.channels.create
		PASSWORD, MODERATOR, DROP, MODE, RANGE, //channel owner or yiffbukkit.channels.force.owner
		KICK, USER, //moderator or yiffbukkit.channels.force.moderator
		SWITCH, SAY, MUTE, SINGLE, LEAVE; //users in channel or yiffbukkit.channels.force.user
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		SubCommand cmd = SubCommand.valueOf(args[0].toUpperCase());

		ChatHelper helper = ChatHelper.getInstance();
		String plyname = ply.getName().toLowerCase();

		ChatChannel chan = helper.getActiveChannel(ply);
		if (cmd != SubCommand.CREATE) {
			try {
				chan = helper.getChannel(args[1]);
			}
			catch (YiffBukkitCommandException e) {
				throw e; 
			}
			catch (Exception e) {
				// Exceptchu, I choose you!
			}
		}

		switch (cmd) {
		case JOIN:
			if (!ply.hasPermission("yiffbukkit.channels.force.user")) {
				if (args.length > 2) {
					if (!chan.canJoin(ply, args[2])) {
						throw new PermissionDeniedException();
					}
				}
				else {
					if (!chan.canJoin(ply)) {
						throw new PermissionDeniedException();
					}
				}
			}

			helper.joinChannel(ply, chan);
			helper.sendChat(null, ply.getDisplayName() + "\u00a7f joined this channel", false, chan);
			break;

		case LIST:
			if (args.length < 2) {
				StringBuilder sb = new StringBuilder();
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

				plugin.playerHelper.sendDirectedMessage(ply, "Current channels: " + sb.toString());
			}
			else {
				plugin.playerHelper.sendDirectedMessage(ply, "Channel users: " + Utils.concatArray(chan.players.keySet().toArray(new String[0]), 0, "No users"));
			}
			return; //prevents saving!

		case INFO:
			plugin.playerHelper.sendDirectedMessage(ply, "Channel info");
			plugin.playerHelper.sendDirectedMessage(ply, "Name: " + chan.name);
			plugin.playerHelper.sendDirectedMessage(ply, "Owner: " + chan.owner);
			plugin.playerHelper.sendDirectedMessage(ply, "Mode: " + chan.mode.toString());
			plugin.playerHelper.sendDirectedMessage(ply, "Range: " + chan.range);
			return; //prevents saving!

		case CREATE:
			if (!ply.hasPermission("yiffbukkit.channels.create")) throw new PermissionDeniedException();

			chan = helper.addChannel(ply, args[1]);
			helper.joinChannel(ply, chan);

			plugin.playerHelper.sendDirectedMessage(ply, "Channel " + chan.name + " has been created!");
			break;

		case PASSWORD:
			if (!chan.isOwner(ply)) {
				throw new PermissionDeniedException();
			}

			chan.password = args[2];
			plugin.playerHelper.sendDirectedMessage(ply, "Set password for channel " + chan.name);
			break;

		case MODERATOR:
			if (!chan.isOwner(ply)) {
				throw new PermissionDeniedException();
			}

			char subsub = args[2].toLowerCase().charAt(0);
			switch (subsub) {
			case 'a':
				Player plya = plugin.playerHelper.matchPlayerSingle(args[3]);
				chan.addModerator(plya);
				plugin.playerHelper.sendDirectedMessage(ply, "Added moderator " + plya.getName() + " to channel " + chan.name);
				break;

			case 'd':
			case 'r':
				Player plyb = plugin.playerHelper.matchPlayerSingle(args[3]);
				chan.removeModerator(plyb);
				plugin.playerHelper.sendDirectedMessage(ply, "Removed moderator " + plyb.getName() + " from channel " + chan.name);
				break;

			case 'l':
				plugin.playerHelper.sendDirectedMessage(ply, "Channel moderators: " + Utils.concatArray(chan.moderators.toArray(new String[0]), 0, "No moderators"));
				return; //prevents saving!

			default:
				throw new YiffBukkitCommandException("Unknown action!");
			}
			break;

		case DROP:
			if (!chan.isOwner(ply)) {
				throw new PermissionDeniedException();
			}

			helper.removeChannel(chan);
			plugin.playerHelper.sendDirectedMessage(ply, "Dropped channel " + chan.name);
			break;

		case MODE:
			if (!chan.isOwner(ply)) {
				throw new PermissionDeniedException();
			}

			chan.mode = ChatChannel.ChatChannelMode.valueOf(args[2].toUpperCase());
			plugin.playerHelper.sendDirectedMessage(ply, "Set mode of channel " + chan.name  + " to " + chan.mode.toString());
			break;

		case RANGE:
			if (!chan.isOwner(ply)) {
				throw new PermissionDeniedException();
			}

			chan.range = Integer.parseInt(args[2]);
			String rangeStr;
			if (chan.range <= 0) {
				chan.range = 0;
				rangeStr = "Infinite";
			}
			else {
				rangeStr = "" + chan.range;
			}

			plugin.playerHelper.sendDirectedMessage(ply, "Set range of channel " + chan.name  + " to " + rangeStr);
			break;

		case KICK:
			if (!chan.isModerator(ply)) {
				throw new PermissionDeniedException();
			}

			Player plyx = plugin.playerHelper.matchPlayerSingle(args[2]);
			helper.leaveChannel(plyx, chan);
			plugin.playerHelper.sendDirectedMessage(ply, "Kicked " + plyx.getDisplayName() + " out of " + chan.name);
			plugin.playerHelper.sendDirectedMessage(plyx, "You got kicked out of " + chan.name);
			helper.sendChat(null, plyx.getDisplayName() + "\u00a7f got kicked out of this channel", false, chan);
			break;

		case USER:
			if (!chan.isModerator(ply)) {
				throw new PermissionDeniedException();
			}

			char subsub2 = args[2].toLowerCase().charAt(0);
			switch (subsub2) {
			case 'a':
				Player plya = plugin.playerHelper.matchPlayerSingle(args[3]);
				chan.addUser(plya);
				plugin.playerHelper.sendDirectedMessage(ply, "Added user " + plya.getDisplayName() + "\u00a7f to channel " + chan.name);
				break;

			case 'd':
			case 'r':
				Player plyb = plugin.playerHelper.matchPlayerSingle(args[3]);
				chan.removeUser(plyb);
				plugin.playerHelper.sendDirectedMessage(ply, "Removed user " + plyb.getDisplayName() + "\u00a7f from channel " + chan.name);
				break;

			case 'l':
				plugin.playerHelper.sendDirectedMessage(ply, "Channel users: " + Utils.concatArray(chan.users.toArray(new String[0]), 0, "No users"));
				return; //prevents saving!

			default:
				throw new YiffBukkitCommandException("Unknown action!");
			}
			break;

		case SWITCH:
			helper.setActiveChannel(ply, chan);
			plugin.playerHelper.sendDirectedMessage(ply, "You switched to channel " + chan.name);
			break;

		case SAY:
			helper.sendChat(ply, Utils.concatArray(args, 2, null), true, chan);
			return; //prevents saving!

		case MUTE:
			if (!chan.players.containsKey(plyname)) {
				throw new YiffBukkitCommandException("You are not in that channel!");
			}

			boolean state;
			try {
				if ("off".equals(args[2])) state = true;
				else if ("on".equals(args[2])) state = false;
				else throw new Exception();
			}
			catch (Exception e) {
				state = !chan.players.get(plyname);
			}

			chan.players.put(plyname, state);

			if (state) {
				plugin.playerHelper.sendDirectedMessage(ply, "Channel " + chan.name + " UNMUTED");
			}
			else {
				plugin.playerHelper.sendDirectedMessage(ply, "Channel " + chan.name + " MUTED");
			}
			break;

		case SINGLE:
			if (!chan.players.containsKey(plyname)) {
				throw new YiffBukkitCommandException("You are not in that channel!");
			}

			chan.players.put(plyname, true);

			for (ChatChannel otherchan : helper.container.channels.values()) {
				if (otherchan == chan || !otherchan.players.containsKey(plyname)) continue;
				otherchan.players.put(plyname, false);
			}

			plugin.playerHelper.sendDirectedMessage(ply, "Muted all channels except " + chan.name);
			break;

		case LEAVE:
			helper.sendChat(null, ply.getDisplayName() + "\u00a7f left this channel", false, chan);
			helper.leaveChannel(ply, chan);
			break;

		default:
			throw new YiffBukkitCommandException("Unknown command");
		}

		ChatHelper.saveChannels();
	}
}
