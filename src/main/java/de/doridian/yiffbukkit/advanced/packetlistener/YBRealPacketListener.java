package de.doridian.yiffbukkit.advanced.packetlistener;

import de.doridian.yiffbukkit.componentsystem.YBListener;
import de.doridian.yiffbukkit.core.YiffBukkit;
import net.minecraft.server.v1_7_R3.NetworkManager;
import net.minecraft.server.v1_7_R3.Packet;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;

public class YBRealPacketListener extends NetworkManager.DoriBukkitPacketListener implements YBListener {
	private final HashMap<Class<? extends Packet>, HashSet<YBPacketListener>> incomingPacketListeners;
	private final HashMap<Class<? extends Packet>, HashSet<YBPacketListener>> outgoingPacketListeners;

	private static YBRealPacketListener instance = null;

	private static void initialize() {
		if(instance != null) return;
		new YBRealPacketListener(YiffBukkit.instance);
	}

	static void register(YBPacketListener ybPacketListener, Class<? extends Packet>[] packetsIn, Class<? extends Packet>[] packetsOut) {
		initialize();
		instance._register(ybPacketListener, packetsIn, packetsOut);
	}

	private void _register(YBPacketListener ybPacketListener, Class<? extends Packet>[] packetsIn, Class<? extends Packet>[] packetsOut) {
		if(packetsIn != null) {
			for(Class<? extends Packet> i : packetsIn) {
				if(!incomingPacketListeners.containsKey(i))
					incomingPacketListeners.put(i, new HashSet<YBPacketListener>());
				incomingPacketListeners.get(i).add(ybPacketListener);
			}
		}

		if(packetsOut != null) {
			for(Class<? extends Packet> i : packetsOut) {
				if(!outgoingPacketListeners.containsKey(i))
					outgoingPacketListeners.put(i, new HashSet<YBPacketListener>());
				outgoingPacketListeners.get(i).add(ybPacketListener);
			}
		}
	}

	private YBRealPacketListener(YiffBukkit plugin) {
		if(instance != null)
			throw new RuntimeException("This is a singleton!");

		incomingPacketListeners = new HashMap<>();
		outgoingPacketListeners = new HashMap<>();

		instance = this;

		NetworkManager.registerPacketListener(this);
	}

	@Override
	public boolean outgoingPacket(final Player ply, final Packet packet) {
		final Class<? extends Packet> packetCls = packet.getClass();
		final HashSet<YBPacketListener> ybPacketListeners = outgoingPacketListeners.get(packetCls);
		if(ybPacketListeners == null || ybPacketListeners.isEmpty())
			return true;
		for(YBPacketListener ybPacketListener : ybPacketListeners)
			if(!ybPacketListener.onOutgoingPacket(ply, packetCls, packet))
				return false;
		return true;
	}

	@Override
	public boolean incomingPacket(final Player ply, final Packet packet) {
		final Class<? extends Packet> packetCls = packet.getClass();
		final HashSet<YBPacketListener> ybPacketListeners = incomingPacketListeners.get(packetCls);
		if(ybPacketListeners == null || ybPacketListeners.isEmpty())
			return true;
		for(YBPacketListener ybPacketListener : ybPacketListeners)
			if(!ybPacketListener.onIncomingPacket(ply, packetCls, packet))
				return false;
		return true;
	}
}
