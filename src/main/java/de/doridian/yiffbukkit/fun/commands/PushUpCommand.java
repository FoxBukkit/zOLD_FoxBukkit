package de.doridian.yiffbukkit.fun.commands;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.Region;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import net.minecraft.server.EntityFallingBlock;
import net.minecraft.server.WorldServer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Names("pushup")
@Help("Pushes the selected region up, if it's sand or gravel.")
@Permission("yiffbukkit.fun.pushup")
public class PushUpCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);
		LocalSession session = plugin.worldEdit.getSession(ply);

		World world = ply.getWorld();

		final Region selected;
		try {
			selected = session.getSelection(BukkitUtil.getLocalWorld(world));
		}
		catch (IncompleteRegionException e) {
			throw new YiffBukkitCommandException("Please select a region.", e);
		}

		Map<BlockVector2D, Integer> heightMap = new HashMap<BlockVector2D, Integer>();

		for (BlockVector pos : selected) {
			final int x = pos.getBlockX();
			final int y = pos.getBlockY();
			final int z = pos.getBlockZ();

			if (world.getBlockTypeIdAt(x, y, z) == 0)
				continue;

			final BlockVector2D key = new BlockVector2D(x, z);

			Integer oldValue = heightMap.get(key);
			if (oldValue == null)
				oldValue = Integer.MIN_VALUE;

			final int newValue = y;
			if (newValue <= oldValue)
				continue;

			heightMap.put(key, newValue);
		}

		for (Entry<BlockVector2D, Integer> entry : heightMap.entrySet()) {
			BlockVector2D xz = entry.getKey();
			pushUp(world, xz.getBlockX(), entry.getValue(), xz.getBlockZ());
		}
	}

	private static void pushUp(World world, int x, int y, int z) {
		final Block block = world.getBlockAt(x, y, z);

		final int typeId = block.getTypeId();
		switch (typeId) {
		case 12: // SAND
		case 13: // GRAVEL
			break;

		default:
			return;
		}

		block.setTypeIdAndData(0, (byte) 0, true);

		final WorldServer notchWorld = ((CraftWorld) world).getHandle();

		final EntityFallingBlock notchEntity = new EntityFallingBlock(notchWorld, x + 0.5, y + 0.5, z + 0.5, typeId, block.getData());
		notchWorld.addEntity(notchEntity);

		final Entity entity = notchEntity.getBukkitEntity();

		entity.setVelocity(new Vector(0, 1, 0));
	}
}
