package de.doridian.yiffbukkit.permissions.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissions;
import org.bukkit.entity.Player;

@ICommand.Names({"checkoff","co"})
@ICommand.Help("Check-Off list and system for YB")
@ICommand.Usage("[name]")
@ICommand.Permission("yiffbukkit.checkoff")
public class CheckOffCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if(args.length < 1) {
			String[] plys = YiffBukkitPermissions.checkOffPlayers.toArray(new String[YiffBukkitPermissions.checkOffPlayers.size()]);
			StringBuilder reply = new StringBuilder();
			reply.append("\u00a74CO: ");
			for(int i=0;i<plys.length;i++) {
				String plystr = plys[i];
				if(i != 0) {
					reply.append("\u00a7f, ");
				}
				Player plyply = plugin.getServer().getPlayerExact(plystr);
				if(plyply != null && plyply.isOnline()) {
					reply.append("\u00a72");
				} else {
					reply.append("\u00a74");
				}
				reply.append(plystr);
			}
			plugin.playerHelper.sendDirectedMessage(ply, reply.toString());
		} else {
			if(YiffBukkitPermissions.removeCOPlayer(args[0])) {
				plugin.playerHelper.sendDirectedMessage(ply, "Removed player from CO");
			} else {
				plugin.playerHelper.sendDirectedMessage(ply, "Player not found on CO");
			}
		}
	}
}
