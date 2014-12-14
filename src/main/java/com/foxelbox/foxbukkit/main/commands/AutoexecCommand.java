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
package com.foxelbox.foxbukkit.main.commands;

import com.foxelbox.foxbukkit.core.util.MessageHelper;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.BooleanFlags;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.NumericFlags;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

@Names("autoexec")
@Help("Schedules commands to be executed every time you connect.")
@Usage("[<command>|-r <index>]")
@Permission("foxbukkit.autoexec")
@BooleanFlags("e")
@NumericFlags("r")
public class AutoexecCommand extends ICommand implements Listener {
	public AutoexecCommand() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				execCommands(player);
			}
		});
	}

	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		if (argStr.trim().isEmpty()) {
			listAutoexec(ply);

			return;
		}

		argStr = parseFlags(argStr);

		if (booleanFlags.contains('e')) {
			execCommands(ply);

			return;
		}

		if (numericFlags.containsKey('r')) {
			final List<String> commands = playerHelper.autoexecs.get(ply.getUniqueId());

			final int id = (int) (double) numericFlags.get('r');

			if (commands == null)
				throw new FoxBukkitCommandException("You never defined an autoexec.");
			if (commands.size() <= id)
				throw new FoxBukkitCommandException("Index out of range.");

			String removedCommand = commands.remove(id);
			if (commands.isEmpty())
				playerHelper.autoexecs.remove(ply.getUniqueId());

			playerHelper.saveAutoexecs();

			listAutoexec(ply);
			final String restoreCommand = "/autoexec " + removedCommand;
			MessageHelper.sendMessage(ply, "Removed command %1$s: <color name=\"blue\">%2$s</color> " + MessageHelper.button(restoreCommand, "restore", "dark_green", false), "" + id, removedCommand);

			return;
		}

		if (argStr.charAt(0) != '/')
			argStr = '/' + argStr;

		final List<String> commands = getAutoexec(ply);
		commands.add(argStr);
		playerHelper.saveAutoexecs();

		listAutoexec(ply);
		final int id = commands.size() - 1;
		final String undoCommand = "/autoexec -r " + id;
		MessageHelper.sendMessage(ply, "Added command %1$s: <color name=\"blue\">%2$s</color> " + MessageHelper.button(undoCommand, "x", "red", true), "" + id, argStr);
	}

	private List<String> getAutoexec(Player player) {
		List<String> commands = playerHelper.autoexecs.get(player.getUniqueId());
		if (commands == null)
			playerHelper.autoexecs.put(player.getUniqueId(), commands = new ArrayList<>());

		return commands;
	}

	private void listAutoexec(Player player) {
		PlayerHelper.sendDirectedMessage(player, "Current autoexec:");

		List<String> commands = playerHelper.autoexecs.get(player.getUniqueId());
		if (commands == null || commands.isEmpty()) {
			PlayerHelper.sendDirectedMessage(player, "<empty>");
			return;
		}

		for (int id = 0; id < commands.size(); ++id) {
			final String command = commands.get(id);
			final String removeCommand = "/autoexec -r " + id;
			MessageHelper.sendMessage(player, "%1$s: <color name=\"blue\">%2$s</color> " + MessageHelper.button(removeCommand, "x", "red", true), "" + id, command);
		}
	}

	private void execCommands(final Player player) {
		List<String> commands = playerHelper.autoexecs.get(player.getUniqueId());
		if (commands == null)
			return;

		for (String command : commands) {
			player.chat(command);
		}
	}
}
