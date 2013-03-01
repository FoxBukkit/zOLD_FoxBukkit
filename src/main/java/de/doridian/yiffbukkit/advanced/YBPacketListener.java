package de.doridian.yiffbukkit.advanced;

import de.doridian.yiffbukkitsplit.YiffBukkit;
import net.minecraft.server.v1_4_R1.Connection;
import net.minecraft.server.v1_4_R1.INetworkManager;
import net.minecraft.server.v1_4_R1.Packet;
import net.minecraft.server.v1_4_R1.PlayerConnection;
import org.bukkit.entity.Player;
import org.spigotmc.netty.PacketListener;

public abstract class YBPacketListener extends PacketListener {
	public YBPacketListener(YiffBukkit plugin) {
		PacketListener.register(this, plugin);
	}

	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) { return true; }
	public boolean onIncomingPacket(Player ply, int packetID, Packet packet) { return true; }

	@Override
	public Packet packetReceived(INetworkManager networkManager, Connection connection, Packet packet) {
		if(onIncomingPacket(((PlayerConnection)connection).getPlayer(), packet.k(), packet)) {
			return packet;
		} else {
			return null;
		}
	}

	@Override
	public Packet packetQueued(INetworkManager networkManager, Connection connection, Packet packet) {
		if(onOutgoingPacket(((PlayerConnection)connection).getPlayer(), packet.k(), packet)) {
			return packet;
		} else {
			return null;
		}
	}
}
