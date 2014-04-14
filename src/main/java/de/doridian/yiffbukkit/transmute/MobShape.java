package de.doridian.yiffbukkit.transmute;


import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MobShape extends EntityShape {
	static {
		yawOffsets[63] = 180;
	}

	public MobShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity, mobType);

		switch (mobType) {
		case 56: // Ghast
		case 63: // EnderDragon
		case 94: // Squid
			dropping = false;
			break;

		default:
			dropping = true;
		}
	}

	@Override
	protected Packet createSpawnPacket() {
		Location location = entity.getLocation();

		final PacketPlayOutSpawnEntityLiving p24 = new PacketPlayOutSpawnEntityLiving();

		p24.a = entityId; // v1_7_R1
		p24.b = (byte) mobType; // v1_7_R1
		p24.c = MathHelper.floor(location.getX() * 32.0D); // v1_7_R1
		p24.d = MathHelper.floor((location.getY()+yOffset) * 32.0D); // v1_7_R1
		p24.e = MathHelper.floor(location.getZ() * 32.0D); // v1_7_R1
		p24.i = (byte) ((int) ((location.getYaw()+yawOffset) * 256.0F / 360.0F)); // v1_7_R1
		p24.j = (byte) ((int) (location.getPitch() * 256.0F / 360.0F)); // v1_7_R1
		p24.k = p24.i; // v1_7_R1

		final Vector velocity = entity.getVelocity();
		double d1 = 3.9D;
		double d2 = velocity.getX();
		double d3 = velocity.getY();
		double d4 = velocity.getZ();

		if (d2 < -d1) d2 = -d1;
		if (d3 < -d1) d3 = -d1;
		if (d4 < -d1) d4 = -d1;
		if (d2 > d1) d2 = d1;
		if (d3 > d1) d3 = d1;
		if (d4 > d1) d4 = d1;
		p24.f = (int)(d2 * 8000.0D); // v1_7_R1
		p24.g = (int)(d3 * 8000.0D); // v1_7_R1
		p24.h = (int)(d4 * 8000.0D); // v1_7_R1

		p24.l = datawatcher; // v1_7_R1
		return p24;
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		return packetID == 22 || super.onOutgoingPacket(ply, packetID, packet);
	}
}
