package de.doridian.yiffbukkit.transmute;

import net.minecraft.server.v1_4_6.v1_4_6.MathHelper;
import net.minecraft.server.v1_4_6.v1_4_6.Packet26AddExpOrb;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.server.Packet;

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

		final Packet26AddExpOrb p26 = new Packet26AddExpOrb();

		p26.a = entityId;

		p26.b = MathHelper.floor(location.getX() * 32.0D);
		p26.c = MathHelper.floor((location.getY()+yOffset) * 32.0D);
		p26.d = MathHelper.floor(location.getZ() * 32.0D);

		p26.e = 1;

		return p26;
	}
}
