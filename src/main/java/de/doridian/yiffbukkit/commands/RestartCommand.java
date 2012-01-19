package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.commands.ICommand.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

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
			restarter.removeGUI();
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
			refreshGUI(duration);
		}

		HashMap<SpoutPlayer, GenericLabel> labels = new HashMap<SpoutPlayer, GenericLabel>();
		void refreshGUI(long timeleft) {
			final String formattedTime = "Server restarting in " + (timeleft / 60) + ":" + String.format("%02d", timeleft % 60);

			SpoutPlayer[] players = SpoutManager.getOnlinePlayers();
			for(SpoutPlayer ply : players) {
				try {
					GenericLabel lbl = labels.get(ply);
					if(lbl == null) {
						lbl = new GenericLabel();
						lbl.setAuto(true);
						lbl.setX(200); lbl.setY(120);
						lbl.setWidth(1); lbl.setHeight(1);
						lbl.setAlign(WidgetAnchor.CENTER_CENTER);
						lbl.setTextColor(new Color(255, 0, 0));
						ply.getMainScreen().attachWidget(plugin, lbl);
						labels.put(ply, lbl);
					}
					lbl.setText(formattedTime);
				} catch(Exception e) { }
			}

			try {
				HashSet<SpoutPlayer> plys = new HashSet<SpoutPlayer>(Arrays.asList(players));
				for(SpoutPlayer ply : new ArrayList<SpoutPlayer>(labels.keySet())) {
					if(!plys.contains(ply)) {
						labels.remove(ply);
					}
				}
			} catch(Exception e) { }
		}

		void removeGUI() {
			for(SpoutPlayer ply : SpoutManager.getOnlinePlayers()) {
				if(labels.containsKey(ply)) {
					ply.getMainScreen().removeWidget(labels.get(ply));
				}
			}
		}

		@Override
		public void run() {
			long timeleft = (endtime - System.currentTimeMillis()) / 1000;
			if(timeleft == lasttimeleft) return;
			lasttimeleft = timeleft;
			refreshGUI(timeleft);
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
