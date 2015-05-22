/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.advanced.packetlistener;

import com.foxelbox.foxbukkit.core.FoxBukkit;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;

public class FBRealPacketListener extends NetworkManager.FoxelBoxCraftBukkitPacketListener {
	private final HashMap<Class<? extends Packet>, HashSet<FBPacketListener>> incomingPacketListeners;
	private final HashMap<Class<? extends Packet>, HashSet<FBPacketListener>> outgoingPacketListeners;

	private static FBRealPacketListener instance = null;

	private static void initialize() {
		if(instance != null) return;
		new FBRealPacketListener(FoxBukkit.instance);
	}

	static void register(FBPacketListener fbPacketListener, Class<? extends Packet>[] packetsIn, Class<? extends Packet>[] packetsOut) {
		initialize();
		instance._register(fbPacketListener, packetsIn, packetsOut);
	}

	private void _register(FBPacketListener fbPacketListener, Class<? extends Packet>[] packetsIn, Class<? extends Packet>[] packetsOut) {
		if(packetsIn != null) {
			for(Class<? extends Packet> i : packetsIn) {
				if(!incomingPacketListeners.containsKey(i))
					incomingPacketListeners.put(i, new HashSet<FBPacketListener>());
				incomingPacketListeners.get(i).add(fbPacketListener);
			}
		}

		if(packetsOut != null) {
			for(Class<? extends Packet> i : packetsOut) {
				if(!outgoingPacketListeners.containsKey(i))
					outgoingPacketListeners.put(i, new HashSet<FBPacketListener>());
				outgoingPacketListeners.get(i).add(fbPacketListener);
			}
		}
	}

	private FBRealPacketListener(FoxBukkit plugin) {
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
		final HashSet<FBPacketListener> fbPacketListeners = outgoingPacketListeners.get(packetCls);
		if(fbPacketListeners == null || fbPacketListeners.isEmpty())
			return true;
		for(FBPacketListener fbPacketListener : fbPacketListeners)
			if(!fbPacketListener.onOutgoingPacket(ply, packetCls, packet))
				return false;
		return true;
	}

	@Override
	public boolean incomingPacket(final Player ply, final Packet packet) {
		final Class<? extends Packet> packetCls = packet.getClass();
		final HashSet<FBPacketListener> fbPacketListeners = incomingPacketListeners.get(packetCls);
		if(fbPacketListeners == null || fbPacketListeners.isEmpty())
			return true;
		for(FBPacketListener fbPacketListener : fbPacketListeners)
			if(!fbPacketListener.onIncomingPacket(ply, packetCls, packet))
				return false;
		return true;
	}
}
