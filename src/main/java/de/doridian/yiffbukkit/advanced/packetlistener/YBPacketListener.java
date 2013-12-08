package de.doridian.yiffbukkit.advanced.packetlistener;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.server.v1_7_R1.Packet;
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
		packetToIDMapping = new TObjectIntHashMap<Class<? extends Packet>>();
		idToPacketMapping = new TIntObjectHashMap<Class<? extends Packet>>();


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
