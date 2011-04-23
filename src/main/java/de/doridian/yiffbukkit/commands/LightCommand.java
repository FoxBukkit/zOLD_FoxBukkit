package de.doridian.yiffbukkit.commands;

import net.minecraft.server.Chunk;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("light")
@Help(
		"Sets a region to full skylight or the selected level (experimental).\n" +
		"Flags:" +
		"  -f Fan out light into adjacent blocks"
)
@Usage("[-f] [<level>]")
@Level(4)
@BooleanFlags("f")
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

		CuboidRegion current = (CuboidRegion)selected;
		CuboidRegion previous = null;
		for (int amount = maxAmount; amount >= minAmount; --amount) {
			for (BlockVector bv : current) {
				if (previous != null && previous.contains(bv))
					continue;

				Block block = world.getBlockAt(bv.getBlockX(), bv.getBlockY(), bv.getBlockZ());
				CraftChunk craftChunk = (CraftChunk) block.getChunk();
				Chunk chunk = craftChunk.getHandle();

				final int chunkX = craftChunk.getX();
				final int chunkZ = craftChunk.getZ();

				int x = bv.getBlockX();
				int y = bv.getBlockY();
				int z = bv.getBlockZ();
				x -= chunkX*16;
				z -= chunkZ*16;

				chunk.f.a(x, y, z, amount);
			}
			previous = current;
			current = new CuboidRegion(previous.getMinimumPoint().subtract(1,0,1), previous.getMaximumPoint().add(1,0,1));
		}
		playerHelper.SendDirectedMessage(ply, "Lit the region.");
	}
}
