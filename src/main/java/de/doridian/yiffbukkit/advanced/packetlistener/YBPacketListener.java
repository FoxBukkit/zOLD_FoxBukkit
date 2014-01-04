package de.doridian.yiffbukkit.advanced.packetlistener;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayInChat;
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

	private static final TIntObjectHashMap<Class<? extends Packet>> idToPacketMappingIn;
	private static final TIntObjectHashMap<Class<? extends Packet>> idToPacketMappingOut;

	protected enum PacketDirection {
		OUTGOING, INCOMING
	}

	static {
		packetToIDMapping = new TObjectIntHashMap<>();
		idToPacketMappingIn = new TIntObjectHashMap<>();
		idToPacketMappingOut = new TIntObjectHashMap<>();

		addLegacyMapping(3, PacketPlayInChat.class, PacketPlayOutChat.class);

		addLegacyMappingIn(10, PacketPlayInFlying.class);
		addLegacyMappingIn(11, PacketPlayInPosition.class);
		addLegacyMappingIn(12, PacketPlayInLook.class);
		addLegacyMappingIn(13, PacketPlayInPositionLook.class);

		addLegacyMappingOut(17, PacketPlayOutBed.class);
		addLegacyMappingOut(18, PacketPlayOutAnimation.class);

		addLegacyMappingOut(20, PacketPlayOutNamedEntitySpawn.class);
		addLegacyMappingOut(22, PacketPlayOutCollect.class);
		addLegacyMappingOut(23, PacketPlayOutSpawnEntity.class);
		addLegacyMappingOut(24, PacketPlayOutSpawnEntityLiving.class);

		addLegacyMappingOut(30, PacketPlayOutEntity.class);
		addLegacyMappingOut(31, PacketPlayOutRelEntityMove.class);
		addLegacyMappingOut(32, PacketPlayOutEntityLook.class);
		addLegacyMappingOut(33, PacketPlayOutRelEntityMoveLook.class);
		addLegacyMappingOut(34, PacketPlayOutEntityTeleport.class);
		addLegacyMappingOut(35, PacketPlayOutEntityHeadRotation.class);

		addLegacyMappingOut(40, PacketPlayOutEntityMetadata.class);

		addLegacyMappingOut(44, PacketPlayOutUpdateAttributes.class);

		addLegacyMappingOut(62, PacketPlayOutNamedSoundEffect.class);

		addLegacyMappingOut(70, PacketPlayOutGameStateChange.class);
	}

	private static void addLegacyMappingIn(int ID, Class<? extends Packet> packet) {
		addLegacyMapping(ID, packet, null);
	}

	private static void addLegacyMappingOut(int ID, Class<? extends Packet> packet) {
		addLegacyMapping(ID, null, packet);
	}

	private static void addLegacyMapping(int ID, Class<? extends Packet> packetIn, Class<? extends Packet> packetOut) {
		if(packetIn != null) {
			packetToIDMapping.put(packetIn, ID);
			idToPacketMappingIn.put(ID, packetIn);
		}

		if(packetOut != null) {
			packetToIDMapping.put(packetOut, ID);
			idToPacketMappingOut.put(ID, packetOut);
		}
	}

	protected final void register(Class<? extends Packet>[] packetsIn, Class<? extends Packet>[] packetsOut) {
		YBRealPacketListener.register(this, packetsIn, packetsOut);
	}

	@Deprecated
	protected final void register(int[] packetsIn, int[] packetsOut) {
		register(
				mapIdsToPackets(packetsIn, idToPacketMappingIn),
				mapIdsToPackets(packetsOut, idToPacketMappingOut)
		);
	}

	private static Class<? extends Packet>[] mapIdsToPackets(int[] packetsIn, TIntObjectHashMap<Class<? extends Packet>> mapping) {
		if (packetsIn == null)
			return null;

		@SuppressWarnings("unchecked")
		Class<? extends Packet>[] packetsInCls = new Class[packetsIn.length];
		for (int i = 0; i < packetsIn.length; i++) {
			packetsInCls[i] = mapping.get(packetsIn[i]);
		}
		return packetsInCls;
	}

	@SafeVarargs
	protected final void register(PacketDirection direction, Class<? extends Packet>... packets) {
		switch (direction) {
			case INCOMING:
				register(packets, null);
				break;
			case OUTGOING:
				register((Class<? extends Packet>[])null, packets);
				break;
		}
	}

	@SuppressWarnings("deprecation")
	@Deprecated
	protected final void register(PacketDirection direction, int... packets) {
		switch (direction) {
			case INCOMING:
				register(packets, null);
				break;
			case OUTGOING:
				register((int[]) null, packets);
				break;
		}
	}

	@Override
	public boolean onOutgoingPacket(Player ply, Class<? extends Packet> packetCls, Packet packet) {
		if (!packetToIDMapping.containsKey(packetCls))
			return true;

		//noinspection deprecation
		return onOutgoingPacket(ply, packetToIDMapping.get(packetCls), packet);
	}

	@Override
	public boolean onIncomingPacket(Player ply, Class<? extends Packet> packetCls, Packet packet) {
		if (!packetToIDMapping.containsKey(packetCls))
			return true;

		//noinspection deprecation
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
