package de.doridian.yiffbukkit.main.offlinebukkit;

import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;

import java.io.File;

public class OfflinePlayer extends AbstractPlayer {
	private Location location;
	private int entId = -1;
	private String displayName;
	private World world;

	public OfflinePlayer(Server server, String name) {
		super((CraftServer) server, name);
		final File playerFile = PlayerHelper.getPlayerFile(name, "world");
		if (playerFile != null) {
			final String playerFileName = playerFile.getName();
			name = playerFileName.substring(0, playerFileName.length() - 4);
		}

		displayName = name;
		world = server.getWorld("world"); // TEMP!
		location = world.getSpawnLocation(); // TEMP!
		//ServerConfigurationManager confmgr = ((CraftServer)server).getHandle();
		//File worldFile = ((CraftWorld)world).getHandle().u;
		//PlayerNBTManager pnm = confmgr.
		//PlayerNBTManager pnm = new PlayerNBTManager(worldFile , name, false);
		//new File(worldFile, "_tmp_.dat");
	}

	@Override
	public Location getLocation() {
		return location;
	}
	@Override
	public World getWorld() {
		return world;
	}
	@Override
	public int getEntityId() {
		return entId;
	}
	@Override
	public String getDisplayName() {
		return displayName;
	}
	@Override
	public void setDisplayName(String name) {
		displayName = name;
	}

	@Override
	public double getHealthScale() {
		return 1;
	}

	@Override
	public void playEffect(EntityEffect effect) {
		//TODO: Maybe implement?
	}

}
