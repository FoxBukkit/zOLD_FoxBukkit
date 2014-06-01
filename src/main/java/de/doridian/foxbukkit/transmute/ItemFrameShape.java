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
package de.doridian.foxbukkit.transmute;

import net.minecraft.server.v1_7_R3.Packet;
import org.bukkit.entity.Entity;

public class ItemFrameShape extends ItemShape {
	protected byte orientation = 0;

	public ItemFrameShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity, mobType);

		dropping = false;
	}

	@Override
	protected Packet createItemMetadataPacket() {
		try {
			datawatcher.a(3, (byte)1); // v1_7_R1
			datawatcher.watch(3, (byte)0);
		} catch (Exception e) { }

		datawatcher.watch(3, orientation);
		return createMetadataPacket(2, itemStack.cloneItemStack());
	}

	public byte getOrientation() {
		return orientation;
	}

	public void setOrientation(byte orientation) {
		this.orientation = orientation;

		sendMetadataPacket();
	}
}
