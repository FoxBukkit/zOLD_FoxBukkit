package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.Region;

import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("setjail")
@Help("Defines a jail cell from the current WorldEdit selection or removes the cell whose center you're standing closest to.")
@Usage("[remove]")
@Level(4)
public class SetJailCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (argStr.equals("remove")) {
			plugin.jailEngine.removeJail(ply.getLocation());
			playerHelper.sendDirectedMessage(ply, "Removed the jail cell closest to you.");
			return;
		}

		LocalSession session = plugin.worldEdit.getSession(ply);

		try {
			Region selected = session.getSelection(new BukkitWorld(ply.getWorld()));
			com.sk89q.worldedit.Vector pos1 = selected.getMaximumPoint();
			com.sk89q.worldedit.Vector pos2 = selected.getMinimumPoint();
			double y = Math.min(pos1.getY(), pos2.getY())+1;
			plugin.jailEngine.setJail(ply.getWorld(), new Vector(pos1.getX(), y, pos1.getZ()), new Vector(pos2.getX(), y, pos2.getZ()));
			playerHelper.sendDirectedMessage(ply, "Made a jail here.");
		}
		catch (IncompleteRegionException e) {
			throw new YiffBukkitCommandException("Please select a region.", e);
		}
	}
}
