package de.doridian.yiffbukkit.spawning.effects;

import de.doridian.yiffbukkit.spawning.SpawnUtils;
import de.doridian.yiffbukkit.spawning.effects.system.EffectProperties;
import de.doridian.yiffbukkit.spawning.effects.system.YBEffect;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityLook;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityHeadRotation;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

@EffectProperties(
		name = "rage",
		potionColor = 12,
		potionTrail = true
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
		if (!(entity instanceof CraftLivingEntity)) {
			done();
			return;
		}

		scheduleSyncRepeating(0, 1);
	}

	@Override
	public void runEffect() {
		final EntityLiving notchEntity = ((CraftLivingEntity) entity).getHandle();
		Location location = entity.getLocation();

		// damage animation
		YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new PacketPlayOutAnimation(notchEntity, 2));

		byte yaw = (byte)(random.nextInt(255)-128);
		byte pitch = (byte)(random.nextInt(255)-128);
		if (entity instanceof Player) {
			// arm animation
			YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new PacketPlayOutAnimation(notchEntity, 1));
			// random looking
			YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new PacketPlayOutEntityLook(entity.getEntityId(), yaw, pitch), (Player) entity);
			YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new PacketPlayOutEntityHeadRotation(notchEntity, yaw), (Player) entity);
		}
		else {
			YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new PacketPlayOutEntityLook(entity.getEntityId(), yaw, pitch), null);
			YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new PacketPlayOutEntityHeadRotation(notchEntity, (byte) random.nextInt(256)), null);
		}

		if (++i > ticks) {
			done();
			cancel();
		}
	}

	public static class PotionTrail extends YBEffect.PotionTrail {
		public PotionTrail(Entity entity) {
			super(entity);
		}

		@Override
		protected void renderEffect(Location location) {
			SpawnUtils.makeParticles(location, new Vector(.1, .1, .1), 0, 10, "flame");
		}
	}
}
