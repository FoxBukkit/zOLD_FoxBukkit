package de.doridian.yiffbukkit.teleportation.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.StringFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedList;

@Names("back")
@Help("Teleports back specified number of steps")
@Usage("[steps]")
@Permission("yiffbukkit.teleport.basic.back")
@StringFlags("t")
public class BackCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if(plugin.jailEngine.isJailed(ply)) {
			PlayerHelper.sendDirectedMessage(ply, "You are jailed!");
			return;
		}

		int numSteps = 1;
		if(args.length > 0) {
			numSteps = Integer.parseInt(args[0]);
		}

		LinkedList<Location> teleports = plugin.playerHelper.teleportHistory.get(ply.getName().toLowerCase());
		if(teleports == null) {
			PlayerHelper.sendDirectedMessage(ply, "No teleport history found!");
			return;
		}

		Location goTo = null;
		int curStep = 0;
		for(; curStep < numSteps; curStep++) {
			if(teleports.size() == 0) break;
			goTo = teleports.pollFirst();
		}

		if(goTo == null) {
			PlayerHelper.sendDirectedMessage(ply, "No teleport history found!");
			return;
		}

		ply.teleport(goTo);

		PlayerHelper.sendDirectedMessage(ply, "Teleported back \u00a79"+curStep+"\u00a7f step(s).");
	}
}
