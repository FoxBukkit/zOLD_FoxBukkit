package de.doridian.yiffbukkit.transmute;

import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutSpawnEntityExperienceOrb;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ExperienceOrbShape extends EntityShape {
	static {
		yOffsets[2] = 1.62;
	}

	public ExperienceOrbShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity, mobType);

		dropping = true;
	}

	@Override
	protected Packet createSpawnPacket() {
		Location location = entity.getLocation();

		final PacketPlayOutSpawnEntityExperienceOrb p26 = new PacketPlayOutSpawnEntityExperienceOrb();

		p26.a = entityId; // v1_7_R1

		p26.b = MathHelper.floor(location.getX() * 32.0D); // v1_7_R1
		p26.c = MathHelper.floor((location.getY()+yOffset) * 32.0D); // v1_7_R1
		p26.d = MathHelper.floor(location.getZ() * 32.0D); // v1_7_R1

		p26.e = 1; // v1_7_R1

		return p26;
	}
}
