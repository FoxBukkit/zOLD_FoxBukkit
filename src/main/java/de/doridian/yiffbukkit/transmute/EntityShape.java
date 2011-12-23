package de.doridian.yiffbukkit.transmute;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet30Entity;
import net.minecraft.server.Packet34EntityTeleport;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.PlayerHelper;

public abstract class EntityShape extends Shape {
	protected int mobType;
	private Map<String, MobAction> actions;

	protected float yawOffset = 0;
	protected double yOffset = 0;

	@Override
	public void createTransmutedEntity() {
		if (entity instanceof Player)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, transmute.ignorePacket(createSpawnPacket()), (Player) entity);
		else
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, transmute.ignorePacket(createSpawnPacket()));
	}

	@Override
	public void createTransmutedEntity(Player forPlayer) {
		PlayerHelper.sendPacketToPlayer(forPlayer, transmute.ignorePacket(createSpawnPacket()));
	}

	protected abstract Packet createSpawnPacket();
	
	public EntityShape(Transmute transmute, Player player, Entity entity, int mobType) {
		super(transmute, player, entity);
		
		this.mobType = mobType;
		actions = MobActions.get(mobType);
	}

	private static final Pattern commandPattern = Pattern.compile("^([^ ]+) (.+)?$");

	@Override
	public void runAction(String action) throws YiffBukkitCommandException {
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

		runAction(actionName, args, argStr);
	}

	protected void runAction(final String actionName, final String[] args, final String argStr) throws YiffBukkitCommandException {
		if (actions == null)
			throw new YiffBukkitCommandException("No actions defined for your current shape.");

		final MobAction mobAction = actions.get(actionName);
		if (mobAction == null)
			throw new YiffBukkitCommandException("No action named "+actionName+" defined for your current shape.");

		mobAction.run(this, args, argStr);
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

}