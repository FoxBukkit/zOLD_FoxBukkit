package de.doridian.yiffbukkit.transmute;

import com.sk89q.worldedit.blocks.BlockType;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.PlayerHelper;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet28EntityVelocity;
import net.minecraft.server.Packet30Entity;
import net.minecraft.server.Packet34EntityTeleport;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class EntityShape extends Shape {
	protected int mobType;
	private Map<String, MobAction> actions;

	protected float yawOffset = 0;
	protected double yOffset = 0;
	protected boolean dropping = false;

	@Override
	public void createTransmutedEntity() {
		sendPacketToPlayersAround(transmute.ignorePacket(createSpawnPacket()));
	}

	public void sendPacketToPlayersAround(Packet packet) {
		if (entity instanceof Player)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, packet, (Player) entity);
		else
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, packet);
	}

	@Override
	public void createTransmutedEntity(Player forPlayer) {
		PlayerHelper.sendPacketToPlayer(forPlayer, transmute.ignorePacket(createSpawnPacket()));
	}

	protected abstract Packet createSpawnPacket();

	public EntityShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity);

		this.mobType = mobType;
		actions = MobActions.get(mobType);
	}

	private static final Pattern commandPattern = Pattern.compile("^([^ ]+) (.+)?$");

	@Override
	public void runAction(Player player, String action) throws YiffBukkitCommandException {
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

		runAction(player, actionName, args, argStr);
	}

	protected void runAction(Player player, final String actionName, final String[] args, final String argStr) throws YiffBukkitCommandException {
		if (actions == null)
			throw new YiffBukkitCommandException("No actions defined for your current shape.");

		MobAction mobAction = actions.get(actionName);
		if (mobAction == null) {
			mobAction = actions.get("help");
			if (mobAction == null)
				throw new YiffBukkitCommandException("No action named '"+actionName+"' defined for your current shape.");

			mobAction.run(this, player, new String[] { "" }, "");
			return;
		}
		

		mobAction.run(this, player, args, argStr);
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		switch (packetID) {
		case 18:
			return ((Packet18ArmAnimation) packet).b == 2;
	
		//case 30:
		//case 31:
		case 32:
		case 33:
			Packet30Entity p30 = (Packet30Entity) packet;
			p30.e += (byte) ((int) (yawOffset * 256.0F / 360.0F));
	
			return true;
	
		case 34:
			Packet34EntityTeleport p34 = (Packet34EntityTeleport) packet;
			p34.c += (int)(yOffset * 32.0);
			p34.e += (byte) ((int) (yawOffset * 256.0F / 360.0F));
	
			return true;
	
		default:
			return true;
		}
	}

	@Override
	public void tick() {
		if (!dropping)
			return;

		final net.minecraft.server.Entity notchEntity = ((CraftEntity) entity).getHandle();
		if (yOffset == 0) {
			if (Math.IEEEremainder(notchEntity.locY, 1.0) < 0.00001) {
				int id = entity.getWorld().getBlockTypeIdAt(Location.locToBlock(notchEntity.locX), Location.locToBlock(notchEntity.locY)-1, Location.locToBlock(notchEntity.locZ));
				if (!BlockType.canPassThrough(id))
					return;
			}
		}

		sendPacketToPlayersAround(new Packet34EntityTeleport(notchEntity));
		sendPacketToPlayersAround(new Packet28EntityVelocity(entityId, notchEntity.motX, notchEntity.motZ, notchEntity.motZ));
	}

	public double getYOffset() {
		return yOffset;
	}
}