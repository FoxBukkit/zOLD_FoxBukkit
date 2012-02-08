package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@Names("muteall")
@Help("Mutes all player chat")
@Usage("[on|off]")
@Permission("yiffbukkitsplit.users.muteall")
public class MuteAllCommand extends ICommand implements Listener {
	private boolean muteall = false;

	public MuteAllCommand() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(PlayerChatEvent event) {
		if (muteall && !plugin.permissionHandler.has(event.getPlayer(), "yiffbukkitsplit.users.muteall")) {
			plugin.playerHelper.sendDirectedMessage(event.getPlayer(), "Server chat is disabled at this time for all users.");
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (muteall && !plugin.permissionHandler.has(event.getPlayer(), "yiffbukkitsplit.users.muteall")) {
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