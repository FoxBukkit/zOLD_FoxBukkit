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
package de.doridian.foxbukkit.spawning.effects;

import de.doridian.foxbukkit.advanced.packetlistener.FBPacketListener;
import de.doridian.foxbukkit.core.FoxBukkit;
import de.doridian.foxbukkit.spawning.effects.system.EffectProperties;
import de.doridian.foxbukkit.spawning.effects.system.FBEffect;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_7_R3.PacketPlayOutRelEntityMove;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@EffectProperties(
		name = "redrum",
		potionColor = 8
)
public class Redrum extends FBEffect {
	static TIntHashSet rotating = new TIntHashSet();
	static boolean paused = false;

	static FBPacketListener packetListener = new FBPacketListener() {
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
		final net.minecraft.server.v1_7_R3.Entity notchEntity = ((CraftEntity) entity).getHandle();

		paused = true;
		FoxBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new PacketPlayOutEntityHeadRotation(notchEntity, yaw), except);
		FoxBukkit.instance.playerHelper.sendPacketToPlayersAround(location, 32, new PacketPlayOutRelEntityMove(entity.getEntityId(), (byte) 0, (byte) 0, entz), except);
		paused = false;

		if (++i > ticks) {
			done();
			cancel();
			cleanup();
		}
	}
}
