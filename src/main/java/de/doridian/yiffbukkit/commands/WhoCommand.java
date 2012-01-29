package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.commands.ICommand.Usage;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissionHandler;
import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.util.PlayerHelper;
import de.doridian.yiffbukkit.util.Utils;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.net.InetAddress;

@Names({ "who", "list" })
@Help("Prints user list if used without parameters or information about the specified user")
@Usage("[name]")
@Permission("yiffbukkit.who")
public class WhoCommand extends ICommand {
	@Override
	public void run(final CommandSender commandSender, String[] args, String argStr) throws PlayerFindException {
		if(args.length > 0) {
			final World world;
			if (commandSender instanceof Player)
				world = ((Player)commandSender).getWorld();
			else
				world = plugin.getOrCreateWorld("world", Environment.NORMAL);

			final Player target = playerHelper.matchPlayerSingle(args[0], false);

			playerHelper.sendDirectedMessage(commandSender, "Name: " + target.getName());
			playerHelper.sendDirectedMessage(commandSender, "Rank: " + playerHelper.getPlayerRank(target));
			playerHelper.sendDirectedMessage(commandSender, "NameTag: " + playerHelper.GetFullPlayerName(target));
			playerHelper.sendDirectedMessage(commandSender, "World: " + target.getWorld().getName());

			final int playerLevel = playerHelper.getPlayerLevel(commandSender);
			final YiffBukkitPermissionHandler permissionHandler = plugin.permissionHandler;
			if (permissionHandler.has(commandSender, "yiffbukkit.who.lastlogout")) {
				playerHelper.sendDirectedMessage(commandSender, "Last logout: " + Utils.readableDate(PlayerHelper.lastLogout(target)));
			}

			if (permissionHandler.has(commandSender, "yiffbukkit.who.lastlogoutbackup")) {
				playerHelper.sendDirectedMessage(commandSender, "Last logout before backup: " + Utils.readableDate(PlayerHelper.lastLogoutBackup(target)));
			}

			if (permissionHandler.has(commandSender, "yiffbukkit.who.position") && playerLevel >= playerHelper.getPlayerLevel(target)) {
				Vector targetPosition = target.getLocation().toVector();
				playerHelper.sendDirectedMessage(commandSender, "Position: " + targetPosition);

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

				playerHelper.sendDirectedMessage(commandSender, "That's "+unitsFromSpawn+"m "+directionFromSpawn+" from the spawn"+fromYou+"." );
			}

			if (permissionHandler.has(commandSender, "yiffbukkit.who.address") && playerLevel >= playerHelper.getPlayerLevel(target) && target.isOnline()) {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						InetAddress address = target.getAddress().getAddress();
						playerHelper.sendDirectedMessage(commandSender, "IP: " + address.getHostAddress() + "(" + address.getCanonicalHostName() + ")");
					}
				});
				thread.start();
			}
		}
		else {
			Player[] players = plugin.getServer().getOnlinePlayers();
			String str = "Online players: ";
			if(players.length > 0) {
				if (plugin.permissionHandler.has(commandSender, "yiffbukkit.who.ranklevels")) {
					str += playerHelper.formatPlayer(players[0]);
					for(int i=1;i<players.length;i++) {
						str += ", " + playerHelper.formatPlayer(players[i]);
					}
				}
				else {
					str += players[0].getName();
					for(int i=1;i<players.length;i++) {
						str += ", " + players[i].getName();
					}
				}
			}
			playerHelper.sendDirectedMessage(commandSender, str);
		}
	}
}
