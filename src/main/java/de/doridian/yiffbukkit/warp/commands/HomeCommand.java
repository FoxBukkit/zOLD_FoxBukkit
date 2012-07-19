package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.entity.Player;

import java.util.Set;

@Names("home")
@ICommand.Usage("[-l] [name]")
@Help("Teleports you to your home position (see /sethome)")
@ICommand.BooleanFlags("l")
@Permission("yiffbukkit.teleport.basic.home")
public class HomeCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		if (plugin.jailEngine.isJailed(ply)) {
			PlayerHelper.sendDirectedMessage(ply, "You are jailed!");
			return;
		}

		String homeName = "default";
		if(args.length > 0) {
			homeName = args[0];
		}

		if(booleanFlags.contains('l')) {
			Set<String> homeNames = playerHelper.getPlayerHomePositionNames(ply);
			String[] homeNamesA = homeNames.toArray(new String[homeNames.size()]);
			PlayerHelper.sendDirectedMessage(ply, "Home locations [" + ((homeNamesA.length > 0) ? (homeNamesA.length - 1) : 0) + "/" + playerHelper.getPlayerHomePositionLimit(ply.getName()) + "]: " + Utils.concatArray(homeNamesA, 0, ""));
			return;
		}

		plugin.playerHelper.teleportWithHistory(ply, playerHelper.getPlayerHomePosition(ply, homeName));
		PlayerHelper.sendDirectedMessage(ply, "You went to your home [" + homeName + "]");
	}
}
