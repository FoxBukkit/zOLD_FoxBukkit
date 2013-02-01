package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

@Names("restart")
@Permission("yiffbukkit.admin.restart")
public class RestartCommand extends ICommand {
	int taskID = -1;
	RestartRunnable restarter;

	@Override
	public void run(final CommandSender sender, String[] args, String argStr) {
		if(taskID >= 0) {
			plugin.getServer().getScheduler().cancelTask(taskID);
			taskID = -1;
			plugin.playerHelper.sendServerMessage("Restart cancelled!");
			return;
		}
		long time = 120;
		try {
			time = Long.parseLong(args[0]);
		} catch(Exception e) { }
		restarter = new RestartRunnable(time);
		restarter.taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, restarter, 10, 10);
		taskID = restarter.taskID;
	}

	private class RestartRunnable implements Runnable {
		long lasttimeleft = 0;
		int taskID;
		long endtime;

		public RestartRunnable(long duration) {
			endtime = System.currentTimeMillis() + (duration * 1000);
			announceInChat(duration);
		}

		@Override
		public void run() {
			long timeleft = (endtime - System.currentTimeMillis()) / 1000;
			if(timeleft == lasttimeleft) return;
			lasttimeleft = timeleft;
			if(timeleft <= 0) {
				plugin.getServer().getScheduler().cancelTask(taskID);
				plugin.getServer().shutdown();
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
					for(Player ply : plugin.getServer().getOnlinePlayers()) {
						ply.kickPlayer("Server is restarting! Reconnecting instantly will slow down the restart!");
					}
				} else if(timeleft == 1) {
					plugin.getServer().savePlayers();
				}
			}
		}
	}

	public void announceInChat(long timeleft) {
		plugin.playerHelper.sendServerMessage("Server restarting in " + timeleft + " seconds!");
	}
}
