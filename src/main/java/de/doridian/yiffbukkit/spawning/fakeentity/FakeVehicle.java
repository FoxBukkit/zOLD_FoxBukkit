package de.doridian.yiffbukkit.spawning.fakeentity;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import net.minecraft.server.v1_7_R2.MathHelper;
import net.minecraft.server.v1_7_R2.PacketPlayOutSpawnEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FakeVehicle extends FakeEntity {
	public int vehicleTypeId;
	public int dataValue;

	public FakeVehicle(Location location, int vehicleType) {
		this(location, vehicleType, 0);
	}

	public FakeVehicle(Location location, int vehicleType, int dataValue) {
		super(location);

		this.vehicleTypeId = vehicleType;
		this.dataValue = dataValue;
	}

	@Override
	public void send(Player player) {
		final PacketPlayOutSpawnEntity p23 = new PacketPlayOutSpawnEntity();

		final Location position = player.getLocation();

		p23.a = entityId; // v1_7_R1
		p23.b = MathHelper.floor(position.getX() * 32.0D); // v1_7_R1
		p23.c = MathHelper.floor(position.getY() * 32.0D); // v1_7_R1
		p23.d = MathHelper.floor(position.getZ() * 32.0D); // v1_7_R1
		p23.h = MathHelper.d(position.getPitch() * 256.0F / 360.0F); // v1_7_R1
		p23.i = MathHelper.d(position.getYaw() * 256.0F / 360.0F); // v1_7_R1
		p23.j = vehicleTypeId; // v1_7_R1
		p23.k = dataValue; // v1_7_R1
		if (dataValue > 0) {
			final Vector velocity = getVelocity();
			double d1 = velocity.getX();
			double d2 = velocity.getY();
			double d3 = velocity.getZ();
			double d4 = 3.9D;
			if (d1 < -d4) d1 = -d4;
			if (d2 < -d4) d2 = -d4;
			if (d3 < -d4) d3 = -d4;
			if (d1 > d4) d1 = d4;
			if (d2 > d4) d2 = d4;
			if (d3 > d4) d3 = d4;
			p23.e = (int)(d1 * 8000.0D); // v1_7_R1
			p23.f = (int)(d2 * 8000.0D); // v1_7_R1
			p23.g = (int)(d3 * 8000.0D); // v1_7_R1
		}

		PlayerHelper.sendPacketToPlayer(player, p23);
	}
}
