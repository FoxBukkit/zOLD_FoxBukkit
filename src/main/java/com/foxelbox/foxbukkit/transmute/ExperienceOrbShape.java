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

import net.minecraft.server.v1_7_R4.MathHelper;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityExperienceOrb;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ExperienceOrbShape extends EntityShape {
	static {
		yOffsets[2] = 1.62;
	}

	public ExperienceOrbShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity, mobType);

		dropping = true;
	}

	@Override
	protected Packet createSpawnPacket() {
		Location location = entity.getLocation();

		final PacketPlayOutSpawnEntityExperienceOrb p26 = new PacketPlayOutSpawnEntityExperienceOrb();

		p26.a = entityId; // v1_7_R1

		p26.b = MathHelper.floor(location.getX() * 32.0D); // v1_7_R1
		p26.c = MathHelper.floor((location.getY()+yOffset) * 32.0D); // v1_7_R1
		p26.d = MathHelper.floor(location.getZ() * 32.0D); // v1_7_R1

		p26.e = 1; // v1_7_R1

		return p26;
	}
}
