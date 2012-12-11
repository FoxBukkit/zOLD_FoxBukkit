package de.doridian.yiffbukkit.jail;

import de.doridian.yiffbukkit.main.util.Ini;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JailDescriptor {
	private final World world;
	private final Vector position;
	private final double sizeX, sizeZ;

	public JailDescriptor(World world, Vector pos1, Vector pos2) {
		this.world = world;
		position = pos1.clone().add(new Vector(0.5, 0, 0.5));
		Vector size = pos2.clone().subtract(pos1);
		sizeX = size.getX();
		sizeZ = size.getZ();
	}

	public JailDescriptor(Map<String, List<String>> section) {
		world = Ini.loadWorld(section, "%s");
		position = Ini.loadVector(section, "position%s");
		Vector size = Ini.loadVector(section, "size%s");
		sizeX = size.getX();
		sizeZ = size.getZ();
	}

	public Map<String, List<String>> save() {
		Map<String, List<String>> section = new TreeMap<String, List<String>>();

		Ini.saveWorld(section, "%s", world);
		Ini.saveVector(section, "position%s", position);
		Ini.saveVector(section, "size%s", new Vector(sizeX, 0, sizeZ));

		return section;
	}

	public void jailPlayer(Player ply) {
		Vector vector = position.clone().add(new Vector(sizeX*Math.random(), 0, sizeZ*Math.random()));
		Location location = vector.toLocation(world);
		ply.teleport(location);
	}

	public Vector getCenter() {
		return position.clone().add(new Vector(sizeX*0.5, 0, sizeZ*0.5));
	}

	public World getWorld() {
		return world;
	}
}
