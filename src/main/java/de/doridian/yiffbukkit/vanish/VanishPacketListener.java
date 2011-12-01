package de.doridian.yiffbukkit.vanish;

import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;
import org.bukkit.plugin.Plugin;

import net.minecraft.server.Packet17EntityLocationAction;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet22Collect;
import net.minecraft.server.Packet23VehicleSpawn;
import net.minecraft.server.Packet24MobSpawn;
import net.minecraft.server.Packet28EntityVelocity;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet30Entity;
import net.minecraft.server.Packet34EntityTeleport;
import net.minecraft.server.Packet38EntityStatus;
import net.minecraft.server.Packet39AttachEntity;
import net.minecraft.server.Packet40EntityMetadata;
import net.minecraft.server.Packet5EntityEquipment;

public class VanishPacketListener extends PacketListener {
	private final Vanish vanish;

	public VanishPacketListener(Vanish vanish) {
		this.vanish = vanish;
		Plugin plugin = vanish.plugin;

		/*for (int i = 0; i < 256; ++i) {
			PacketListener.addPacketListener(true, i, this, plugin);
		}*/
		PacketListener.addPacketListener(true,  5, this, plugin);
		PacketListener.addPacketListener(true, 17, this, plugin);
		PacketListener.addPacketListener(true, 18, this, plugin);
		PacketListener.addPacketListener(true, 20, this, plugin);
		PacketListener.addPacketListener(true, 22, this, plugin);
		PacketListener.addPacketListener(true, 23, this, plugin);
		PacketListener.addPacketListener(true, 24, this, plugin);
		PacketListener.addPacketListener(true, 28, this, plugin);
		PacketListener.addPacketListener(true, 29, this, plugin);
		PacketListener.addPacketListener(true, 30, this, plugin);
		PacketListener.addPacketListener(true, 31, this, plugin);
		PacketListener.addPacketListener(true, 32, this, plugin);
		PacketListener.addPacketListener(true, 33, this, plugin);
		PacketListener.addPacketListener(true, 34, this, plugin);
		PacketListener.addPacketListener(true, 38, this, plugin);
		PacketListener.addPacketListener(true, 39, this, plugin);
		PacketListener.addPacketListener(true, 40, this, plugin);
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		if (vanish.canSeeEveryone(ply))
			return true;

		int entityId = -1;
		switch (packetID) {
		case 5:
			Packet5EntityEquipment p5 = (Packet5EntityEquipment) packet;
			entityId = p5.a;
			break;

		case 17: // Use Bed
			Packet17EntityLocationAction p17 = (Packet17EntityLocationAction) packet;
			entityId = p17.a;
			break;

		case 18:
			Packet18ArmAnimation p18 = (Packet18ArmAnimation) packet;
			entityId = p18.a;
			break;

		case 20:
			Packet20NamedEntitySpawn p20 = (Packet20NamedEntitySpawn) packet;
			entityId = p20.a;
			break;

		case 22:
			Packet22Collect p22 = (Packet22Collect) packet;
			entityId = p22.b;
			break;

		case 23:
			Packet23VehicleSpawn p23 = (Packet23VehicleSpawn) packet;
			if (vanish.vanishedEntityIds.contains(p23.i))
				p23.i = 1;

			entityId = p23.a;
			break;

		case 24:
			Packet24MobSpawn p24 = (Packet24MobSpawn) packet;
			entityId = p24.a;
			break;

		case 28:
			Packet28EntityVelocity p28 = (Packet28EntityVelocity) packet;
			entityId = p28.a;
			break;

		case 29:
			Packet29DestroyEntity p29 = (Packet29DestroyEntity) packet;
			entityId = p29.a;
			break;

		case 30:
		case 31:
		case 32:
		case 33:
			Packet30Entity p30 = (Packet30Entity) packet;
			entityId = p30.a;
			break;

		case 34:
			Packet34EntityTeleport p34 = (Packet34EntityTeleport) packet;
			entityId = p34.a;
			break;

		case 38:
			Packet38EntityStatus p38 = (Packet38EntityStatus) packet;
			entityId = p38.a;
			break;

		case 39:
			Packet39AttachEntity p39 = (Packet39AttachEntity) packet;
			if (vanish.vanishedEntityIds.contains(p39.b))
				return false;

			entityId = p39.a;
			break;

		case 40:
			Packet40EntityMetadata p40 = (Packet40EntityMetadata) packet;
			entityId = p40.a;
			break;

		default:
			removePacketListener(true, packetID, this);
			return true;
		}

		if (entityId == -1) {
			return true;
		}

		/*if (otherName.charAt(0) == '\u00a7')
			return false;*/ // TODO!!!

		if (vanish.vanishedEntityIds.contains(entityId))
			return false;

		return true;
	}
}
