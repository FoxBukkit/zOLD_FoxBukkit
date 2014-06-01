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
package com.foxelbox.foxbukkit.spawning.fakeentity;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityExperienceOrb;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FakeExperienceOrb extends FakeEntity {
	public int value;

	public FakeExperienceOrb(Location location, int value) {
		super(location);

		this.value = value;
	}

	@Override
	public void send(Player player) {
		final PacketPlayOutSpawnEntityExperienceOrb p26 = new PacketPlayOutSpawnEntityExperienceOrb();
		p26.a = entityId; // v1_7_R1
		p26.b = MathHelper.floor(location.getX() * 32.0D); // v1_7_R1
		p26.c = MathHelper.floor(location.getY() * 32.0D); // v1_7_R1
		p26.d = MathHelper.floor(location.getZ() * 32.0D); // v1_7_R1
		p26.e = value; // v1_7_R1

		PlayerHelper.sendPacketToPlayer(player, p26);
	}
}
