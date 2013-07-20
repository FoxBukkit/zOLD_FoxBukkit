package de.doridian.yiffbukkit.spawning.fakeentity;

import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_6_R2.MathHelper;
import net.minecraft.server.v1_6_R2.Packet23VehicleSpawn;
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
		final Packet23VehicleSpawn p23 = new Packet23VehicleSpawn();

		final Location position = player.getLocation();

		p23.a = entityId; // v1_6_R2
		p23.b = MathHelper.floor(position.getX() * 32.0D); // v1_6_R2
		p23.c = MathHelper.floor(position.getY() * 32.0D); // v1_6_R2
		p23.d = MathHelper.floor(position.getZ() * 32.0D); // v1_6_R2
		p23.h = MathHelper.d(position.getPitch() * 256.0F / 360.0F); // v1_6_R2
		p23.i = MathHelper.d(position.getYaw() * 256.0F / 360.0F); // v1_6_R2
		p23.j = vehicleTypeId; // v1_6_R2
		p23.k = dataValue; // v1_6_R2
		if (dataValue > 0) {
			final Vector velocity = getVelocity();
			double d0 = velocity.getX();
			double d1 = velocity.getY();
			double d2 = velocity.getZ();
			double d3 = 3.9D;

			if (d0 < -d3) {
				d0 = -d3;
			}

			if (d1 < -d3) {
				d1 = -d3;
			}

			if (d2 < -d3) {
				d2 = -d3;
			}

			if (d0 > d3) {
				d0 = d3;
			}

			if (d1 > d3) {
				d1 = d3;
			}

			if (d2 > d3) {
				d2 = d3;
			}

			p23.e = (int) (d0 * 8000.0D); // v1_6_R2
			p23.f = (int) (d1 * 8000.0D); // v1_6_R2
			p23.g = (int) (d2 * 8000.0D); // v1_6_R2
		}

		PlayerHelper.sendPacketToPlayer(player, p23);
	}
}
