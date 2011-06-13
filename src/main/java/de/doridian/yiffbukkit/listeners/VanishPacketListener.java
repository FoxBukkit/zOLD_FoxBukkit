package de.doridian.yiffbukkit.listeners;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.PlayerHelper;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet17;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet22Collect;
import net.minecraft.server.Packet28EntityVelocity;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet30Entity;
import net.minecraft.server.Packet34EntityTeleport;
import net.minecraft.server.Packet38EntityStatus;
import net.minecraft.server.Packet39AttachEntity;
import net.minecraft.server.Packet40EntityMetadata;
import net.minecraft.server.Packet5EntityEquipment;

public class VanishPacketListener extends PacketListener {
	private final YiffBukkit plugin;
	private PlayerHelper playerHelper;

	public VanishPacketListener(YiffBukkit instance) {
		plugin = instance;
		playerHelper = plugin.playerHelper;

		/*for (int i = 0; i < 256; ++i) {
			PacketListener.addPacketListener(true, i, this, plugin);
		}*/
		PacketListener.addPacketListener(true,  5, this, plugin);
		PacketListener.addPacketListener(true, 17, this, plugin);
		PacketListener.addPacketListener(true, 18, this, plugin);
		PacketListener.addPacketListener(true, 20, this, plugin);
		PacketListener.addPacketListener(true, 22, this, plugin);
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

	private static final String nameFromEntityId(World world, int entityID) {
		Entity entity = ((CraftWorld)world).getHandle().getEntity(entityID);
		if (!(entity instanceof EntityPlayer))
			return null;

		return ((EntityPlayer)entity).name;
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		if (playerHelper.getPlayerLevel(ply) >= 3)
			return true;

		String otherName;
		switch (packetID) {
		case 5:
			Packet5EntityEquipment p5 = (Packet5EntityEquipment) packet;
			otherName = nameFromEntityId(ply.getWorld(), p5.a);
			break;

		case 17: // Use Bed
			Packet17 p17 = (Packet17) packet;
			otherName = nameFromEntityId(ply.getWorld(), p17.a);
			break;

		case 18:
			Packet18ArmAnimation p18 = (Packet18ArmAnimation) packet;
			otherName = nameFromEntityId(ply.getWorld(), p18.a);
			break;

		case 20:
			Packet20NamedEntitySpawn p20 = (Packet20NamedEntitySpawn) packet;
			otherName = p20.b;
			break;

		case 22:
			Packet22Collect p22 = (Packet22Collect) packet;
			otherName = nameFromEntityId(ply.getWorld(), p22.b);
			break;

		case 28:
			Packet28EntityVelocity p28 = (Packet28EntityVelocity) packet;
			otherName = nameFromEntityId(ply.getWorld(), p28.a);
			break;

		case 29:
			Packet29DestroyEntity p29 = (Packet29DestroyEntity) packet;
			otherName = nameFromEntityId(ply.getWorld(), p29.a);
			break;

		case 30:
		case 31:
		case 32:
		case 33:
			Packet30Entity p30 = (Packet30Entity) packet;
			otherName = nameFromEntityId(ply.getWorld(), p30.a);
			break;

		case 34:
			Packet34EntityTeleport p34 = (Packet34EntityTeleport) packet;
			otherName = nameFromEntityId(ply.getWorld(), p34.a);
			break;

		case 38:
			Packet38EntityStatus p38 = (Packet38EntityStatus) packet;
			otherName = nameFromEntityId(ply.getWorld(), p38.a);
			break;

		case 39:
			Packet39AttachEntity p39 = (Packet39AttachEntity) packet;
			if (playerHelper.vanishedPlayers.contains(nameFromEntityId(ply.getWorld(), p39.b)))
				return false;

			otherName = nameFromEntityId(ply.getWorld(), p39.a);
			break;

		case 40:
			Packet40EntityMetadata p40 = (Packet40EntityMetadata) packet;
			otherName = nameFromEntityId(ply.getWorld(), p40.a);
			break;

		default:
			removePacketListener(true, packetID, this);
			return true;
		}

		if (otherName == null) {
			return true;
		}

		if (playerHelper.vanishedPlayers.contains(otherName))
			return false;

		return true;
	}

}
