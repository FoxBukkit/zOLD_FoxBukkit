package de.doridian.yiffbukkit.spawning.fakeentity;

import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_5_R1.MathHelper;
import net.minecraft.server.v1_5_R1.Packet23VehicleSpawn;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

		p23.a = entityId;
		p23.b = MathHelper.floor(position.getX() * 32.0D);
		p23.c = MathHelper.floor(position.getY() * 32.0D);
		p23.d = MathHelper.floor(position.getZ() * 32.0D);
		p23.h = vehicleTypeId;
		p23.i = dataValue;

		PlayerHelper.sendPacketToPlayer(player, p23);
	}
}
