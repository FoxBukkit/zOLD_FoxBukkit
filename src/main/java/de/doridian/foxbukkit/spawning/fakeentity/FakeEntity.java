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
package de.doridian.foxbukkit.spawning.fakeentity;

import de.doridian.foxbukkit.core.FoxBukkit;
import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.util.Utils;
import net.minecraft.server.v1_7_R3.DataWatcher;
import net.minecraft.server.v1_7_R3.ItemStack;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityMetadata;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public abstract class FakeEntity extends AbstractEntity {
	static int lastFakeEntityId = 1000000000;

	public final int entityId;
	public Location location;
	private boolean isDead;

	private DataWatcher datawatcher = Utils.createEmptyDataWatcher();

	@Override
	public boolean isOnGround() {
		return true;
	}

	public FakeEntity(Location location) {
		entityId = ++lastFakeEntityId;
		this.location = location;
	}

	public void send() {
		for (Player player : location.getWorld().getPlayers()) {
			send(player);
		}
	}

	abstract public void send(Player player);

	private void delete() {
		for (Player player : location.getWorld().getPlayers()) {
			delete(player);
		}
	}

	private void delete(Player player) {
		PlayerHelper.sendPacketToPlayer(player, new PacketPlayOutEntityDestroy(entityId));
	}

	@Override
	public void setVelocity(Vector velocity) {
		for (Player player : location.getWorld().getPlayers()) {
			PlayerHelper.sendPacketToPlayer(player, new PacketPlayOutEntityVelocity(entityId, velocity.getX(), velocity.getY(), velocity.getZ()));
		}
	}

	@Override
	public boolean teleport(Location location) {
		this.location = location;
		for (Player player : location.getWorld().getPlayers()) {
			PlayerHelper.sendPacketToPlayer(player, new PacketPlayOutEntityTeleport(entityId, MathHelper.floor(location.getX()*32.0D), MathHelper.floor(location.getY()*32.0D), MathHelper.floor(location.getZ()*32.0D), (byte)0, (byte)0));
		}
		return true;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public World getWorld() {
		return location.getWorld();
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		return null;
		/*
		EntityPlayer entity = new EntityPlayer(null, null, null, null);
		@SuppressWarnings("unchecked")
		List<Entity> notchEntityList = ((CraftWorld)world).getHandle().b(entity, entity.boundingBox.b(x, y, z));
		List<org.bukkit.entity.Entity> bukkitEntityList = new java.util.ArrayList<org.bukkit.entity.Entity>(notchEntityList.size());

		for (Entity e: notchEntityList) {
			bukkitEntityList.add(e.getBukkitEntity());
		}
		return bukkitEntityList;
		*/
	}

	@Override
	public int getEntityId() {
		return entityId;
	}

	@Override
	public void remove() {
		delete();
		isDead = true;
	}

	@Override
	public boolean isDead() {
		return isDead;
	}

	public void setData(int index, Object value) {
		sendPacketToPlayersAround(createMetadataPacket(index, value));
	}

	protected PacketPlayOutEntityMetadata createMetadataPacket(int index, Object value) {
		if (value instanceof ItemStack) {
			try {
				// create entry
				datawatcher.a(index, 5); // v1_7_R1
			} catch (Exception e) { }

			// put the actual data in
			datawatcher.watch(index, value);

			// mark dirty
			datawatcher.h(index);

			final PacketPlayOutEntityMetadata packet40EntityMetadata = new PacketPlayOutEntityMetadata(entityId, datawatcher, false);
			/*TODO: postponed
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			packet40EntityMetadata.a(new DataOutputStream(baos));
			*/
			return packet40EntityMetadata;
		}
		else {
			try {
				// create entry
				datawatcher.a(index, value.getClass().getConstructor(String.class).newInstance("0")); // v1_7_R1
				// mark dirty
				datawatcher.watch(index, value.getClass().getConstructor(String.class).newInstance("1"));
			}
			catch (Exception e) { }

			// put the actual data in
			datawatcher.watch(index, value);

			return new PacketPlayOutEntityMetadata(entityId, datawatcher, false);
		}
	}

	public void sendEntityStatus(byte status) {
		final PacketPlayOutEntityStatus packetPlayOutEntityStatus = new PacketPlayOutEntityStatus();
		packetPlayOutEntityStatus.a = entityId; // v1_7_R1
		packetPlayOutEntityStatus.b = status; // v1_7_R1
		sendPacketToPlayersAround(packetPlayOutEntityStatus);
	}

	public void sendPacketToPlayersAround(Packet packet) {
		FoxBukkit.instance.playerHelper.sendPacketToPlayersAround(getLocation(), 1024, packet);
	}
}
