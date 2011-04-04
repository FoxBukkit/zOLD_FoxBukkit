package de.doridian.yiffbukkit;

import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.util.PlayerHelper;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IPacketListener;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet28EntityVelocity;
import net.minecraft.server.Packet30Entity;
import net.minecraft.server.Packet34EntityTeleport;

public class VanishPacketListener implements IPacketListener {
	private final YiffBukkit plugin;
	private PlayerHelper playerHelper;

	public VanishPacketListener(YiffBukkit instance) {
		plugin = instance;
		playerHelper = plugin.playerHelper;

		NetServerHandler.addPacketListener(true, 18, this);
		NetServerHandler.addPacketListener(true, 20, this);
		NetServerHandler.addPacketListener(true, 28, this);
		NetServerHandler.addPacketListener(true, 30, this);
		NetServerHandler.addPacketListener(true, 31, this);
		NetServerHandler.addPacketListener(true, 32, this);
		NetServerHandler.addPacketListener(true, 33, this);
		NetServerHandler.addPacketListener(true, 34, this);
	}

	private static final String nameFromEntityId(World world, int entityID) {
		Entity entity = ((CraftWorld)world).getHandle().a(entityID);
		if (!(entity instanceof EntityPlayer))
			return null;

		return ((EntityPlayer)entity).name;
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		if (playerHelper.GetPlayerLevel(ply) >= 4)
			return true;

		String otherName;
		switch (packetID) {
		case 18:
			Packet18ArmAnimation p18 = (Packet18ArmAnimation) packet;
			otherName = nameFromEntityId(ply.getWorld(), p18.a);
			break;

		case 20:
			Packet20NamedEntitySpawn p20 = (Packet20NamedEntitySpawn) packet;
			otherName = p20.b;
			break;

		case 28:
			Packet28EntityVelocity p28 = (Packet28EntityVelocity) packet;
			otherName = nameFromEntityId(ply.getWorld(), p28.a);
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

		default:
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
