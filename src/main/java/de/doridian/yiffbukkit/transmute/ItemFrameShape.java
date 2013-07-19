package de.doridian.yiffbukkit.transmute;

import net.minecraft.server.v1_6_R2.Packet;
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
			datawatcher.a(3, (byte)1);
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
