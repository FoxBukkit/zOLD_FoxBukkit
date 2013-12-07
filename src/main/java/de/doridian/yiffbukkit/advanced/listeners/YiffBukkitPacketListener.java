package de.doridian.yiffbukkit.advanced.listeners;

import com.sk89q.worldedit.blocks.BlockType;
import de.doridian.yiffbukkit.advanced.packetlistener.YBPacketListener;
import de.doridian.yiffbukkit.componentsystem.YBListener;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import de.doridian.yiffbukkitsplit.util.PlayerHelper.WeatherType;
import net.minecraft.server.v1_7_R1.ControllerMove;
import net.minecraft.server.v1_7_R1.EntityCreature;
import net.minecraft.server.v1_7_R1.EntityInsentient;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutFlying;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;
import net.minecraft.server.v1_7_R1.PacketPlayOutBed;
import net.minecraft.server.v1_7_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class YiffBukkitPacketListener extends YBPacketListener implements YBListener {
	private static final double QUARTER_CIRCLE = 2.0*Math.PI/4.0;
	private final YiffBukkit plugin;
	private PlayerHelper playerHelper;

	public YiffBukkitPacketListener(YiffBukkit instance) {
		plugin = instance;
		playerHelper = plugin.playerHelper;

		register(PacketDirection.OUTGOING, 3);
		register(PacketDirection.OUTGOING, 34);
		register(PacketDirection.OUTGOING, 70);

		register(PacketDirection.INCOMING, 11);
		register(PacketDirection.INCOMING, 13);
	}

	@Override
	public boolean onOutgoingPacket(final Player ply, int packetID, Packet packet) {
		switch (packetID) {
		case 3:
			final PacketPlayOutChat p3 = (PacketPlayOutChat) packet;
			if (p3.message.contains("\"\u00a74You are in a no-PvP area.\""))
				return false;

			if (p3.message.contains("\"\u00a74That player is in a no-PvP area.\""))
				return false;

			return true;

		case 34:
			final PacketPlayOutEntityTeleport p34 = (PacketPlayOutEntityTeleport) packet;
			if (p34.a != 0 && p34.a != ply.getEntityId()) // v1_6_R2
				return true;

			final int x = MathHelper.floor(p34.b / 32.0D); // v1_6_R2
			final int y = MathHelper.floor(p34.c / 32.0D); // v1_6_R2
			final int z = MathHelper.floor(p34.d / 32.0D); // v1_6_R2

			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { @Override public void run() {
				final WorldServer notchWorld = ((CraftWorld) ply.getWorld()).getHandle();
				final PacketPlayOutBlockChangeExpress p53 = new PacketPlayOutBlockChangeExpress(x, y-1, z, notchWorld);
				PlayerHelper.sendPacketToPlayer(ply, p53);
			}});

			return true;

		case 70: {
			final PacketPlayOutBed p70 = (PacketPlayOutBed) packet;
			int reason = p70.b; // v1_6_R2
			final boolean rainState;
			if (reason == 1)
				rainState = true;
			else if (reason == 2)
				rainState = false;
			else
				return true;

			final WeatherType frozenWeather = playerHelper.frozenWeathers.get(ply.getName());

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

			final Entity vehicle = ply.getVehicle();
			if (vehicle instanceof Boat)
				break;

			final PacketPlayOutFlying p10 = (PacketPlayOutFlying) packet;

			//if (!p10.h)
				//break;

			if (p10.y != -999.0D)
				break;

			if (p10.stance != -999.0D)
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

			final ControllerMove oldController = Utils.getPrivateValue(EntityInsentient.class, notchEntity, "moveController"); // v1_6_R2
			final IdleControllerMove controller;
			if (oldController instanceof IdleControllerMove) {
				controller = (IdleControllerMove) oldController;
			}
			else {
				Utils.setPrivateValue(EntityInsentient.class, notchEntity, "moveController", controller = new IdleControllerMove(notchEntity, oldController)); // v1_6_R2
			}

			if (notchEntity instanceof EntityCreature) {
				final EntityCreature notchCreature = (EntityCreature) notchEntity;
				Utils.setPrivateValue(EntityCreature.class, notchCreature, "target", null);
				Utils.setPrivateValue(EntityCreature.class, notchCreature, "pathEntity", null);
			}

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

		@Override public boolean a() { return oldController.a(); } // v1_6_R2

		@Override public void a(double arg0, double arg1, double arg2, double arg3) { oldController.a(arg0, arg1, arg2, arg3); } // v1_6_R2

		@Override public double b() { return oldController.b(); } // v1_6_R2

		@Override
		public void c() { // v1_6_R2
			if (notchEntity.passenger == null) {
				Utils.setPrivateValue(EntityInsentient.class, notchEntity, "moveController", oldController); // v1_6_R2
				oldController.c(); // v1_6_R2
			}
		}

		

		public void jump() {
			notchEntity.getControllerJump().a(); // v1_6_R2
		}
	}
}
