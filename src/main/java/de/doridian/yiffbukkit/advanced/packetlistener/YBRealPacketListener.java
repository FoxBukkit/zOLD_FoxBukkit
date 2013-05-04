package de.doridian.yiffbukkit.advanced.packetlistener;

import de.doridian.yiffbukkitsplit.YiffBukkit;
import net.minecraft.server.v1_5_R3.Connection;
import net.minecraft.server.v1_5_R3.INetworkManager;
import net.minecraft.server.v1_5_R3.Packet;
import net.minecraft.server.v1_5_R3.PlayerConnection;
import org.bukkit.entity.Player;
import org.spigotmc.netty.PacketListener;

import java.util.HashSet;

class YBRealPacketListener extends PacketListener implements YBPacketListenerInt {
	@SuppressWarnings("serial")
	private class YBPLCollection extends HashSet<YBPacketListener> { }

	private final YBPLCollection[] incomingPacketListeners;
	private final YBPLCollection[] outgoingPacketListeners;

	private static YBRealPacketListener instance = null;
	static void initialize(YiffBukkit plugin) {
		if(instance != null) return;
		instance = new YBRealPacketListener(plugin);
	}

	static void register(YBPacketListener ybPacketListener, int[] packetsIn, int[] packetsOut) {
		initialize(YiffBukkit.instance);
		instance._register(ybPacketListener, packetsIn, packetsOut);
	}

	private void _register(YBPacketListener ybPacketListener, int[] packetsIn, int[] packetsOut) {
		if(packetsIn != null) {
			for(int i : packetsIn) {
				incomingPacketListeners[i].add(ybPacketListener);
			}
		}

		if(packetsOut != null) {
			for(int i : packetsOut) {
				outgoingPacketListeners[i].add(ybPacketListener);
			}
		}
	}

	public YBRealPacketListener(YiffBukkit plugin) {
		PacketListener.register(this, plugin);

		incomingPacketListeners = new YBPLCollection[256];
		outgoingPacketListeners = new YBPLCollection[256];

		for(int i=0;i<256;i++) {
			incomingPacketListeners[i] = new YBPLCollection();
			outgoingPacketListeners[i] = new YBPLCollection();
		}
	}

	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		for(YBPacketListener ybPacketListener : outgoingPacketListeners[packetID]) {
			if(!ybPacketListener.onOutgoingPacket(ply, packetID, packet)) return false;
		}
		return true;
	}

	public boolean onIncomingPacket(Player ply, int packetID, Packet packet) {
		for(YBPacketListener ybPacketListener : incomingPacketListeners[packetID]) {
			if(!ybPacketListener.onIncomingPacket(ply, packetID, packet)) return false;
		}
		return true;
	}

	@Override
	public Packet packetReceived(INetworkManager networkManager, Connection connection, Packet packet) {
		if(!(connection instanceof PlayerConnection)) return packet;

		if(onIncomingPacket(((PlayerConnection)connection).getPlayer(), packet.n(), packet)) {
			return packet;
		} else {
			return null;
		}
	}

	@Override
	public Packet packetQueued(INetworkManager networkManager, Connection connection, Packet packet) {
		if(packet == null || !(connection instanceof PlayerConnection)) return packet;

		if(onOutgoingPacket(((PlayerConnection)connection).getPlayer(), packet.n(), packet)) {
			return packet;
		} else {
			return null;
		}
	}
}
