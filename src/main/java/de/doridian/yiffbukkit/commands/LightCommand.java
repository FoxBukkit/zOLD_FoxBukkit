package de.doridian.yiffbukkit.commands;

import net.minecraft.server.Chunk;
import net.minecraft.server.NibbleArray;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("light")
@Help(
		"Sets a region to full light or the selected level (experimental).\n" +
		"Flags:\n" +
		"  -f Fan out light into adjacent blocks\n" +
		"  -s Set skylight instead of blocklight"
)
@Usage("[<flags>] [<level>]")
@Level(4)
@BooleanFlags("fs")
public class LightCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);
		LocalSession session = plugin.worldEdit.getSession(ply);

		World world = ply.getWorld();

		final Region selected;
		try {
			selected = session.getSelection(new BukkitWorld(ply.getWorld()));
		}
		catch (IncompleteRegionException e) {
			throw new YiffBukkitCommandException("Please select a region.", e);
		}

		if (!(selected instanceof CuboidRegion))
			throw new YiffBukkitCommandException("Please select a cuboid region.");

		int maxAmount;
		if (args.length == 0) {
			maxAmount = 15;
		}
		else {
			try {
				maxAmount = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				throw new YiffBukkitCommandException("Number or nothing expected.", e);
			}
		}

		final int minAmount = booleanFlags.contains('f') ? 0 : maxAmount;
		final boolean doSkyLight = booleanFlags.contains('s');

		CuboidRegion current = (CuboidRegion)selected;
		CuboidRegion previous = null;
		for (int amount = maxAmount; amount >= minAmount; --amount) {
			for (BlockVector bv : current) {
				if (previous != null && previous.contains(bv))
					continue;

				Block block = world.getBlockAt(bv.getBlockX(), bv.getBlockY(), bv.getBlockZ());
				CraftChunk craftChunk = (CraftChunk) block.getChunk();
				Chunk chunk = craftChunk.getHandle();

				final int x = bv.getBlockX() - craftChunk.getX()*16;
				final int y = bv.getBlockY();
				final int z = bv.getBlockZ() - craftChunk.getZ()*16;

				final NibbleArray nibbleArray;
				if (doSkyLight) {
					nibbleArray = chunk.f;
				} else
					nibbleArray = chunk.g;

				nibbleArray.a(x, y, z, Math.max(amount, nibbleArray.a(x, y, z)));
			}
			previous = current;
			final Vector min = previous.getMinimumPoint().subtract(1,0,1);
			final Vector max = previous.getMaximumPoint().add(1,0,1);
			if (min.getY() > 0)
				min.setY(min.getY()-1);
			if (max.getY() < 127)
				max.setY(max.getY()+1);
			current = new CuboidRegion(min, max);
		}
		playerHelper.SendDirectedMessage(ply, "Lit the region.");
	}
}
