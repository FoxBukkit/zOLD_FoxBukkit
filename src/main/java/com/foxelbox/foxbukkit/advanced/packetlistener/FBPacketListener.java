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

import com.foxelbox.foxbukkit.main.listeners.BaseListener;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockPlace;
import net.minecraft.server.v1_7_R4.PacketPlayInChat;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInLook;
import net.minecraft.server.v1_7_R4.PacketPlayInPosition;
import net.minecraft.server.v1_7_R4.PacketPlayInPositionLook;
import net.minecraft.server.v1_7_R4.PacketPlayOutAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayOutBed;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;
import net.minecraft.server.v1_7_R4.PacketPlayOutCollect;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityLook;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R4.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_7_R4.PacketPlayOutRelEntityMove;
import net.minecraft.server.v1_7_R4.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R4.PacketPlayOutUpdateAttributes;
import org.bukkit.entity.Player;

public class FBPacketListener extends BaseListener implements FBPacketListenerInt {
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

		addLegacyMappingIn(8, PacketPlayInBlockPlace.class);

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
		FBRealPacketListener.register(this, packetsIn, packetsOut);
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
