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
package com.foxelbox.foxbukkit.advanced.listeners;

import com.sk89q.worldedit.blocks.BlockType;
import com.foxelbox.foxbukkit.advanced.packetlistener.FBPacketListener;
import com.foxelbox.foxbukkit.componentsystem.FBListener;
import com.foxelbox.foxbukkit.core.util.AutoCleanup;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.core.util.PlayerHelper.WeatherType;
import net.minecraft.server.v1_8_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftLivingEntity;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class FoxBukkitPacketListener extends FBPacketListener implements FBListener {
	private static final double QUARTER_CIRCLE = 2.0*Math.PI/4.0;

	public FoxBukkitPacketListener() {
		register(PacketDirection.OUTGOING, PacketPlayOutChat.class);
		register(PacketDirection.OUTGOING, PacketPlayOutEntityTeleport.class);
		register(PacketDirection.OUTGOING, PacketPlayOutGameStateChange.class);

		register(PacketDirection.INCOMING, PacketPlayInBlockPlace.class);

		//register(PacketDirection.INCOMING, PacketPlayInPosition.class);
		//register(PacketDirection.INCOMING, PacketPlayInPositionLook.class);
	}

	@Override
	public boolean onOutgoingPacket(final Player ply, int packetID, Packet packet) {
		switch (packetID) {
		case 3:
			final PacketPlayOutChat p3 = (PacketPlayOutChat) packet;
			final String text = p3.a.c();

			if (text.contains("You are in a no-PvP area."))
				return false;

			//noinspection RedundantIfStatement
			if (text.contains("That player is in a no-PvP area."))
				return false;

			return true;

		case 34:
			final PacketPlayOutEntityTeleport p34 = (PacketPlayOutEntityTeleport) packet;
			if (p34.a != 0 && p34.a != ply.getEntityId()) // v1_7_R1
				return true;

			final int x = MathHelper.floor(p34.b / 32.0D); // v1_7_R1
			final int y = MathHelper.floor(p34.c / 32.0D); // v1_7_R1
			final int z = MathHelper.floor(p34.d / 32.0D); // v1_7_R1

			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { @Override public void run() {
				final WorldServer notchWorld = ((CraftWorld) ply.getWorld()).getHandle();
				final PacketPlayOutBlockChange p53 = new PacketPlayOutBlockChange(notchWorld, new BlockPosition(x, y-1, z));
				PlayerHelper.sendPacketToPlayer(ply, p53);
			}});

			return true;

		case 70: {
			final PacketPlayOutGameStateChange p70 = (PacketPlayOutGameStateChange) packet;
			int reason = p70.b; // v1_7_R1?
			final boolean rainState;
			switch (reason) {
			case 1:
				rainState = false;
				break;
			case 2:
				rainState = true;
				break;
			default:
				return true;
			}

			final WeatherType frozenWeather = playerHelper.frozenWeathers.get(ply.getUniqueId());

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

	private static boolean is255OrNeg1(int value) {
		return value == -1 || value == 255;
	}

	private static final long USE_PACKET_LIMITER = 100L;

	@Override
	public boolean onIncomingPacket(final Player ply, int packetID, Packet packet) {
		switch (packetID) {
		//case 10:
		case 11:
		//case 12:
		case 13: // Disabled for now by not registering the packets
			if (!ply.isInsideVehicle())
				break;

			final Entity vehicle = ply.getVehicle();
			if (vehicle instanceof Boat)
				break;

			final PacketPlayInFlying p10 = (PacketPlayInFlying) packet;

			//if (!p10.h)
				//break;

			if (p10.y != -999.0D)
				break;

			double factor = 5D;

			Vector passengerXZVel = new Vector(p10.x, 0, p10.z);
			final double vel = passengerXZVel.length();
			if (vel < 0.01)
				break;

			if (vel > 1) factor *= 1 / vel;

			passengerXZVel = passengerXZVel.multiply(factor);

			vehicle.setVelocity(vehicle.getVelocity().add(passengerXZVel));

			if (!(vehicle instanceof CraftLivingEntity))
				break;

			final EntityLiving notchLiving = ((CraftLivingEntity) vehicle).getHandle();
			if (!(notchLiving instanceof EntityInsentient))
				break;

			final EntityInsentient notchEntity = (EntityInsentient) notchLiving;

			final ControllerMove oldController = notchEntity.moveController;
			final IdleControllerMove controller;
			if (oldController instanceof IdleControllerMove) {
				controller = (IdleControllerMove) oldController;
			}
			else {
				notchEntity.moveController = controller = new IdleControllerMove(notchEntity, oldController);
			}

			/*if (notchEntity instanceof EntityCreature) {
				final EntityCreature notchCreature = (EntityCreature) notchEntity;
				notchCreature.setGoalTarget(null);
			}*/

			final double yaw = Math.round(Math.atan2(p10.z, p10.x)/QUARTER_CIRCLE)*QUARTER_CIRCLE;
			final Vector normalizedVel = new Vector(Math.cos(yaw), 0, Math.sin(yaw));

			final Block targetBlock = ply.getVehicle().getLocation().add(normalizedVel).getBlock();
			if (BlockType.canPassThrough(targetBlock.getTypeId(), targetBlock.getData()))
				break;

			final Block blockAbove = targetBlock.getRelative(0, 1, 0);
			if (!BlockType.canPassThrough(blockAbove.getTypeId(), blockAbove.getData()))
				break;

			controller.jump();
			break;
		}

		return true;
	}

	public static class IdleControllerMove extends ControllerMove {
		private final EntityInsentient notchEntity;
		private final ControllerMove oldController;

		private IdleControllerMove(EntityInsentient notchEntity, ControllerMove oldController) {
			super(notchEntity);
			this.notchEntity = notchEntity;
			this.oldController = oldController;
		}

		@Override public boolean a() { return oldController.a(); } // v1_7_R1

		@Override public void a(double arg0, double arg1, double arg2, double arg3) { oldController.a(arg0, arg1, arg2, arg3); } // v1_7_R1

		@Override public double b() { return oldController.b(); } // v1_7_R1

		@Override
		public void c() { // v1_7_R1
			if (notchEntity.passenger == null) {
				notchEntity.moveController = oldController;
				oldController.c(); // v1_7_R1
			}
		}



		public void jump() {
			notchEntity.getControllerJump().a(); // v1_7_R1
		}
	}
}
