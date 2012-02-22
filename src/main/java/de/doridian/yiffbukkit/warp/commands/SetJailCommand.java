package de.doridian.yiffbukkit.warp.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.Region;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Names("setjail")
@Help("Defines a jail cell from the current WorldEdit selection or removes the cell whose center you're standing closest to.")
@Usage("[remove]")
@Permission("yiffbukkit.jail.setjail")
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
			Region selected = session.getSelection(BukkitUtil.getLocalWorld(ply.getWorld()));
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
