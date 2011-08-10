package de.doridian.yiffbukkit.transmute;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.Packet17;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet23VehicleSpawn;
import net.minecraft.server.Packet24MobSpawn;

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
	}

	@Override
	public boolean onOutgoingPacket(final Player ply, int packetID, final Packet packet) {
		switch (packetID) {
		case 17:
			final Packet17 p17 = (Packet17) packet;

			return !transmute.isTransmuted(p17.a);

		case 18:
			final Packet18ArmAnimation p18 = (Packet18ArmAnimation) packet;

			if (p18.b == 2)
				return true;

			return !transmute.isTransmuted(p18.a);

		case 20:
			if (ignoredPackets.contains(packet))
				return true;

			final Packet20NamedEntitySpawn p20 = (Packet20NamedEntitySpawn) packet;
			return handleSpawn(ply, p20.a);

		case 23:
			if (ignoredPackets.contains(packet))
				return true;

			final Packet23VehicleSpawn p23 = (Packet23VehicleSpawn) packet;
			return handleSpawn(ply, p23.a);

		case 24:
			if (ignoredPackets.contains(packet))
				return true;

			final Packet24MobSpawn p24 = (Packet24MobSpawn) packet;
			return handleSpawn(ply, p24.a);

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
