package de.doridian.yiffbukkit.permissions.commands;

import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.PlayerFindException;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
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

			PlayerHelper.sendDirectedMessage(commandSender, "Name: " + target.getName());
			PlayerHelper.sendDirectedMessage(commandSender, "Rank: " + playerHelper.getPlayerRank(target));
			PlayerHelper.sendDirectedMessage(commandSender, "NameTag: " + playerHelper.GetFullPlayerName(target));
			PlayerHelper.sendDirectedMessage(commandSender, "World: " + target.getWorld().getName());

			final int playerLevel = playerHelper.getPlayerLevel(commandSender);
			if (commandSender.hasPermission("yiffbukkit.who.lastlogout")) {
				PlayerHelper.sendDirectedMessage(commandSender, "Last logout: " + Utils.readableDate(PlayerHelper.lastLogout(target)));
			}

			if (commandSender.hasPermission("yiffbukkit.who.lastlogoutbackup")) {
				PlayerHelper.sendDirectedMessage(commandSender, "Last logout before backup: " + Utils.readableDate(PlayerHelper.lastLogoutBackup(target)));
			}

			if (commandSender.hasPermission("yiffbukkit.who.position") && playerLevel >= playerHelper.getPlayerLevel(target)) {
				Vector targetPosition = target.getLocation().toVector();
				PlayerHelper.sendDirectedMessage(commandSender, "Position: " + targetPosition);

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

				PlayerHelper.sendDirectedMessage(commandSender, "That's "+unitsFromSpawn+"m "+directionFromSpawn+" from the spawn"+fromYou+"." );
			}

			if (commandSender.hasPermission("yiffbukkit.who.address") && playerLevel >= playerHelper.getPlayerLevel(target) && target.isOnline()) {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						InetAddress address = target.getAddress().getAddress();
						PlayerHelper.sendDirectedMessage(commandSender, "IP: " + address.getHostAddress() + "(" + address.getCanonicalHostName() + ")");
					}
				});
				thread.start();
			}
		}
		else {
			Player[] players = plugin.getServer().getOnlinePlayers();
			String str = "Online players: ";
			if(players.length > 0) {
				if (commandSender.hasPermission("yiffbukkit.who.ranklevels")) {
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
			PlayerHelper.sendDirectedMessage(commandSender, str);
		}
	}
}
