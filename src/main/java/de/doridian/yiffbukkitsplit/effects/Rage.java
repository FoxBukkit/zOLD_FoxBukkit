package de.doridian.yiffbukkitsplit.effects;

import java.util.Random;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet32EntityLook;
import net.minecraft.server.Packet35EntityHeadRotation;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkitsplit.YiffBukkit;

@EffectProperties(
		name = "rage",
		potionColor = 12,
		radius = 3
)
public class Rage extends YBEffect {
	// TODO: area/direct hit with different lengths
	private static final int ticks = 100;

	private int i = 0;
	final Random random = new Random();

	public Rage(Entity entity) {
		super(entity);
	}

	@Override
	public void start() {
		if (!(entity instanceof CraftLivingEntity))
			return;

		scheduleSyncRepeating(0, 1);
	}

	@Override
	public void run() {
		final EntityLiving notchEntity = ((CraftLivingEntity) entity).getHandle();
		Location location = entity.getLocation();

		// damage animation
		YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new Packet18ArmAnimation(notchEntity, 2));

		byte yaw = (byte)(random.nextInt(255)-128);
		byte pitch = (byte)(random.nextInt(255)-128);
		if (entity instanceof Player) {
			// arm animation
			YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new Packet18ArmAnimation(notchEntity, 1));
			// random looking
			YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new Packet32EntityLook(entity.getEntityId(), yaw, pitch), (Player) entity);
			YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new Packet35EntityHeadRotation(entity.getEntityId(), yaw), (Player) entity);
		}
		else {
			YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new Packet32EntityLook(entity.getEntityId(), yaw, pitch), null);
			YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new Packet35EntityHeadRotation(entity.getEntityId(), (byte) random.nextInt(256)), null);
		}

		if (++i > ticks) {
			done();
			cancel();
		}
	}
}