package de.doridian.yiffbukkit.permissions.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.*;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissions;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.entity.Player;

@Names({"checkoff","co"})
@Help("Check-Off list and system for YB")
@Usage("[[-f] name]")
@BooleanFlags("f")
@Permission("yiffbukkit.checkoff")
public class CheckOffCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);
		if (args.length < 1) {
			final StringBuilder reply = new StringBuilder("\u00a76CO: ");
			boolean first = true;
			for (String playerName : YiffBukkitPermissions.checkOffPlayers) {
				if (!first) {
					reply.append("\u00a7f, ");
				}
				if (isOnline(playerName)) {
					reply.append("\u00a72");
				} else {
					reply.append("\u00a74");
				}
				reply.append(playerName);
				first = false;
			}
			PlayerHelper.sendDirectedMessage(ply, reply.toString());
		} else {
			final String playerName = args[0];
			if (!booleanFlags.contains('f') && isOnline(playerName))
				throw new YiffBukkitCommandException("Cannot check off online player without -f flag.");

			if (YiffBukkitPermissions.removeCOPlayer(playerName)) {
				PlayerHelper.sendDirectedMessage(ply, "Removed player "+playerName+" from CO");
			} else {
				PlayerHelper.sendDirectedMessage(ply, "Player "+playerName+" not found on CO");
			}
		}
	}

	public boolean isOnline(String playerName) {
		Player plyply = plugin.getServer().getPlayerExact(playerName);
		return plyply != null && plyply.isOnline();
	}
}
