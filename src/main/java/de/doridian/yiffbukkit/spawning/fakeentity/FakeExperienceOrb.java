package de.doridian.yiffbukkit.spawning.fakeentity;

import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.PacketPlayOutSpawnEntityExperienceOrb;
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
		final PacketPlayOutSpawnEntityExperienceOrb p26 = new PacketPlayOutSpawnEntityExperienceOrb();
		p26.a = entityId; // v1_6_R2
		p26.b = MathHelper.floor(location.getX() * 32.0D); // v1_6_R2
		p26.c = MathHelper.floor(location.getY() * 32.0D); // v1_6_R2
		p26.d = MathHelper.floor(location.getZ() * 32.0D); // v1_6_R2
		p26.e = value; // v1_6_R2

		PlayerHelper.sendPacketToPlayer(player, p26);
	}
}
