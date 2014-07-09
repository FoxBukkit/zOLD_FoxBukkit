/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.main.offlinebukkit;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.DoubleTag;
import com.sk89q.jnbt.FloatTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.Tag;
import com.foxelbox.foxbukkit.core.FoxBukkit;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import org.bukkit.Achievement;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

public class OfflinePlayer extends AbstractPlayer {
	private Vector velocity;
	private Location location;
	private String displayName;
	private World world;

	public OfflinePlayer(Server server, String name) {
		this(server, null, name);
	}

	public OfflinePlayer(Server server, UUID uuid) {
		this(server, uuid, null);
	}

	public void sendSignChange(Location loc, String[] str) {

	}

	@Override
	public void removeAchievement(Achievement achievement) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean hasAchievement(Achievement achievement) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void decrementStatistic(Statistic statistic, int i) throws IllegalArgumentException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setStatistic(Statistic statistic, int i) throws IllegalArgumentException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public int getStatistic(Statistic statistic) throws IllegalArgumentException {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void decrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void incrementStatistic(Statistic statistic, EntityType entityType, int i) throws IllegalArgumentException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void decrementStatistic(Statistic statistic, EntityType entityType, int i) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setStatistic(Statistic statistic, EntityType entityType, int i) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public OfflinePlayer(Server server, UUID uuid, String name) {
		super((CraftServer) server, uuid, name);

		File playerFile = PlayerHelper.getPlayerFile(this.getUniqueId(), "world");

		world = server.getWorld("world"); // default value
		location = world.getSpawnLocation(); // default value

		FoxBukkit.instance.playerHelper.setPlayerDisplayName(this);

		if (playerFile == null || !playerFile.exists())
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
