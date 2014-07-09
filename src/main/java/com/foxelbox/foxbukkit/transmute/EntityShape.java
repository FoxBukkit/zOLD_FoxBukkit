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
package com.foxelbox.foxbukkit.transmute;

import com.sk89q.worldedit.blocks.BlockType;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import net.minecraft.server.v1_7_R4.MathHelper;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityVelocity;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class EntityShape extends Shape {
	protected static double[] yOffsets = new double[1024];
	protected static float[] yawOffsets = new float[1024];

	protected int mobType;
	private Map<String, ShapeAction> actions;

	protected float yawOffset = 0;
	protected double yOffset = 0;
	protected boolean dropping = false;

	public EntityShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity);

		this.mobType = mobType;
		actions = ShapeActions.get(mobType);

		yOffset = yOffsets[mobType];
		yawOffset = yawOffsets[mobType];

		if (!(entity instanceof CraftEntity))
			return;

		try {
			Class<? extends net.minecraft.server.v1_7_R4.Entity> entityClass = ((CraftEntity) entity).getHandle().getClass();
			int entityMobType = MyEntityTypes.classToId(entityClass);

			yOffset -= yOffsets[entityMobType];
			yawOffset -= yawOffsets[entityMobType];
		}
		catch (EntityTypeNotFoundException e) {
		}

		yOffset += 0.015625D;
	}

	@Override
	public void createTransmutedEntity() {
		sendPacketToPlayersAround(transmute.ignorePacket(createSpawnPacket()));

		// TODO: send datawatcher to players around

		if (entity instanceof Player) {
			try {
				String typeName = MyEntityTypes.classToTypeName(MyEntityTypes.idToClass(mobType));
			}
			catch (EntityTypeNotFoundException e) {
			}
		}
	}

	@Override
	public void createTransmutedEntity(Player forPlayer) {
		PlayerHelper.sendPacketToPlayer(forPlayer, transmute.ignorePacket(createSpawnPacket()));
		// TODO: send datawatcher to player
	}

	protected abstract Packet createSpawnPacket();

	private static final Pattern commandPattern = Pattern.compile("^([^ ]+) (.+)?$");

	@Override
	public void runAction(CommandSender commandSender, String action) throws FoxBukkitCommandException {
		final Matcher matcher = commandPattern.matcher(action);

		final String actionName;
		final String argStr;
		final String[] args;
		if (matcher.matches()) {
			actionName = matcher.group(1);
			argStr = matcher.group(2);
			args = argStr.split(" +");
		}
		else {
			actionName = action.trim();
			argStr = "";
			args = new String[0];
		}

		runAction(commandSender, actionName, args, argStr);
	}

	protected void runAction(CommandSender commandSender, final String actionName, final String[] args, final String argStr) throws FoxBukkitCommandException {
		if (actions == null)
			throw new FoxBukkitCommandException("No actions defined for your current shape.");

		ShapeAction mobAction = actions.get(actionName);
		if (mobAction == null) {
			mobAction = actions.get("help");
			if (mobAction == null)
				throw new FoxBukkitCommandException("No action named '"+actionName+"' defined for your current shape.");

			mobAction.run(this, commandSender, new String[] { "" }, "");
			return;
		}

		mobAction.run(this, commandSender, args, argStr);
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		if (ply == entity)
			return true;

		switch (packetID) {
		case 18:
			return ((PacketPlayOutAnimation) packet).b == 2; // v1_7_R1

		case 22:
			return false; // will be overridden in MobShape

		//case 30:
		//case 31:
		case 32:
		case 33:
			PacketPlayOutEntity p30 = (PacketPlayOutEntity) packet;
			p30.e += (byte) ((int) (yawOffset * 256.0F / 360.0F)); // v1_7_R1

			return true;

		case 34:
			PacketPlayOutEntityTeleport p34 = (PacketPlayOutEntityTeleport) packet;
			final net.minecraft.server.v1_7_R4.Entity notchEntity = ((CraftEntity) entity).getHandle();
			p34.c = MathHelper.floor((notchEntity.locY+yOffset) * 32.0D); // v1_7_R1
			p34.e = (byte) ((int) ((notchEntity.yaw+yawOffset) * 256.0F / 360.0F)); // v1_7_R1
			//p34.c += (int)(yOffset * 32.0);
			//p34.e += (byte) ((int) (yawOffset * 256.0F / 360.0F));

			return true;

		default:
			return true;
		}
	}

	@Override
	public void tick() {
		if (!dropping)
			return;

		final net.minecraft.server.v1_7_R4.Entity notchEntity = ((CraftEntity) entity).getHandle();
		if (yOffset == 0) {
			if (Math.IEEEremainder(notchEntity.locY, 1.0) < 0.00001) {
				final Block block = entity.getWorld().getBlockAt(Location.locToBlock(notchEntity.locX), Location.locToBlock(notchEntity.locY)-1, Location.locToBlock(notchEntity.locZ));
				if (!BlockType.canPassThrough(block.getTypeId(), block.getData()))
					return;
			}
		}

		sendPacketToPlayersAround(new PacketPlayOutEntityTeleport(notchEntity));
		sendPacketToPlayersAround(new PacketPlayOutEntityVelocity(entityId, notchEntity.motX, notchEntity.motY, notchEntity.motZ));
	}

	public double getYOffset() {
		return yOffset;
	}

	public void sendEntityStatus(byte status) {
		final PacketPlayOutEntityStatus packetPlayOutEntityStatus = new PacketPlayOutEntityStatus();
		packetPlayOutEntityStatus.a = entityId; // v1_7_R1
		packetPlayOutEntityStatus.b = status; // v1_7_R1
		sendPacketToPlayersAround(packetPlayOutEntityStatus);
	}
}
