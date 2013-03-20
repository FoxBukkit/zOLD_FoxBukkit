package de.doridian.yiffbukkit.advanced.packetlistener;

import net.minecraft.server.v1_5_R1.Packet;
import org.bukkit.entity.Player;

public class YBPacketListener implements YBPacketListenerInt {
	protected enum PacketDirection {
		OUTGOING, INCOMING
	}

	public YBPacketListener() {

	}

	protected void register(int[] packetsIn, int[] packetsOut) {
		YBRealPacketListener.register(this, packetsIn, packetsOut);
	}

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

	protected void register(PacketDirection direction, int packet) {
		register(direction, new int[] { packet });
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		return true;
	}

	@Override
	public boolean onIncomingPacket(Player ply, int packetID, Packet packet) {
		return true;
	}
}
