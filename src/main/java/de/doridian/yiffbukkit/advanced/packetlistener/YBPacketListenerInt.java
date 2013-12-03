package de.doridian.yiffbukkit.advanced.packetlistener;

import net.minecraft.server.v1_7_R1.Packet;
import org.bukkit.entity.Player;

interface YBPacketListenerInt {
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet);
	public boolean onIncomingPacket(Player ply, int packetID, Packet packet);
}
