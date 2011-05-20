package de.doridian.yiffbukkit.commands;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.offlinebukkit.OfflinePlayer;
import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.util.PlayerHelper;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({ "who", "list" })
@Help("Prints user list if used without parameters or information about the specified user")
@Usage("[name]")
@Level(0)
public class WhoCommand extends ICommand {
	@Override
	public void run(final CommandSender commandSender, String[] args, String argStr) throws PlayerFindException {
		if(args.length > 0) {
			Matcher matcher = Pattern.compile("^\"(.*)\"$").matcher(args[0]);

			final World world;
			if (commandSender instanceof Player)
				world = ((Player)commandSender).getWorld();
			else
				world = plugin.GetOrCreateWorld("world", Environment.NORMAL);

			final Player target = matcher.matches() ? new OfflinePlayer(plugin.getServer(), world, matcher.group(1)) : playerHelper.MatchPlayerSingle(args[0]);

			playerHelper.SendDirectedMessage(commandSender, "Name: " + target.getName());
			playerHelper.SendDirectedMessage(commandSender, "Rank: " + playerHelper.GetPlayerRank(target));
			playerHelper.SendDirectedMessage(commandSender, "NameTag: " + playerHelper.GetFullPlayerName(target));
			playerHelper.SendDirectedMessage(commandSender, "World: " + target.getWorld().getName());

			int playerLevel = playerHelper.GetPlayerLevel(commandSender);
			if (playerLevel < 2) return;
			playerHelper.SendDirectedMessage(commandSender, "Last logout: " + Utils.readableDate(PlayerHelper.lastLogout(target)));

			if (playerLevel < 3) return;
			if (playerLevel < playerHelper.GetPlayerLevel(target)) return;
			playerHelper.SendDirectedMessage(commandSender, "Last logout before backup: " + Utils.readableDate(PlayerHelper.lastLogoutBackup(target)));
			Vector targetPosition = target.getLocation().toVector();
			playerHelper.SendDirectedMessage(commandSender, "Position: " + targetPosition);

			Vector offsetFromSpawn = targetPosition.clone().subtract(world.getSpawnLocation().toVector());
			long unitsFromSpawn = Math.round(offsetFromSpawn.length());
			String directionFromSpawn = Utils.yawToDirection(Utils.vectorToYaw(offsetFromSpawn));

			final String fromYou;
			if (commandSender instanceof Player) {
				Vector offsetFromYou = targetPosition.clone().subtract(((Player)commandSender).getLocation().toVector());
				long unitsFromYou = Math.round(offsetFromYou.length());
				String directionFromYou = Utils.yawToDirection(Utils.vectorToYaw(offsetFromYou));
				fromYou = " and "+unitsFromYou+"m "+directionFromYou+" from you";
			}
			else {
				fromYou = "";
			}

			playerHelper.SendDirectedMessage(commandSender, "That's "+unitsFromSpawn+"m "+directionFromSpawn+" from the spawn"+fromYou+"." );
			if (target.isOnline()) {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						InetAddress address = target.getAddress().getAddress();
						playerHelper.SendDirectedMessage(commandSender, "IP: " + address.getHostAddress() + "(" + address.getCanonicalHostName() + ")");
					}
				});
				thread.start();
			}
		}
		else {
			Player[] players = plugin.getServer().getOnlinePlayers();
			String str = "Online players: " + players[0].getName();
			for(int i=1;i<players.length;i++) {
				str += ", " + players[i].getName();
			}
			playerHelper.SendDirectedMessage(commandSender, str);
		}
	}
}
