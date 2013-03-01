package de.doridian.yiffbukkit.transmute.listeners;

import de.doridian.yiffbukkit.advanced.YBPacketListener;
import de.doridian.yiffbukkit.componentsystem.YBListener;
import de.doridian.yiffbukkit.transmute.Shape;
import de.doridian.yiffbukkit.transmute.Transmute;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import net.minecraft.server.v1_4_R1.Packet;
import net.minecraft.server.v1_4_R1.Packet17EntityLocationAction;
import net.minecraft.server.v1_4_R1.Packet18ArmAnimation;
import net.minecraft.server.v1_4_R1.Packet20NamedEntitySpawn;
import net.minecraft.server.v1_4_R1.Packet22Collect;
import net.minecraft.server.v1_4_R1.Packet23VehicleSpawn;
import net.minecraft.server.v1_4_R1.Packet24MobSpawn;
import net.minecraft.server.v1_4_R1.Packet30Entity;
import net.minecraft.server.v1_4_R1.Packet34EntityTeleport;
import net.minecraft.server.v1_4_R1.Packet40EntityMetadata;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class TransmutePacketListener extends YBPacketListener implements YBListener {
	private final Transmute transmute;
	public final Set<Packet> ignoredPackets = new HashSet<Packet>();

	public TransmutePacketListener(Transmute transmute) {
		super(YiffBukkit.instance);
		this.transmute = transmute;
		Plugin plugin = YiffBukkit.instance;
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

		case 22:
			entityId = ((Packet22Collect) packet).b;
			break;

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
