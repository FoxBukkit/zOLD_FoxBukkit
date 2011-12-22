package de.doridian.yiffbukkit.transmute;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet24MobSpawn;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.PlayerHelper;
import de.doridian.yiffbukkit.util.Utils;

public class MobShape extends Shape {
	int mobType;
	private Map<String, MobAction> actions;

	public MobShape(Transmute transmute, Player player, Entity entity, int mobType) {
		super(transmute, player, entity);

		this.mobType = mobType;
		actions = MobActions.get(mobType);
	}

	@Override
	public void createTransmutedEntity() {
		if (entity instanceof Player)
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, transmute.ignorePacket(createMobSpawnPacket()), (Player) entity);
		else
			transmute.plugin.playerHelper.sendPacketToPlayersAround(entity.getLocation(), 1024, transmute.ignorePacket(createMobSpawnPacket()));
	}


	@Override
	public void createTransmutedEntity(Player forPlayer) {
		PlayerHelper.sendPacketToPlayer(forPlayer, transmute.ignorePacket(createMobSpawnPacket()));
	}

	private Packet24MobSpawn createMobSpawnPacket() {
		Location location = entity.getLocation();

		final Packet24MobSpawn p24 = new Packet24MobSpawn();

		p24.a = entityId;
		p24.b = (byte) mobType;
		p24.c = MathHelper.floor(location.getX() * 32.0D);
		p24.d = MathHelper.floor(location.getY() * 32.0D);
		p24.e = MathHelper.floor(location.getZ() * 32.0D);
		p24.f = (byte) ((int) (location.getYaw() * 256.0F / 360.0F));
		p24.g = (byte) ((int) (location.getPitch() * 256.0F / 360.0F));
		Utils.setPrivateValue(Packet24MobSpawn.class, p24, "h", datawatcher);
		return p24;
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
