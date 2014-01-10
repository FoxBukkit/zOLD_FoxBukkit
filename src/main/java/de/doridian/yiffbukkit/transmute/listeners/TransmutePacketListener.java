package de.doridian.yiffbukkit.transmute.listeners;

import de.doridian.yiffbukkit.advanced.packetlistener.YBPacketListener;
import de.doridian.yiffbukkit.componentsystem.YBListener;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.transmute.Shape;
import de.doridian.yiffbukkit.transmute.Transmute;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutBed;
import net.minecraft.server.v1_7_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R1.PacketPlayOutCollect;
import net.minecraft.server.v1_7_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntity;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R1.PacketPlayOutUpdateAttributes;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class TransmutePacketListener extends YBPacketListener implements YBListener {
	private final Transmute transmute;
	public final Set<Packet> ignoredPackets = new HashSet<>();

	public TransmutePacketListener(Transmute transmute) {
		super();
		this.transmute = transmute;

		register(PacketDirection.OUTGOING, 17);
		register(PacketDirection.OUTGOING, 18);

		register(PacketDirection.OUTGOING, 20);
		register(PacketDirection.OUTGOING, 22);
		register(PacketDirection.OUTGOING, 23);
		register(PacketDirection.OUTGOING, 24);

		register(PacketDirection.OUTGOING, 32);
		register(PacketDirection.OUTGOING, 33);
		register(PacketDirection.OUTGOING, 34);
		register(PacketDirection.OUTGOING, 40);
		register(PacketDirection.OUTGOING, 44);
	}

	@Override
	public boolean onOutgoingPacket(final Player ply, int packetID, final Packet packet) {
		if (ignoredPackets.contains(packet))
			return true;

		final int entityId;

		switch (packetID) {
		case 17:
			return !transmute.isTransmuted(((PacketPlayOutBed) packet).a); // v1_7_R1

		case 18:
			entityId = ((PacketPlayOutAnimation) packet).a; // v1_7_R1
			break;

		case 20:
			return handleSpawn(ply, ((PacketPlayOutNamedEntitySpawn) packet).a); // v1_7_R1

		case 22:
			entityId = ((PacketPlayOutCollect) packet).b; // v1_7_R1?
			break;

		case 23:
			return handleSpawn(ply, ((PacketPlayOutSpawnEntity) packet).a); // v1_7_R1

		case 24:
			return handleSpawn(ply, ((PacketPlayOutSpawnEntityLiving) packet).a); // v1_7_R1

		//case 30:
		//case 31:
		case 32:
		case 33:
			entityId = ((PacketPlayOutEntity) packet).a; // v1_7_R1
			break;

		case 34:
			entityId = ((PacketPlayOutEntityTeleport) packet).a; // v1_7_R1
			break;

		case 40:
			return !transmute.isTransmuted(((PacketPlayOutEntityMetadata) packet).a); // v1_7_R1

		case 44:
			final int entityId2 = Utils.getPrivateValue(PacketPlayOutUpdateAttributes.class, (PacketPlayOutUpdateAttributes) packet, "a"); // v1_7_R1
			return !transmute.isTransmuted(entityId2); // TODO: don't block for MobShape and see what happens

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
