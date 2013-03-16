package de.doridian.yiffbukkit.advanced;

import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.World;

import java.util.HashMap;

public class LayerLinker {
	static {
		links = new HashMap<WorldAndY, WorldAndY>();
		addLayerLink(YiffBukkit.instance.getOrCreateWorld("world"), 254, YiffBukkit.instance.getOrCreateWorld("biosphere"), 1);
	}

	public static class WorldAndY {
		public final World world;
		public final int y;

		private WorldAndY(World world, int y) {
			this.world = world;
			this.y = y;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			WorldAndY worldAndY = (WorldAndY) o;

			if (y != worldAndY.y) return false;
			if (!world.equals(worldAndY.world)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = world.hashCode();
			result = 31 * result + y;
			return result;
		}
	}

	private static final HashMap<WorldAndY, WorldAndY> links;

	public static void addLayerLink(World world1, int y1, World world2, int y2) {
		WorldAndY way1 = new WorldAndY(world1, y1);
		if(links.containsKey(way1)) {
			removeLayerLink(world1, y1);
		}
		WorldAndY way2 = new WorldAndY(world2, y2);
		links.put(way1, way2);
		links.put(way2, way1);
	}

	public static void removeLayerLink(World world1, int y1) {
		links.remove(links.remove(new WorldAndY(world1, y1)));
	}

	public static WorldAndY getLinkedPoint(World world, int y) {
		return links.get(new WorldAndY(world, y));
	}
}
