package de.doridian.yiffbukkit.commands;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.YiffBukkitPlayerListener;
import de.doridian.yiffbukkit.offlinebukkit.OfflinePlayer;
import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.util.Utils;

public class WhoCommand extends ICommand {

	public int GetMinLevel() {
		return 0;
	}

	public WhoCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	public void Run(final Player ply, String[] args, String argStr) throws PlayerFindException {
		if(args.length > 0) {
			Matcher matcher = Pattern.compile("^\"(.*)\"$").matcher(args[0]);

			final Player target = matcher.matches() ? new OfflinePlayer(plugin.getServer(), ply.getWorld(), matcher.group(1)) : playerHelper.MatchPlayerSingle(args[0]);

			playerHelper.SendDirectedMessage(ply, "Name: " + target.getName());
			playerHelper.SendDirectedMessage(ply, "Rank: " + playerHelper.GetPlayerRank(target));
			playerHelper.SendDirectedMessage(ply, "NameTag: " + playerHelper.GetFullPlayerName(target));
			playerHelper.SendDirectedMessage(ply, "World: " + target.getWorld().getName());

			int playerLevel = playerHelper.GetPlayerLevel(ply);
			if (playerLevel < 2) return;
			playerHelper.SendDirectedMessage(ply, "Last logout: " + playerHelper.lastLogout(target));
			
			if (playerLevel < 3) return;
			if (playerLevel < playerHelper.GetPlayerLevel(target)) return;
			playerHelper.SendDirectedMessage(ply, "Last logout before backup: " + playerHelper.lastLogoutBackup(target));
			Vector targetPosition = target.getLocation().toVector();
			playerHelper.SendDirectedMessage(ply, "Position: " + targetPosition);
			Vector offsetFromYou = targetPosition.clone().subtract(ply.getLocation().toVector());
			Vector offsetFromSpawn = targetPosition.clone().subtract(ply.getWorld().getSpawnLocation().toVector());

			long unitsFromYou = Math.round(offsetFromYou.length());
			long unitsFromSpawn = Math.round(offsetFromSpawn.length());
			String directionFromYou = Utils.yawToDirection(Utils.vectorToYaw(offsetFromYou));
			String directionFromSpawn = Utils.yawToDirection(Utils.vectorToYaw(offsetFromSpawn));

			playerHelper.SendDirectedMessage(ply,
					"That's "+unitsFromSpawn+"m "+directionFromSpawn+" from the spawn "+
					"and "+unitsFromYou+"m "+directionFromYou+" from you."
			);
			if (target.isOnline()) {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						InetAddress address = target.getAddress().getAddress();
						playerHelper.SendDirectedMessage(ply, "IP: " + address.getHostAddress() + "(" + address.getCanonicalHostName() + ")");
					}
				});
				thread.start();
			}
		} else {
			Player[] players = plugin.getServer().getOnlinePlayers();
			String str = "Online players: " + players[0].getName();
			for(int i=1;i<players.length;i++) {
				str += ", " + players[i].getName();
			}
			playerHelper.SendDirectedMessage(ply, str);
		}
	}

	public String GetHelp() {
		return "Prints user list if used without parameters or information about the specified user";
	}

	public String GetUsage() {
		return "[name]";
	}
}
