package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("muteall")
@Help("Mutes all player chat")
@Usage("[on|off]")
@Permission("yiffbukkit.users.muteall")
public class MuteAllCommand extends ICommand {
	private boolean muteall = false;

	public MuteAllCommand() {
		final PlayerListener chatListener = new PlayerListener() {
			@Override
			public void onPlayerChat(PlayerChatEvent event) {
				if (muteall && !plugin.permissionHandler.has(event.getPlayer(), "yiffbukkit.users.muteall")) {
					plugin.playerHelper.sendDirectedMessage(event.getPlayer(), "Server chat is disabled at this time for all users.");
					event.setCancelled(true);
					return;
				}
			}

			@Override
			public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
				if (muteall && !plugin.permissionHandler.has(event.getPlayer(), "yiffbukkit.users.muteall")) {
					String fullCmd = event.getMessage();
					String cmd = fullCmd.substring(0, fullCmd.indexOf(' ')).trim();
					if(cmd.equals("msg") || cmd.equals("pm") || cmd.equals("conv") || cmd.equals("conversation") || cmd.equals("kick") || cmd.equals("irckick") || cmd.equals("settag") || cmd.equals("setnick") || cmd.equals("setrank") || cmd.equals("jail"))
					{
						plugin.playerHelper.sendDirectedMessage(event.getPlayer(), "Some server commands have been disabled for all users.");
						event.setCancelled(true);
						return;
					}
				}
			}
		};

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_CHAT, chatListener, Priority.Highest, plugin);
		pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, chatListener, Priority.Lowest, plugin);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		final String playerName = ply.getName();
		if (muteall) {
			if (argStr.equals("on"))
				throw new YiffBukkitCommandException("The server is already muted!");

			muteall = false;
			playerHelper.sendServerMessage(playerName + " enabled server chat and commands.");
		}
		else {
			if (argStr.equals("off"))
				throw new YiffBukkitCommandException("The server is already unmuted!");

			muteall = true;
			playerHelper.sendServerMessage(playerName + " disabled server chat and commands.");
		}

	}
}