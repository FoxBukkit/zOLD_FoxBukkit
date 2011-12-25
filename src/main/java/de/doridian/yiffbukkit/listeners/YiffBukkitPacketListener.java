package de.doridian.yiffbukkit.listeners;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.PlayerHelper;
import de.doridian.yiffbukkit.util.PlayerHelper.WeatherType;
import de.doridian.yiffbukkit.util.Utils;
import net.minecraft.server.EntityWolf;
import net.minecraft.server.Packet10Flying;
import net.minecraft.server.Packet38EntityStatus;
import net.minecraft.server.Packet3Chat;
import net.minecraft.server.Packet4UpdateTime;
import net.minecraft.server.Packet70Bed;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;
import org.bukkit.util.Vector;

public class YiffBukkitPacketListener extends PacketListener {
	private final YiffBukkit plugin;
	private PlayerHelper playerHelper;

	public YiffBukkitPacketListener(YiffBukkit instance) {
		plugin = instance;
		playerHelper = plugin.playerHelper;

		PacketListener.addPacketListener(true, 3, this, plugin);
		PacketListener.addPacketListener(true, 4, this, plugin);
		PacketListener.addPacketListener(true, 70, this, plugin);

		//PacketListener.addPacketListener(false, 10, this, plugin);
		PacketListener.addPacketListener(false, 11, this, plugin);
		//PacketListener.addPacketListener(false, 12, this, plugin);
		PacketListener.addPacketListener(false, 13, this, plugin);
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		switch (packetID) {
		case 3:
			Packet3Chat p3 = (Packet3Chat) packet;
			if (p3.message.equals("\u00a74You are in a no-PvP area."))
				return false;

			return true;

		case 4:
			Packet4UpdateTime p4 = (Packet4UpdateTime) packet;
			Long frozenTime = playerHelper.frozenTimes.get(ply.getName());

			if (frozenTime != null) {
				p4.a = frozenTime;
			}
			else if (playerHelper.frozenServerTime != null) {
				p4.a = playerHelper.frozenServerTime;
			}

			return true;

		case 38: {
			if (!ply.getWorld().hasStorm())
				return true;

			WeatherType frozenWeather = playerHelper.frozenWeathers.get(ply.getName());

			if (frozenWeather != null) {
				if (frozenWeather != WeatherType.CLEAR) {
					return true;
				}
			}
			else if (playerHelper.frozenServerWeather != null) {
				if (playerHelper.frozenServerWeather != WeatherType.CLEAR) {
					return true;
				}
			}

			Packet38EntityStatus p38 = (Packet38EntityStatus) packet;
			if (p38.b != 8) // shaking
				return true;

			final int entityId = p38.a;
			net.minecraft.server.Entity notchEntity = ((CraftWorld)ply.getWorld()).getHandle().getEntity(entityId);
			if (notchEntity instanceof EntityWolf)
				return false;

			return true;
		}

		case 70: {
			Packet70Bed p70 = (Packet70Bed) packet;
			int reason = p70.b;
			final boolean rainState;
			if (reason == 1)
				rainState = true;
			else if (reason == 2)
				rainState = false;
			else
				return true;

			WeatherType frozenWeather = playerHelper.frozenWeathers.get(ply.getName());

			if (frozenWeather != null) {
				final boolean frozenRainState = frozenWeather != WeatherType.CLEAR;
				if (rainState != frozenRainState) {
					return false;
				}
			}
			else if (playerHelper.frozenServerWeather != null) {
				final boolean frozenRainState = playerHelper.frozenServerWeather != WeatherType.CLEAR;
				if (rainState != frozenRainState) {
					return false;
				}
			}

			return true;
		}
		}

		return true;
	}

	@Override
	public boolean onIncomingPacket(Player ply, int packetID, Packet packet) {
		switch (packetID) {
		//case 10:
		case 11:
		//case 12:
		case 13:
			if (!ply.isInsideVehicle())
				break;

			final Entity vehicle = Utils.getVehicle(ply);

			final Packet10Flying p10 = (Packet10Flying) packet;

			/*if (!p10.h)
				break;*/

			if (p10.y != -999.0D)
				break;

			if (p10.stance != -999.0D)
				break;

			double factor = 5D;

			//System.out.println(p10.y);
			Vector passengerXZVel = new Vector(p10.x, 0, p10.z);
			double vel = passengerXZVel.length();
			if (vel > 1) factor *= 1 / vel;
			passengerXZVel = passengerXZVel.multiply(factor);
			vehicle.setVelocity(vehicle.getVelocity().add(passengerXZVel));
			//System.out.println(passengerXZVel);
			break;
		}

		return true;
	}
}
