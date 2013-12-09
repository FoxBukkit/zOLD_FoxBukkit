package de.doridian.yiffbukkit.advanced.packetlistener;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayInFlying;
import net.minecraft.server.v1_7_R1.PacketPlayInLook;
import net.minecraft.server.v1_7_R1.PacketPlayInPosition;
import net.minecraft.server.v1_7_R1.PacketPlayInPositionLook;
import net.minecraft.server.v1_7_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_7_R1.PacketPlayOutBed;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;
import net.minecraft.server.v1_7_R1.PacketPlayOutCollect;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntity;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityLook;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R1.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_7_R1.PacketPlayOutRelEntityMove;
import net.minecraft.server.v1_7_R1.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_7_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R1.PacketPlayOutUpdateAttributes;
import org.bukkit.entity.Player;

public class YBPacketListener implements YBPacketListenerInt {
	private static final TObjectIntHashMap<Class<? extends Packet>> packetToIDMapping;
	private static final TIntObjectHashMap<Class<? extends Packet>> idToPacketMapping;

	protected enum PacketDirection {
		OUTGOING, INCOMING
	}

	public YBPacketListener() {

	}

	static {
		packetToIDMapping = new TObjectIntHashMap<>();
		idToPacketMapping = new TIntObjectHashMap<>();

		addLegacyMapping(3, PacketPlayOutChat.class);

		addLegacyMapping(10, PacketPlayInFlying.class);
		addLegacyMapping(11, PacketPlayInPosition.class);
		addLegacyMapping(12, PacketPlayInLook.class);
		addLegacyMapping(13, PacketPlayInPositionLook.class);

		addLegacyMapping(17, PacketPlayOutBed.class);
		addLegacyMapping(18, PacketPlayOutAnimation.class);

		addLegacyMapping(20, PacketPlayOutNamedEntitySpawn.class);
		addLegacyMapping(22, PacketPlayOutCollect.class);
		addLegacyMapping(23, PacketPlayOutSpawnEntity.class);
		addLegacyMapping(24, PacketPlayOutSpawnEntityLiving.class);

		addLegacyMapping(30, PacketPlayOutEntity.class);
		addLegacyMapping(31, PacketPlayOutRelEntityMove.class);
		addLegacyMapping(32, PacketPlayOutEntityLook.class);
		addLegacyMapping(33, PacketPlayOutRelEntityMoveLook.class);
		addLegacyMapping(34, PacketPlayOutEntityTeleport.class);
		addLegacyMapping(35, PacketPlayOutEntityHeadRotation.class);

		addLegacyMapping(40, PacketPlayOutEntityMetadata.class);

		addLegacyMapping(44, PacketPlayOutUpdateAttributes.class);

		addLegacyMapping(64, PacketPlayOutNamedSoundEffect.class);

		addLegacyMapping(70, PacketPlayOutGameStateChange.class);
	}

	private static void addLegacyMapping(int ID, Class<? extends Packet> packet) {
		packetToIDMapping.put(packet, ID);
		idToPacketMapping.put(ID, packet);
	}

	protected void register(Class<? extends Packet>[] packetsIn, Class<? extends Packet>[] packetsOut) {
		YBRealPacketListener.register(this, packetsIn, packetsOut);
	}

	@Deprecated
	protected void register(int[] packetsIn, int[] packetsOut) {
		Class<? extends Packet>[] packetsInCls = new Class[packetsIn.length];
		Class<? extends Packet>[] packetsOutCls = new Class[packetsOut.length];
		for (int i = 0; i < packetsIn.length; i++) {
			packetsInCls[i] = idToPacketMapping.get(packetsIn[i]);
		}
		for (int i = 0; i < packetsOut.length; i++) {
			packetsOutCls[i] = idToPacketMapping.get(packetsOut[i]);
		}
		register(packetsInCls, packetsOutCls);
	}

	protected void register(PacketDirection direction, Class<? extends Packet>[] packets) {
		switch (direction) {
			case INCOMING:
				register(packets, null);
				break;
			case OUTGOING:
				register((Class<? extends Packet>[])null, packets);
				break;
		}
	}

	@Deprecated
	protected void register(PacketDirection direction, int[] packets) {
		switch (direction) {
			case INCOMING:
				register(packets, null);
				break;
			case OUTGOING:
				register((int[])null, packets);
				break;
		}
	}

	@Deprecated
	protected void register(PacketDirection direction, int packet) {
		register(direction, new int[] { packet });
	}

	protected void register(PacketDirection direction, Class<? extends Packet> packet) {
		register(direction, new Class[] { packet });
	}

	@Override
	public boolean onOutgoingPacket(Player ply, Class<? extends Packet> packetCls, Packet packet) {
		if(!packetToIDMapping.containsKey(packetCls))
			return true;
		return onOutgoingPacket(ply, packetToIDMapping.get(packetCls), packet);
	}

	@Override
	public boolean onIncomingPacket(Player ply, Class<? extends Packet> packetCls, Packet packet) {
		if(!packetToIDMapping.containsKey(packetCls))
			return true;
		return onIncomingPacket(ply, packetToIDMapping.get(packetCls), packet);
	}

	@Deprecated
	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		return true;
	}

	@Deprecated
	@Override
	public boolean onIncomingPacket(Player ply, int packetID, Packet packet) {
		return true;
	}
}
