package de.doridian.yiffbukkit.transmute;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.Packet17EntityLocationAction;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet23VehicleSpawn;
import net.minecraft.server.Packet24MobSpawn;
import net.minecraft.server.Packet40EntityMetadata;

import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;
import org.bukkit.plugin.Plugin;

public class TransmutePacketListener extends PacketListener {
	private final Transmute transmute;
	final Set<net.minecraft.server.Packet> ignoredPackets = new HashSet<net.minecraft.server.Packet>();

	public TransmutePacketListener(Transmute transmute) {
		this.transmute = transmute;
		Plugin plugin = transmute.plugin;

		PacketListener.addPacketListener(true, 17, this, plugin);
		PacketListener.addPacketListener(true, 18, this, plugin);
		PacketListener.addPacketListener(true, 20, this, plugin);
		PacketListener.addPacketListener(true, 23, this, plugin);
		PacketListener.addPacketListener(true, 24, this, plugin);
		PacketListener.addPacketListener(true, 40, this, plugin);
	}

	@Override
	public boolean onOutgoingPacket(final Player ply, int packetID, final Packet packet) {
		if (ignoredPackets.contains(packet))
			return true;

		switch (packetID) {
		case 17:
			return !transmute.isTransmuted(((Packet17EntityLocationAction) packet).a);

		case 18:
			final Packet18ArmAnimation p18 = (Packet18ArmAnimation) packet;

			if (p18.b == 2)
				return true;

			return !transmute.isTransmuted(p18.a);

		case 20:
			return handleSpawn(ply, ((Packet20NamedEntitySpawn) packet).a);

		case 23:
			return handleSpawn(ply, ((Packet23VehicleSpawn) packet).a);

		case 24:
			return handleSpawn(ply, ((Packet24MobSpawn) packet).a);

		case 40:
			return !transmute.isTransmuted(((Packet40EntityMetadata) packet).a);

		default:
			return true;
		}
	}

	private boolean handleSpawn(final Player ply, final int entityID) {
		final Shape shape = transmute.getShape(entityID);
		if (shape == null)
			return true;

		shape.createTransmutedEntity(ply);

		return false;
	}

	/*private Entity getEntityFromID(final Player ply, final int entityID) {
		return ((CraftWorld)ply.getWorld()).getHandle().getEntity(entityID);
	}*/
}
