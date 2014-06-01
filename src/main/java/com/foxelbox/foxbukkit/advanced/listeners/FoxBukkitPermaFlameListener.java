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
package com.foxelbox.foxbukkit.advanced.listeners;

import com.foxelbox.foxbukkit.advanced.packetlistener.FBPacketListener;
import com.foxelbox.foxbukkit.componentsystem.FBListener;
import com.foxelbox.foxbukkit.core.FoxBukkit;
import com.foxelbox.foxbukkit.core.util.AutoCleanup;
import com.foxelbox.foxbukkit.main.util.Utils;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R3.WatchableObject;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FoxBukkitPermaFlameListener extends FBPacketListener implements FBListener {
	public static FoxBukkitPermaFlameListener instance;

	public FoxBukkitPermaFlameListener() {
		instance = this;

		AutoCleanup.registerEntityIdSet(permaFlameEntities);

		register(PacketDirection.OUTGOING, 40);
	}

	private final TIntHashSet permaFlameEntities = new TIntHashSet();

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		switch (packetID) {
		case 40:
			final PacketPlayOutEntityMetadata p40 = (PacketPlayOutEntityMetadata) packet;
			if (!permaFlameEntities.contains(p40.a)) // v1_7_R1
				break;

			modifyMetadataPacket(p40, ply.getWorld(), false, null);
			break;
		}

		return true;
	}

	private void modifyMetadataPacket(PacketPlayOutEntityMetadata p40, World world, boolean reset, Entity entity) {
		final int entityId = p40.a; // v1_7_R1
		@SuppressWarnings("unchecked")
		final List<WatchableObject> metadata = p40.b; // v1_7_R1

		boolean found = false;
		// The "save some bandwidth" loop
		for (WatchableObject watchableObject : metadata) {
			final int objectType = watchableObject.c(); // v1_7_R1
			if (objectType != 0)
				continue;

			final int dataValueId = watchableObject.a(); // v1_7_R1
			if (dataValueId != 0)
				continue;

			final Object watchedObject = watchableObject.b(); // v1_7_R1
			if (!(watchedObject instanceof Integer))
				continue;

			if (((Integer) watchedObject & 1) == 0)
				continue;

			final boolean watched = watchableObject.d(); // v1_7_R1
			if (watched)
				continue;

			found = true;
			break;
		}

		if (!found) {
			final net.minecraft.server.v1_7_R3.Entity notchEntity;
			byte value = 0;
			if (entity == null) {
				notchEntity = Utils.getEntityByID(entityId, world);
				value = notchEntity.getDataWatcher().getByte(0);
			}
			else if (entity instanceof CraftEntity) {
				notchEntity = ((CraftEntity) entity).getHandle();
				value = notchEntity.getDataWatcher().getByte(0);
			}

			if (!reset)
				value |= 1;

			final WatchableObject watchableObject = new WatchableObject(0, 0, value);

			watchableObject.a(false); // v1_7_R1
			metadata.add(watchableObject);
		}
	}

	public boolean addPermaFlameEntity(Entity entity) {
		final int entityId = entity.getEntityId();
		if (!permaFlameEntities.add(entityId))
			return false;

		final PacketPlayOutEntityMetadata p40 = new PacketPlayOutEntityMetadata();
		p40.a = entityId; // v1_7_R1
		p40.b = new ArrayList<WatchableObject>(); // v1_7_R1
		modifyMetadataPacket(p40, entity.getWorld(), false, entity);
		FoxBukkit.instance.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 512, p40);

		return true;
	}

	public boolean removePermaFlameEntity(Entity entity) {
		final int entityId = entity.getEntityId();
		if (!permaFlameEntities.remove(entityId))
			return false;

		final PacketPlayOutEntityMetadata p40 = new PacketPlayOutEntityMetadata();
		p40.a = entityId; // v1_7_R1
		p40.b = new ArrayList<WatchableObject>(); // v1_7_R1
		modifyMetadataPacket(p40, entity.getWorld(), true, entity);
		FoxBukkit.instance.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 512, p40);

		return true;
	}
}
