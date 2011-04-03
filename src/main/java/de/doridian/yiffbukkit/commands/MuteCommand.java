package de.doridian.yiffbukkit.commands;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;

public class MuteCommand extends ICommand {
	Set<String> muted = new HashSet<String>();

	public MuteCommand(YiffBukkit plug) {
		super(plug);
		PlayerListener chatListener = new PlayerListener() {
			@Override
			public void onPlayerChat(PlayerChatEvent event) {
				if (muted.contains(event.getPlayer().getName())) {
					plugin.playerHelper.SendDirectedMessage(event.getPlayer(), "You are muted and cannot speak at this time.");
					event.setCancelled(true);
					return;
				}
			}
			
			public void onPlayerCommandPreprocess(PlayerChatEvent event) {
				if (muted.contains(event.getPlayer().getName())) {
					plugin.playerHelper.SendDirectedMessage(event.getPlayer(), "You are muted and cannot use commands at this time.");
					event.setCancelled(true);
					return;
				}
			}
		};

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_CHAT, chatListener, Priority.Highest, plugin);
		pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, chatListener, Priority.Highest, plugin);
	}

	@Override
	public int GetMinLevel() {
		return 6; //This can block access to all commands, High Level Only Access...
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		Boolean onoff;
		String name;
		switch (args.length){
		case 0:
			throw new YiffBukkitCommandException("Syntax error");

		case 1:
			//mute <name> - mute a player
			onoff = null;
			name = playerHelper.CompletePlayerName(args[0], false);

			break;

		default:
			if ("on".equals(args[0])) {
				//mute on <name> - mute a player
				onoff = true;
				name = playerHelper.CompletePlayerName(args[1], false);
			}
			else if ("off".equals(args[0])) {
				//mute off <name> - unmute a player
				onoff = false;
				name = playerHelper.CompletePlayerName(args[1], false);
			}
			else {
				//mute <name> <...> - not sure yet
				name = playerHelper.CompletePlayerName(args[0], false);

				if ("on".equals(args[1])) {
					//mute <name> on - mute a player
					onoff = true;
				}
				else if ("off".equals(args[1])) {
					//mute <name> off - unmute a player
					onoff = false;
				}
				else {
					throw new YiffBukkitCommandException("Syntax error");
				}
			}
			break;
		}

		if (onoff == null) {
			onoff = !muted.contains(name);
		}

		if (onoff) {
			muted.add(name);
			playerHelper.SendServerMessage(ply.getName() + " muted " + name + ".");
		}
		else {
			muted.remove(name);
			playerHelper.SendServerMessage(ply.getName() + " unmuted " + name + ".");
		}
	}

	@Override
	public String GetHelp() {
		return "Mutes or unmutes a player.";
	}

	@Override
	public String GetUsage() {
		return "[name] [on|off]";
	}
}