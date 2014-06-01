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

import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityPainting;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PaintingShape extends EntityShape {
	static {
		yOffsets[9] = 1.62;
		yawOffsets[9] = 180;
	}

	private String paintingName = "Kebab";

	public PaintingShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity, mobType);

		dropping = true;
	}

	@Override
	public void createTransmutedEntity() {
		super.createTransmutedEntity();
	}

	@Override
	protected Packet createSpawnPacket() {
		Location location = entity.getLocation();

		final PacketPlayOutSpawnEntityPainting p25 = new PacketPlayOutSpawnEntityPainting();

		p25.a = entityId; // v1_7_R1

		p25.b = location.getBlockX(); // v1_7_R1
		p25.c = (int)(location.getY() + yOffset); // v1_7_R1
		p25.d = location.getBlockZ(); // v1_7_R1
		p25.e = 0; // v1_7_R1
		p25.f = paintingName; // v1_7_R1

		return p25;
	}

	public void setPaintingName(String paintingName) {
		this.paintingName = paintingName;

		deleteEntity();
		createTransmutedEntity();
	}

	public String getPaintingName() {
		return paintingName;
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		if (!super.onOutgoingPacket(ply, packetID, packet))
			return false;

		switch (packetID) {
		//case 30:
		//case 31:
		case 32:
		case 33:
			return false;

		case 34:
			PacketPlayOutEntityTeleport p34 = (PacketPlayOutEntityTeleport) packet;
			p34.e = (byte) -p34.e; // v1_7_R1
			return true;
		}

		return true;
	}
}
