package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.entity.Player;

import java.util.Set;

@Names("sethome")
@Help("Sets your home position (see /home)")
@ICommand.BooleanFlags("ld")
@ICommand.Usage("[-ld] [name]")
@Permission("yiffbukkit.teleport.basic.sethome")
public class SetHomeCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		String homeName = "default";
		if(args.length > 0) {
			homeName = args[0];
		}

		if(booleanFlags.contains('l')) {
			Set<String> homeNames = playerHelper.getPlayerHomePositionNames(ply);
			String[] homeNamesA = homeNames.toArray(new String[homeNames.size()]);
			PlayerHelper.sendDirectedMessage(ply, "Home locations [" + ((homeNamesA.length > 0) ? (homeNamesA.length - 1) : 0) + "/" + playerHelper.getPlayerHomePositionLimit(ply.getName()) + "]: " + Utils.concatArray(homeNamesA, 0, ""));
			return;
		} else if(booleanFlags.contains('d')) {
			playerHelper.setPlayerHomePosition(ply, homeName, null);
			PlayerHelper.sendDirectedMessage(ply, "Home [" + homeName + "] deleted!");
			return;
		}

		playerHelper.setPlayerHomePosition(ply, homeName, ply.getLocation());
		PlayerHelper.sendDirectedMessage(ply, "Home [" + homeName + "] saved!");
	}
}