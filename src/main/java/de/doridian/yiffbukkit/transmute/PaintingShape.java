package de.doridian.yiffbukkit.transmute;

import net.minecraft.server.v1_4_R1.Packet;
import net.minecraft.server.v1_4_R1.Packet25EntityPainting;
import net.minecraft.server.v1_4_R1.Packet34EntityTeleport;
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
			Packet34EntityTeleport p34 = (Packet34EntityTeleport) packet;
			p34.e = (byte) -p34.e;
			return true;
		}

		return true;
	}
}
