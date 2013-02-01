package de.doridian.yiffbukkit.fun.commands;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.Region;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import net.minecraft.server.v1_4_R1.v1_4_R1.EntityFallingBlock;
import net.minecraft.server.v1_4_R1.v1_4_R1.WorldServer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_4_R1.v1_4_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Names("pushup")
@Help("Pushes the selected region up, if it's sand or gravel.")
@Permission("yiffbukkit.fun.pushup")
public class PushUpCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);
		LocalSession session = plugin.worldEdit.getSession(ply);

		double speed = Double.parseDouble(args[0]);

		World world = ply.getWorld();

		final Region selected;
		try {
			selected = session.getSelection(BukkitUtil.getLocalWorld(world));
		}
		catch (IncompleteRegionException e) {
			throw new YiffBukkitCommandException("Please select a region.", e);
		}

		for (BlockVector pos : selected) {
			final int x = pos.getBlockX();
			final int y = pos.getBlockY();
			final int z = pos.getBlockZ();

			if (world.getBlockTypeIdAt(x, y, z) == 0)
				continue;

			pushUp(world, x, y, z, speed);
		}
	}

	private static void pushUp(World world, int x, int y, int z, double speed) {
		final Block block = world.getBlockAt(x, y, z);

		final int typeId = block.getTypeId();
		final byte data = block.getData();

		block.setTypeIdAndData(0, (byte) 0, true);

		final WorldServer notchWorld = ((CraftWorld) world).getHandle();

		final EntityFallingBlock notchEntity = new EntityFallingBlock(notchWorld, x + 0.5, y + 0.5, z + 0.5, typeId, data);

		// This disables the first tick code, which takes care of removing the original block etc.
		notchEntity.c = 1;

		// Do not drop an item if placing a block fails
		notchEntity.dropItem = false;

		notchWorld.addEntity(notchEntity);

		final Entity entity = notchEntity.getBukkitEntity();

		entity.setVelocity(new Vector(0, speed, 0));
	}
}
