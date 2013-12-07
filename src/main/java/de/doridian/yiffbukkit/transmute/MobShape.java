package de.doridian.yiffbukkit.transmute;


import de.doridian.yiffbukkit.main.util.Utils;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutMobSpawn;
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

		final PacketPlayOutMobSpawn p24 = new PacketPlayOutMobSpawn();

		p24.a = entityId; // v1_6_R2
		p24.b = (byte) mobType; // v1_6_R2
		p24.c = MathHelper.floor(location.getX() * 32.0D); // v1_6_R2
		p24.d = MathHelper.floor((location.getY()+yOffset) * 32.0D); // v1_6_R2
		p24.e = MathHelper.floor(location.getZ() * 32.0D); // v1_6_R2
		p24.i = (byte) ((int) ((location.getYaw()+yawOffset) * 256.0F / 360.0F)); // v1_6_R2
		p24.j = (byte) ((int) (location.getPitch() * 256.0F / 360.0F)); // v1_6_R2
		p24.k = p24.i; // v1_6_R2

		final Vector velocity = entity.getVelocity();
		final double d0 = 3.9D;
		double d1 = velocity.getX();
		double d2 = velocity.getY();
		double d3 = velocity.getZ();

		if (d1 < -d0) {
			d1 = -d0;
		}

		if (d2 < -d0) {
			d2 = -d0;
		}

		if (d3 < -d0) {
			d3 = -d0;
		}

		if (d1 > d0) {
			d1 = d0;
		}

		if (d2 > d0) {
			d2 = d0;
		}

		if (d3 > d0) {
			d3 = d0;
		}

		p24.f = (int) (d1 * 8000.0D); // v1_6_R2
		p24.g = (int) (d2 * 8000.0D); // v1_6_R2
		p24.h = (int) (d3 * 8000.0D); // v1_6_R2

		Utils.setPrivateValue(PacketPlayOutMobSpawn.class, p24, "t", datawatcher); // v1_6_R2
		return p24;
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		return packetID == 22 || super.onOutgoingPacket(ply, packetID, packet);
	}
}
