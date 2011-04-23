package de.doridian.yiffbukkit.commands;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.NibbleArray;
import net.minecraft.server.Packet51MapChunk;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftWorld;
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
	private Set<ChunkCoordIntPair> dirtyChunks = new HashSet<ChunkCoordIntPair>();

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

		final boolean fanOut = booleanFlags.contains('f');
		final boolean doSkyLight = booleanFlags.contains('s');

		dirtyChunks.clear();
		for (BlockVector bv : selected) {
			Block block = world.getBlockAt(bv.getBlockX(), bv.getBlockY(), bv.getBlockZ());
			lightBlock(block, maxAmount, doSkyLight);
		}
		if (fanOut) {
			final Vector min = selected.getMinimumPoint();
			final Vector max = selected.getMaximumPoint();
			final int minX = min.getBlockX();
			final int minY = min.getBlockY();
			final int minZ = min.getBlockZ();
			final int maxX = max.getBlockX();
			final int maxY = max.getBlockY();
			final int maxZ = max.getBlockZ();

			final Vector newMin = new Vector(minX-maxAmount, Math.max(0,minY-maxAmount), minZ-maxAmount);
			final Vector newMax = new Vector(maxX+maxAmount, Math.min(127,maxY+maxAmount), maxZ+maxAmount);
			CuboidRegion expanded = new CuboidRegion(newMin, newMax);
			for (BlockVector bv : expanded) {
				if (selected.contains(bv))
					continue;

				int x = bv.getBlockX();
				int y = bv.getBlockY();
				int z = bv.getBlockZ();

				int falloff = 0;

				if (x < minX)
					falloff += minX-x;
				else if (x > maxX)
					falloff += x-maxX;

				if (y < minY)
					falloff += minY-y;
				else if (y > maxY)
					falloff += y-maxY;

				if (z < minZ)
					falloff += minZ-z;
				else if (z > maxZ)
					falloff += z-maxZ;

				if (falloff > maxAmount)
					continue;

				Block block = world.getBlockAt(bv.getBlockX(), bv.getBlockY(), bv.getBlockZ());
				lightBlock(block, maxAmount - falloff, doSkyLight);
			}
		}

		WorldServer worldServer = ((CraftWorld)world).getHandle();
		
		for (ChunkCoordIntPair chunk : dirtyChunks) {
			int x = chunk.a*16;
			int z = chunk.b*16;
			Packet51MapChunk p51 = new Packet51MapChunk(x, 0, z, 16, 128, 16, worldServer);
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				if (!player.getWorld().equals(world))
					continue;

				Location location = player.getLocation();

				if (Math.abs(x-location.getX()) > 192)
					continue;

				if (Math.abs(z-location.getZ()) > 192)
					continue;

				playerHelper.sendPacketToPlayer(player, p51);
			}
		}

		playerHelper.SendDirectedMessage(ply, "Lit the region.");
	}

	private void lightBlock(Block block, int lightLevel,
			final boolean doSkyLight) {
		CraftChunk craftChunk = (CraftChunk) block.getChunk();
		Chunk chunk = craftChunk.getHandle();

		dirtyChunks.add(new ChunkCoordIntPair(chunk.j, chunk.k));

		final int x = block.getX() - chunk.j*16;
		final int y = block.getY();
		final int z = block.getZ() - chunk.k*16;

		final NibbleArray nibbleArray;
		if (doSkyLight) {
			nibbleArray = chunk.f;
		} else
			nibbleArray = chunk.g;

		nibbleArray.a(x, y, z, Math.max(lightLevel, nibbleArray.a(x, y, z)));
	}
}
