package de.doridian.yiffbukkit.advanced.listeners;

import com.sk89q.worldedit.blocks.BlockType;
import de.doridian.yiffbukkit.componentsystem.YBListener;
import de.doridian.yiffbukkit.fun.commands.PlayCommand.Packet53BlockChangeExpress;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import de.doridian.yiffbukkitsplit.util.PlayerHelper.WeatherType;
import net.minecraft.server.v1_4_5.v1_4_5.ControllerMove;
import net.minecraft.server.v1_4_5.v1_4_5.EntityCreature;
import net.minecraft.server.v1_4_5.v1_4_5.EntityLiving;
import net.minecraft.server.v1_4_5.v1_4_5.MathHelper;
import net.minecraft.server.v1_4_5.v1_4_5.Packet10Flying;
import net.minecraft.server.v1_4_5.v1_4_5.Packet34EntityTeleport;
import net.minecraft.server.v1_4_5.v1_4_5.Packet3Chat;
import net.minecraft.server.v1_4_5.v1_4_5.Packet70Bed;
import net.minecraft.server.v1_4_5.v1_4_5.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_4_5.v1_4_5.CraftWorld;
import org.bukkit.craftbukkit.v1_4_5.v1_4_5.entity.CraftLivingEntity;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;
import org.bukkit.util.Vector;

public class YiffBukkitPacketListener extends PacketListener implements YBListener {
	private static final double QUARTER_CIRCLE = 2.0*Math.PI/4.0;
	private final YiffBukkit plugin;
	private PlayerHelper playerHelper;

	public YiffBukkitPacketListener(YiffBukkit instance) {
		plugin = instance;
		playerHelper = plugin.playerHelper;

		PacketListener.addPacketListener(true, 3, this, plugin);
		PacketListener.addPacketListener(true, 34, this, plugin);
		PacketListener.addPacketListener(true, 70, this, plugin);

		//PacketListener.addPacketListener(false, 10, this, plugin);
		PacketListener.addPacketListener(false, 11, this, plugin);
		//PacketListener.addPacketListener(false, 12, this, plugin);
		PacketListener.addPacketListener(false, 13, this, plugin);
	}

	@Override
	public boolean onOutgoingPacket(final Player ply, int packetID, Packet packet) {
		switch (packetID) {
		case 3:
			final Packet3Chat p3 = (Packet3Chat) packet;
			if (p3.message.equals("\u00a74You are in a no-PvP area."))
				return false;

			if (p3.message.equals("\u00a74That player is in a no-PvP area."))
				return false;

			return true;

		case 34:
			final Packet34EntityTeleport p34 = (Packet34EntityTeleport) packet;
			if (p34.a != 0 && p34.a != ply.getEntityId())
				return true;

			final int x = MathHelper.floor(p34.b / 32.0D);
			final int y = MathHelper.floor(p34.c / 32.0D);
			final int z = MathHelper.floor(p34.d / 32.0D);

			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { @Override public void run() {
				final WorldServer notchWorld = ((CraftWorld) ply.getWorld()).getHandle();
				final Packet53BlockChangeExpress p53 = new Packet53BlockChangeExpress(x, y-1, z, notchWorld);
				PlayerHelper.sendPacketToPlayer(ply, p53);
			}});

			return true;

		case 70: {
			final Packet70Bed p70 = (Packet70Bed) packet;
			int reason = p70.b;
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

			final Packet10Flying p10 = (Packet10Flying) packet;

			/*if (!p10.h)
				break;*/

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

			final EntityLiving notchEntity = ((CraftLivingEntity) vehicle).getHandle();

			final ControllerMove oldController = Utils.getPrivateValue(EntityLiving.class, notchEntity, "moveController");
			final IdleControllerMove controller;
			if (oldController instanceof IdleControllerMove) {
				controller = (IdleControllerMove) oldController;
			}
			else {
				Utils.setPrivateValue(EntityLiving.class, notchEntity, "moveController", controller = new IdleControllerMove(notchEntity, oldController));
			}

			if (notchEntity instanceof EntityCreature) {
				Utils.setPrivateValue(EntityCreature.class, (EntityCreature) notchEntity, "target", null);
				Utils.setPrivateValue(EntityCreature.class, (EntityCreature) notchEntity, "pathEntity", null);
			}

			final double yaw = Math.round(Math.atan2(p10.z, p10.x)/QUARTER_CIRCLE)*QUARTER_CIRCLE;
			final Vector normalizedVel = new Vector(Math.cos(yaw), 0, Math.sin(yaw));

			final Block targetBlock = ply.getVehicle().getLocation().add(normalizedVel).getBlock();
			if (BlockType.canPassThrough(targetBlock.getTypeId()))
				break;

			if (!BlockType.canPassThrough(targetBlock.getRelative(0, 1, 0).getTypeId()))
				break;

			controller.jump();
			break;
		}

		return true;
	}

	public static class IdleControllerMove extends ControllerMove {
		private final EntityLiving notchEntity;
		private final ControllerMove oldController;

		private IdleControllerMove(EntityLiving notchEntity, ControllerMove oldController) {
			super(notchEntity);
			this.notchEntity = notchEntity;
			this.oldController = oldController;
		}

		@Override public boolean a() { return oldController.a(); }

		@Override public void a(double arg0, double arg1, double arg2, float arg3) { oldController.a(arg0, arg1, arg2, arg3); }

		@Override public float b() { return oldController.b(); }

		@Override
		public void c() {
			if (notchEntity.passenger == null) {
				Utils.setPrivateValue(EntityLiving.class, notchEntity, "moveController", oldController);
				oldController.c();
			}
		}

		public void jump() {
			notchEntity.getControllerJump().a();
		}
	}
}
