/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbukkit.jail.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.Region;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.jail.JailComponent;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Names("setjail")
@Help("Defines a jail cell from the current WorldEdit selection or removes the cell whose center you're standing closest to.")
@Usage("[remove]")
@Permission("yiffbukkit.jail.setjail")
public class SetJailCommand extends ICommand {
	private final JailComponent jail = (JailComponent) plugin.componentSystem.getComponent("jail");

	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		if (argStr.equals("remove")) {
			jail.engine.removeJail(ply.getLocation());
			PlayerHelper.sendDirectedMessage(ply, "Removed the jail cell closest to you.");
			return;
		}

		LocalSession session = plugin.worldEdit.getSession(ply);

		try {
			Region selected = session.getSelection(BukkitUtil.getLocalWorld(ply.getWorld()));
			com.sk89q.worldedit.Vector pos1 = selected.getMaximumPoint();
			com.sk89q.worldedit.Vector pos2 = selected.getMinimumPoint();
			double y = Math.min(pos1.getY(), pos2.getY())+1;
			jail.engine.setJail(ply.getWorld(), new Vector(pos1.getX(), y, pos1.getZ()), new Vector(pos2.getX(), y, pos2.getZ()));
			PlayerHelper.sendDirectedMessage(ply, "Made a jail here.");
		}
		catch (IncompleteRegionException e) {
			throw new YiffBukkitCommandException("Please select a region.", e);
		}
	}
}
