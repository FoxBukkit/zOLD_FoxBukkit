package de.doridian.yiffbukkit.spawning.effects;

import de.doridian.yiffbukkit.advanced.packetlistener.YBPacketListener;
import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.spawning.effects.system.EffectProperties;
import de.doridian.yiffbukkit.spawning.effects.system.YBEffect;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_7_R1.PacketPlayOutRelEntityMove;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@EffectProperties(
		name = "redrum",
		potionColor = 8
)
public class Redrum extends YBEffect {
	static TIntHashSet rotating = new TIntHashSet();
	static boolean paused = false;

	static YBPacketListener packetListener = new YBPacketListener() {
		{
			register(PacketDirection.OUTGOING, 35);
		}

		@Override
		public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
			if (paused) return true;

			return !rotating.contains(((PacketPlayOutEntityHeadRotation) packet).a); // v1_7_R1
		}
	};

	// TODO: area/direct hit with different lengths
	private static final int ticks = 100;

	private int i = 0;
	private byte startYaw;

	public Redrum(Entity entity) {
		super(entity);
	}

	@Override
	public void start() {
		if (!(entity instanceof CraftLivingEntity)) {
			done();
			return;
		}

		rotating.add(entity.getEntityId());

		startYaw = (byte) MathHelper.d(entity.getLocation().getYaw() * 256.0F / 360.0F);
		scheduleSyncRepeating(0, 1);
	}

	@Override
	protected void cleanup() {
		rotating.remove(entity.getEntityId());
	}

	@Override
	public void runEffect() {
		Location location = entity.getLocation();

		byte yaw = (byte) (i*255*3/ticks+startYaw);
		final byte entz = (byte) (i%2*2-1);
		final Player except = entity instanceof Player ? (Player) entity : null;
		final net.minecraft.server.v1_7_R1.Entity notchEntity = ((CraftEntity) entity).getHandle();

		paused = true;
		YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new PacketPlayOutEntityHeadRotation(notchEntity, yaw), except);
		YiffBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new PacketPlayOutRelEntityMove(entity.getEntityId(), (byte) 0, (byte) 0, entz), except);
		paused = false;

		if (++i > ticks) {
			done();
			cancel();
			cleanup();
		}
	}
}
