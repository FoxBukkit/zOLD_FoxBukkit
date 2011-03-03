package de.doridian.yiffbukkit.jail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class JailEngine {
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

	private List<JailDescriptor> jails = new ArrayList<JailDescriptor>();
	private Map<String, Location> jailedPlayers = new HashMap<String, Location>();

	public JailEngine() {
		LoadJails();
	}

	public void LoadJails() {
	}

	public void SaveJails() {
	}

	public boolean isJailed(Player ply) {
		return jailedPlayers.containsKey(ply.getName());
	}
	
	public void jailPlayer(Player ply, boolean jailed) {
		if (jailed) {
			int index = (int) Math.floor(Math.random() * jails.size());

			jailedPlayers.put(ply.getName(), ply.getLocation());
			jails.get(index).jailPlayer(ply);
		}
		else {
			Location previousLocation = jailedPlayers.remove(ply.getName());
			ply.teleportTo(previousLocation);
		}
	}

	public void setJail(World world, Vector pos1, Vector pos2) {
		jails.add(new JailDescriptor(world, pos1, pos2));

		SaveJails();
	}

	public void removeJail(Location location) {
		if (jails.isEmpty())
			return;

		World world = location.getWorld();
		Vector pos = location.toVector();
		int closestIndex = -1;
		double minDistanceSquared = Double.MAX_VALUE; 

		for (int i = 0; i < jails.size(); ++i) {
			JailDescriptor jail = jails.get(i);

			if (!jail.world.equals(world))
				continue;

			double distanceSquared = pos.distanceSquared(jail.center());
			if (distanceSquared >= minDistanceSquared)
				continue;

			minDistanceSquared = distanceSquared;
			closestIndex = i;
		}

		jails.remove(closestIndex);

		SaveJails();
	}
}
