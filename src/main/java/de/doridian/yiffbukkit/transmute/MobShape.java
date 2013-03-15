package de.doridian.yiffbukkit.transmute;


import de.doridian.yiffbukkit.main.util.Utils;
import net.minecraft.server.v1_5_R1.MathHelper;
import net.minecraft.server.v1_5_R1.Packet;
import net.minecraft.server.v1_5_R1.Packet24MobSpawn;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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

		final Packet24MobSpawn p24 = new Packet24MobSpawn();

		p24.a = entityId;
		p24.b = (byte) mobType;
		p24.c = MathHelper.floor(location.getX() * 32.0D);
		p24.d = MathHelper.floor((location.getY()+yOffset) * 32.0D);
		p24.e = MathHelper.floor(location.getZ() * 32.0D);
		p24.f = (byte) ((int) ((location.getYaw()+yawOffset) * 256.0F / 360.0F));
		p24.g = (byte) ((int) (location.getPitch() * 256.0F / 360.0F));
		p24.h = p24.g;
		//p24.i = 
		//p24.j = 
		//p24.k = 
		Utils.setPrivateValue(Packet24MobSpawn.class, p24, "s", datawatcher);
		return p24;
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		return packetID == 22 || super.onOutgoingPacket(ply, packetID, packet);
	}
}
