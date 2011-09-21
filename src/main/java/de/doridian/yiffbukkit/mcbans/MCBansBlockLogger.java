package de.doridian.yiffbukkit.mcbans;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import de.doridian.yiffbukkit.YiffBukkit;

public abstract class MCBansBlockLogger {
	YiffBukkit plugin;
	public MCBansBlockLogger(YiffBukkit plug) {
		plugin = plug;
	}
	
	public String getFormattedBlockChangesBy(String name, World world, boolean center) {
		HashMap<Location,MCBansBlockChange> changes = getChangedRawBlocks(name, world);
		if(changes.isEmpty()) return "";
		
		StringBuilder ret = new StringBuilder();
		int centerX, centerY, centerZ;
		if(center) {
			int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
			for(MCBansBlockChange change : changes.values()) {
				int x = change.position.getBlockX();
				int y = change.position.getBlockY();
				int z = change.position.getBlockZ();
				if(x < minX) minX = x; if(y < minY) minY = y; if(z < minZ) minZ = z;
				if(x > maxX) maxX = x; if(y > maxY) maxY = y; if(z > maxZ) maxZ = z;
			}
			centerX = (maxX + minX) / 2;
			centerY = (maxY + minY) / 2;
			centerZ = (maxZ + minZ) / 2;
		} else {
			centerX = 0;
			centerY = 0;
			centerZ = 0;
		}
		addSurroundings(changes);
		for(MCBansBlockChange change : changes.values()) {
			ret.append(',');
			ret.append(change.toString(centerX,centerY,centerZ));
		}
		return ret.deleteCharAt(0).toString();
	}
	
	public Collection<MCBansBlockChange> getChangedBlocksBy(String name, World world) {
		return getChangedBlocksBy(name, world, true);
	}
	
	public Collection<MCBansBlockChange> getChangedBlocksBy(String name, World world, boolean surroundings) {
		HashMap<Location,MCBansBlockChange> rawChanges = getChangedRawBlocks(name, world);
		if(surroundings) addSurroundings(rawChanges);
		return rawChanges.values();
	}
	
	protected void addSurroundings(HashMap<Location,MCBansBlockChange> blockSet) {
		@SuppressWarnings("unchecked")
		HashSet<MCBansBlockChange> iterator = (HashSet<MCBansBlockChange>) blockSet.clone();
		for(MCBansBlockChange change : iterator) {
			Location loc = change.position;
			World world = loc.getWorld();
			int bx = loc.getBlockX(); int by = loc.getBlockY(); int bz = loc.getBlockZ();
			for(int x = bx - 5; x <= bx + 5; x++) {
				for(int y = by - 5; y <= by + 5; y++) {
					for(int z = bz - 5; z <= bz + 5; z++) {
						Block block = world.getBlockAt(x, y, z);
						if(block == null || block.getTypeId() == 0) continue;
						MCBansBlockChange tmp = new MCBansBlockChange(block);
						blockSet.put(tmp.position,tmp);
					}
				}
			}
		}
	}
	
	protected abstract HashMap<Location,MCBansBlockChange> getChangedRawBlocks(String name, World world);
}
