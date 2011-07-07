package de.doridian.yiffbukkit.transmute;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityTypes;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet24MobSpawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.Utils;

public class MobShape extends Shape {
	int mobType;
	private Map<String, MobAction> actions;

	protected MobShape(Transmute transmute, Player player, String mobType) {
		this(transmute, player, typeNameToClass(mobType));
	}

	protected MobShape(Transmute transmute, Player player, Class<? extends net.minecraft.server.Entity> mobType) {
		this(transmute, player, classToId(mobType));
	}

	public MobShape(Transmute transmute, Player player, int mobType) {
		super(transmute, player);

		this.mobType = mobType;
		actions = MobActions.get(mobType);
	}

	@Override
	public void createTransmutedEntity() {
		transmute.plugin.playerHelper.sendPacketToPlayersAround(player.getLocation(), 1024, createMobSpawnPacket(), player);
	}


	@Override
	public void createTransmutedEntity(Player forPlayer) {
		transmute.plugin.playerHelper.sendPacketToPlayer(forPlayer, createMobSpawnPacket());
	}

	private Packet24MobSpawn createMobSpawnPacket() {
		Location location = player.getLocation();

		final Packet24MobSpawn p24 = new Packet24MobSpawn();

		p24.a = entityID;
		p24.b = (byte) mobType;
		p24.c = MathHelper.floor(location.getX() * 32.0D);
		p24.d = MathHelper.floor(location.getY() * 32.0D);
		p24.e = MathHelper.floor(location.getZ() * 32.0D);
		p24.f = (byte) ((int) (location.getYaw() * 256.0F / 360.0F));
		p24.g = (byte) ((int) (location.getPitch() * 256.0F / 360.0F));
		Utils.setPrivateValue(Packet24MobSpawn.class, p24, "h", datawatcher);
		return p24;
	}

	private static final Class<? extends net.minecraft.server.Entity> typeNameToClass(String mobType) {
		Map<String, Class<? extends net.minecraft.server.Entity>> typeNameToClass = Utils.getPrivateValue(EntityTypes.class, null, "a");

		for (Entry<String, Class<? extends Entity>> entry : typeNameToClass.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(mobType))
				return entry.getValue();
		}

		return null;
		//return typeNameToClass.get(mobType);
	}
	private static final int classToId(Class<? extends Entity> mobType) {
		Map<Class<? extends net.minecraft.server.Entity>, Integer> classToId = Utils.getPrivateValue(EntityTypes.class, null, "d");

		return classToId.get(mobType);
	}

	private static final Pattern commandPattern = Pattern.compile("^([^ ]+) (.+)?$");

	@Override
	public void runAction(String action) throws YiffBukkitCommandException {
		if (actions == null)
			throw new YiffBukkitCommandException("No actions defined for your current shape.");

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

		final MobAction mobAction = actions.get(actionName);
		if (mobAction == null)
			throw new YiffBukkitCommandException("No action named "+actionName+" defined for your current shape.");

		mobAction.run(this, args, argStr);
	}
}
