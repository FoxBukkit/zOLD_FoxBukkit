package de.doridian.yiffbukkit.main.offlinebukkit;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.DoubleTag;
import com.sk89q.jnbt.FloatTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.Tag;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class OfflinePlayer extends AbstractPlayer {
	private Vector velocity;
	private Location location;
	private String displayName;
	private World world;

	public OfflinePlayer(Server server, String name) {
		this(server, PlayerHelper.getPlayerFile(name, "world"), name);
	}

	private OfflinePlayer(Server server, File playerFile, String name) {
		super((CraftServer) server, getCaseCorrectName(playerFile, name));

		world = server.getWorld("world"); // default value
		location = world.getSpawnLocation(); // default value

		YiffBukkit.instance.playerHelper.setPlayerDisplayName(this);

		if (playerFile == null)
			return;

		try {
			final NBTInputStream nbtis = new NBTInputStream(new GZIPInputStream(new FileInputStream(playerFile)));
			final CompoundTag root = (CompoundTag) nbtis.readTag();
			final Map<String,Tag> rootMap = root.getValue();

			// Read velocity
			velocity = listTagToVector(rootMap.get("Motion"));

			// Find world
			final int dimension = ((IntTag) rootMap.get("Dimension")).getValue();
			world = ((CraftServer) server).getServer().getWorldServer(dimension).getWorld();

			// Read rotation values
			@SuppressWarnings("unchecked")
			final List<FloatTag> rotationTagList = (List<FloatTag>) rootMap.get("Rotation").getValue();
			final float yaw = rotationTagList.get(0).getValue();
			final float pitch = rotationTagList.get(1).getValue();

			// Read position values and combine with the world and rotation
			location = listTagToVector(rootMap.get("Pos")).toLocation(world, yaw, pitch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getCaseCorrectName(File playerFile, String name) {
		if (playerFile == null)
			return name;

		final String playerFileName = playerFile.getName();
		// Correct the case of the player name, if the player has been online before.
		return playerFileName.substring(0, playerFileName.length() - 4);
	}

	private static Vector listTagToVector(Tag tag) {
		@SuppressWarnings("unchecked")
		final List<DoubleTag> value = (List<DoubleTag>) tag.getValue();
		return new Vector(
				value.get(0).getValue(),
				value.get(1).getValue(),
				value.get(2).getValue()
		);
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
		return -1;
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

	@Override
	public Vector getVelocity() {
		return velocity;
	}
}
