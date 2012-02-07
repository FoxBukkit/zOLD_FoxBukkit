package de.doridian.yiffbukkit.spawning.fakeentity;

import de.doridian.yiffbukkit.main.util.PlayerHelper;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet26AddExpOrb;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FakeExperienceOrb extends FakeEntity {
	public int value;

	public FakeExperienceOrb(Location location, int value) {
		super(location);

		this.value = value;
	}

	@Override
	public void send(Player player) {
		final Packet26AddExpOrb p26 = new Packet26AddExpOrb();
		p26.a = entityId;
		p26.b = MathHelper.floor(location.getX());// * 32.0D);
		p26.c = MathHelper.floor(location.getY());// * 32.0D);
		p26.d = MathHelper.floor(location.getZ());// * 32.0D);
		p26.e = value;

		PlayerHelper.sendPacketToPlayer(player, p26);
	}
}
