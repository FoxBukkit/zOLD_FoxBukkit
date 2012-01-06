package de.doridian.yiffbukkit.transmute;

import net.minecraft.server.Packet25EntityPainting;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.server.Packet;

public class PaintingShape extends EntityShape {
	static {
		yOffsets[9] = 1.62;
	}

	private String paintingName = "Kebab";

	public PaintingShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity, mobType);

		yOffsets[9] = 1.62;
		dropping = true;
	}

	@Override
	public void createTransmutedEntity() {
		super.createTransmutedEntity();

		sendYCData(ShapeYCData.PAINTING_NAME, paintingName);
	}

	@Override
	protected Packet createSpawnPacket() {
		Location location = entity.getLocation();

		final Packet25EntityPainting p25 = new Packet25EntityPainting();

		p25.a = entityId;

		p25.b = location.getBlockX();
		p25.c = (int)(location.getY() + yOffset);
		p25.d = location.getBlockZ();
		p25.e = 0;
		p25.f = paintingName;

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
}
