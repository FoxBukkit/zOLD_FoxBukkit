/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.fun.commands;

import com.google.common.collect.Sets;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.Region;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.AbusePotential;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.util.Utils;
import net.minecraft.server.v1_7_R4.EntityFallingBlock;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Names("pushup")
@Help("Pushes the selected region up.")
@Permission("foxbukkit.fun.pushup")
@AbusePotential
public class PushUpCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		args = parseFlags(args);
		final LocalSession session = plugin.worldEdit.getSession(ply);

		final double speed = Double.parseDouble(args[0]);

		final World world = ply.getWorld();

		final Region selected;
		try {
			selected = session.getSelection(BukkitUtil.getLocalWorld(world));
		}
		catch (IncompleteRegionException e) {
			throw new FoxBukkitCommandException("Please select a region.", e);
		}

		for (BlockVector pos : Sets.newTreeSet(selected)) {
			final int x = pos.getBlockX();
			final int y = pos.getBlockY();
			final int z = pos.getBlockZ();

			if (world.getBlockTypeIdAt(x, y, z) == 0)
				continue;

			pushUp(world, x, y, z, speed);
		}
	}

	private static void pushUp(World world, int x, int y, int z, double speed) {
		if (speed <= 0) {
			final Block blockBelow = world.getBlockAt(x, y - 1, z);
			if (!BlockType.canPassThrough(blockBelow.getTypeId(), blockBelow.getData()))
				return;
		}

		final Block block = world.getBlockAt(x, y, z);

		final int typeId = block.getTypeId();
		final byte data = block.getData();

		block.setTypeIdAndData(0, (byte) 0, true);

		final EntityFallingBlock notchEntity = Utils.spawnFallingBlock(block.getLocation().add(0.5, 0.5, 0.5), typeId, data);
		final Entity entity = notchEntity.getBukkitEntity();

		entity.setVelocity(new Vector(0, speed, 0));
	}
}
