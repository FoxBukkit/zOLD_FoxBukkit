package de.doridian.yiffbukkit.jail;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class JailDescriptor {
	public JailDescriptor(World world, Vector pos1, Vector pos2) {
		this.world = world;
		position = pos1.clone().add(new Vector(0.5, 0, 0.5));
		size = pos2.clone().subtract(pos1);
		System.out.println(position);
		System.out.println(size);
	}

	World world;
	Vector position, size;

	public void jailPlayer(Player ply) {
		Vector vector = position.clone().add(size.clone().multiply(Math.random()));
		Location location = vector.toLocation(world);
		ply.teleportTo(location);
	}

	public Vector center() {
		return position.clone().add(size.clone().multiply(0.5));
	}

}