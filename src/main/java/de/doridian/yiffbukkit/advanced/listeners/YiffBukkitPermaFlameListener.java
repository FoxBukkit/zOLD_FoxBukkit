package de.doridian.yiffbukkit.advanced.listeners;

import de.doridian.yiffbukkit.advanced.packetlistener.YBPacketListener;
import de.doridian.yiffbukkit.componentsystem.YBListener;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.AutoCleanup;
import gnu.trove.TDecorators;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet40EntityMetadata;
import net.minecraft.server.v1_6_R2.WatchableObject;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class YiffBukkitPermaFlameListener extends YBPacketListener implements YBListener {
	public static YiffBukkitPermaFlameListener instance;

	public YiffBukkitPermaFlameListener() {
		instance = this;

		AutoCleanup.registerEntityIdSet(permaFlameEntities);

		register(PacketDirection.OUTGOING, 40);
	}

	private final TIntHashSet permaFlameEntities = new TIntHashSet();

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		switch (packetID) {
		case 40:
			final Packet40EntityMetadata p40 = (Packet40EntityMetadata) packet;
			if (!permaFlameEntities.contains(p40.a)) // v1_6_R2
				break;

			modifyMetadataPacket(p40, ply.getWorld(), false, null);
			break;
		}

		return true;
	}

	private void modifyMetadataPacket(Packet40EntityMetadata p40, World world, boolean reset, Entity entity) {
		final int entityId = p40.a; // v1_6_R2
		final List<WatchableObject> metadata = Utils.getPrivateValue(Packet40EntityMetadata.class, p40, "b"); // v1_6_R2

		boolean found = false;
		// The "save some bandwidth" loop
		for (WatchableObject watchableObject : metadata) {
			final int objectType = watchableObject.c(); // v1_6_R2
			if (objectType != 0)
				continue;

			final int dataValueId = watchableObject.a(); // v1_6_R2
			if (dataValueId != 0)
				continue;

			final Object watchedObject = watchableObject.b(); // v1_6_R2
			if (!(watchedObject instanceof Integer))
				continue;

			if (((Integer) watchedObject & 1) == 0)
				continue;

			final boolean watched = watchableObject.d(); // v1_6_R2
			if (watched)
				continue;

			found = true;
			break;
		}

		if (!found) {
			final net.minecraft.server.v1_6_R2.Entity notchEntity;
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

			final WatchableObject e = new WatchableObject(0, 0, value);

			e.a(false); // v1_6_R2
			metadata.add(e);
		}
	}

	public boolean addPermaFlameEntity(Entity entity) {
		final int entityId = entity.getEntityId();
		if (!permaFlameEntities.add(entityId))
			return false;

		final Packet40EntityMetadata p40 = new Packet40EntityMetadata();
		p40.a = entityId; // v1_6_R2
		Utils.setPrivateValue(Packet40EntityMetadata.class, p40, "b", new ArrayList<WatchableObject>()); // v1_6_R2
		modifyMetadataPacket(p40, entity.getWorld(), false, entity);
		YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 512, p40);

		return true;
	}

	public boolean removePermaFlameEntity(Entity entity) {
		final int entityId = entity.getEntityId();
		if (!permaFlameEntities.remove(entityId))
			return false;

		final Packet40EntityMetadata p40 = new Packet40EntityMetadata();
		p40.a = entityId; // v1_6_R2
		Utils.setPrivateValue(Packet40EntityMetadata.class, p40, "b", new ArrayList<WatchableObject>()); // v1_6_R2
		modifyMetadataPacket(p40, entity.getWorld(), true, entity);
		YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 512, p40);

		return true;
	}
}
