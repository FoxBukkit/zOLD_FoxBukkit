package de.doridian.yiffbukkit.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

import java.util.LinkedList;

@Names("back")
@Help("Teleports back specified number of steps")
@Usage("[steps]")
@Permission("yiffbukkit.teleport.basic.back")
@StringFlags("t")
public class BackCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
        int numSteps = 1;
        if(args.length > 0) {
            numSteps = Integer.parseInt(args[0]);
        }

        LinkedList<Location> teleports = plugin.playerListener.teleportHistory.get(ply.getName().toLowerCase());

        Location goTo = null;
        int curStep = 0;
        for(; curStep < numSteps; curStep++) {
            if(teleports.size() == 0) break;
            goTo = teleports.pollFirst();
        }

        if(goTo == null) {
            playerHelper.sendDirectedMessage(ply, "No teleport history found!");
            return;
        }

        ply.teleport(goTo);

		playerHelper.sendDirectedMessage(ply, "Teleported back \u00a79"+curStep+"\u00a7f step(s).");
	}
}
