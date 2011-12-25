package de.doridian.yiffbukkit.transmute;

import net.minecraft.server.Packet17EntityLocationAction;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet23VehicleSpawn;
import net.minecraft.server.Packet24MobSpawn;
import net.minecraft.server.Packet30Entity;
import net.minecraft.server.Packet34EntityTeleport;
import net.minecraft.server.Packet40EntityMetadata;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class TransmutePacketListener extends PacketListener {
	private final Transmute transmute;
	final Set<Packet> ignoredPackets = new HashSet<Packet>();

	public TransmutePacketListener(Transmute transmute) {
		this.transmute = transmute;
		Plugin plugin = transmute.plugin;

		PacketListener.addPacketListener(true, 17, this, plugin);
		PacketListener.addPacketListener(true, 18, this, plugin);
		PacketListener.addPacketListener(true, 20, this, plugin);
		PacketListener.addPacketListener(true, 23, this, plugin);
		PacketListener.addPacketListener(true, 24, this, plugin);
		//PacketListener.addPacketListener(true, 30, this, plugin);
		//PacketListener.addPacketListener(true, 31, this, plugin);
		PacketListener.addPacketListener(true, 32, this, plugin);
		PacketListener.addPacketListener(true, 33, this, plugin);
		PacketListener.addPacketListener(true, 34, this, plugin);
		PacketListener.addPacketListener(true, 40, this, plugin);
	}

	@Override
	public boolean onOutgoingPacket(final Player ply, int packetID, final Packet packet) {
		if (ignoredPackets.contains(packet))
			return true;

		final int entityId;

		switch (packetID) {
		case 17:
			return !transmute.isTransmuted(((Packet17EntityLocationAction) packet).a);

		case 18:
			entityId = ((Packet18ArmAnimation) packet).a;
			break;

		case 20:
			return handleSpawn(ply, ((Packet20NamedEntitySpawn) packet).a);

		case 23:
			return handleSpawn(ply, ((Packet23VehicleSpawn) packet).a);

		case 24:
			return handleSpawn(ply, ((Packet24MobSpawn) packet).a);

		//case 30:
		//case 31:
		case 32:
		case 33:
			entityId = ((Packet30Entity) packet).a;
			break;

		case 34:
			entityId = ((Packet34EntityTeleport) packet).a;
			break;

		case 40:
			return !transmute.isTransmuted(((Packet40EntityMetadata) packet).a);

		default:
			return true;
		}

		final Shape shape = transmute.getShape(entityId);
		if (shape == null)
			return true;

		return shape.onOutgoingPacket(ply, packetID, packet);
	}

	private boolean handleSpawn(final Player ply, final int entityId) {
		final Shape shape = transmute.getShape(entityId);
		if (shape == null)
			return true;

		shape.createTransmutedEntity(ply);

		return false;
	}

	/*private Entity getEntityFromID(final Player ply, final int entityId) {
		return ((CraftWorld)ply.getWorld()).getHandle().getEntity(entityId);
	}*/
}
