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
package com.foxelbox.foxbukkit.chat.commands;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@Names("muteall")
@Help("Mutes all player chat")
@Usage("[on|off]")
@Permission("foxbukkit.users.muteall")
public class MuteAllCommand extends ICommand implements Listener {
	private boolean muteall = false;

	public MuteAllCommand() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (muteall && !event.getPlayer().hasPermission("foxbukkit.users.muteall")) {
			PlayerHelper.sendDirectedMessage(event.getPlayer(), "Server chat is disabled at this time for all users.");
			event.setCancelled(true);
			//noinspection UnnecessaryReturnStatement
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (muteall && !event.getPlayer().hasPermission("foxbukkit.users.muteall")) {
			String fullCmd = event.getMessage();
			String cmd = fullCmd.substring(0, fullCmd.indexOf(' ')).trim();
			switch (cmd) {
			case "msg":
			case "pm":
			case "conv":
			case "conversation":
			case "kick":
			case "irckick":
			case "settag":
			case "setnick":
			case "setrank":
			case "jail":
				PlayerHelper.sendDirectedMessage(event.getPlayer(), "Some server commands have been disabled for all users.");
				event.setCancelled(true);
				//noinspection UnnecessaryReturnStatement
				return;
			}
		}
	}

	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		final String playerName = ply.getName();
		if (muteall) {
			if (argStr.equals("on"))
				throw new FoxBukkitCommandException("The server is already muted!");

			muteall = false;
			PlayerHelper.sendServerMessage(playerName + " enabled server chat and commands.");
		}
		else {
			if (argStr.equals("off"))
				throw new FoxBukkitCommandException("The server is already unmuted!");

			muteall = true;
			PlayerHelper.sendServerMessage(playerName + " disabled server chat and commands.");
		}

	}
}