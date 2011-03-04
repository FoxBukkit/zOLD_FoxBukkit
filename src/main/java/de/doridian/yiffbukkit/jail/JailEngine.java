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
	private List<JailDescriptor> jails = new ArrayList<JailDescriptor>();
	private Map<String, Location> inmates = new HashMap<String, Location>();

	public JailEngine() {
		LoadJails();
	}

	public void LoadJails() {
	}

	public void SaveJails() {
	}

	public boolean isJailed(Player ply) {
		return inmates.containsKey(ply.getName());
	}
	
	public void jailPlayer(Player ply, boolean jailed) {
		if (jailed) {
			int index = (int) Math.floor(Math.random() * jails.size());

			inmates.put(ply.getName(), ply.getLocation());
			jails.get(index).jailPlayer(ply);
		}
		else {
			Location previousLocation = inmates.remove(ply.getName());
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
