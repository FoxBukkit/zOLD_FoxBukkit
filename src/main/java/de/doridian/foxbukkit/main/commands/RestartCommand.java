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
package de.doridian.foxbukkit.main.commands;

import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.multicraft.api.MulticraftAPI;
import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.commands.system.ICommand;
import de.doridian.foxbukkit.main.commands.system.ICommand.AbusePotential;
import de.doridian.foxbukkit.main.commands.system.ICommand.Names;
import de.doridian.foxbukkit.main.commands.system.ICommand.Permission;
import me.confuser.barapi.BarAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

@Names("restart")
@Permission("foxbukkit.admin.restart")
@AbusePotential
public class RestartCommand extends ICommand {
	int taskID = -1;
	RestartRunnable restarter;

	@Override
	public void run(final CommandSender sender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		if(taskID >= 0) {
			plugin.getServer().getScheduler().cancelTask(taskID);
			taskID = -1;
			PlayerHelper.sendServerMessage("Restart cancelled!");
			for (Player player : Bukkit.getOnlinePlayers()) {
				BarAPI.removeBar(player);
			}
			return;
		}

		final long time;
		if (args.length == 0) {
			time = 120;
		}
		else {
			try {
				time = Long.parseLong(args[0]);
			}
			catch (NumberFormatException e) {
				throw new FoxBukkitCommandException("Number expected.", e);
			}
		}

		restarter = new RestartRunnable(time);
		restarter.taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, restarter, 10, 10);
		taskID = restarter.taskID;
	}

	private class RestartRunnable implements Runnable {
		private final long duration;
		long lasttimeleft = 0;
		int taskID;
		private final long endtime;

		public RestartRunnable(long duration) {
			this.duration = duration;
			endtime = System.currentTimeMillis() + (duration * 1000);
			announceInChat(duration);
			adjustBar(duration);
		}

		@Override
		public void run() {
			long timeleft = (endtime - System.currentTimeMillis()) / 1000;
			if(timeleft == lasttimeleft) return;
			lasttimeleft = timeleft;
			adjustBar(timeleft);
			if(timeleft <= 0) {
				plugin.getServer().getScheduler().cancelTask(taskID);
				restartServer();
			} else if(timeleft > 60) {
				if((timeleft % 60) == 0) {
					announceInChat(timeleft);
				}
			} else if(timeleft > 10) {
				if((timeleft % 10) == 0) {
					announceInChat(timeleft);
				}
			} else {
				announceInChat(timeleft);
				if(timeleft == 2) {
					for(Player ply : Bukkit.getOnlinePlayers()) {
						ply.kickPlayer("Server restarting");
					}
				} else if(timeleft == 1) {
					plugin.getServer().savePlayers();
				}
			}
		}

		public void adjustBar(long timeleft) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				BarAPI.setMessage(player, "Restarting server in " + timeleft + " seconds", 100f * timeleft / duration);
			}
		}
	}

	public void announceInChat(long timeleft) {
		PlayerHelper.sendServerMessage("Server restarting in " + timeleft + " seconds!");
	}

	private static final String ENDPOINT_URL = "http://panel.mc.doridian.de/api.php";
	private static final String API_USER = "admin";
	private static final String API_KEY = "06ffd261c790e2d31d66";

	public static void restartServer() {
		//plugin.getServer().shutdown();
		final MulticraftAPI api = new MulticraftAPI(ENDPOINT_URL, API_USER, API_KEY);

		api.call("restartServer", Collections.singletonMap("id", "1"));
	}
}
