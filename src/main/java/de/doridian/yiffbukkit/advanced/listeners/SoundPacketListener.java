package de.doridian.yiffbukkit.advanced.listeners;

import de.doridian.yiffbukkit.advanced.packetlistener.YBPacketListener;
import de.doridian.yiffbukkit.componentsystem.YBListener;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedSoundEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class SoundPacketListener extends YBPacketListener implements YBListener {
	public SoundPacketListener(YiffBukkit plugin) {
		register(PacketDirection.OUTGOING, 62);
	}

	private static final Field PacketPlayOutNamedSoundEffect_soundName;
	private static final Field PacketPlayOutNamedSoundEffect_effectX;
	private static final Field PacketPlayOutNamedSoundEffect_effectY;
	private static final Field PacketPlayOutNamedSoundEffect_effectZ;
	static {
		try {
			PacketPlayOutNamedSoundEffect_soundName = PacketPlayOutNamedSoundEffect.class.getDeclaredField("a"); // v1_6_R2
			PacketPlayOutNamedSoundEffect_effectX = PacketPlayOutNamedSoundEffect.class.getDeclaredField("b"); // v1_6_R2
			PacketPlayOutNamedSoundEffect_effectY = PacketPlayOutNamedSoundEffect.class.getDeclaredField("c"); // v1_6_R2
			PacketPlayOutNamedSoundEffect_effectZ = PacketPlayOutNamedSoundEffect.class.getDeclaredField("d"); // v1_6_R2
		}
		catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}

		PacketPlayOutNamedSoundEffect_soundName.setAccessible(true);
		PacketPlayOutNamedSoundEffect_effectX.setAccessible(true);
		PacketPlayOutNamedSoundEffect_effectY.setAccessible(true);
		PacketPlayOutNamedSoundEffect_effectZ.setAccessible(true);
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		final int x;
		try {
			x = (Integer) PacketPlayOutNamedSoundEffect_effectX.get(packet);
		}
		catch (IllegalAccessException e) {
			return true;
		}

		final Location location = ply.getLocation();
		if (Math.abs(MathHelper.floor(location.getX() * 8.0D) - x) > 512*8)
			return false;

		final int z;
		try {
			z = (Integer) PacketPlayOutNamedSoundEffect_effectZ.get(packet);
		}
		catch (IllegalAccessException e) {
			return true;
		}
		if (Math.abs(MathHelper.floor(location.getZ() * 8.0D) - z) > 512*8)
			return false;

		final String soundName;
		try {
			soundName = (String) PacketPlayOutNamedSoundEffect_soundName.get(packet);
		}
		catch (IllegalAccessException e) {
			return true;
		}

		if (!soundName.startsWith("step.") && !soundName.equals("random.splash") && !soundName.startsWith("liquid.") && !soundName.startsWith("damage."))
			return true;

		final int y;
		try {
			y = (Integer) PacketPlayOutNamedSoundEffect_effectY.get(packet);
		}
		catch (IllegalAccessException e) {
			return true;
		}

		for (Player otherply : ply.getWorld().getPlayers()) {
			if (ply.canSee(otherply))
				continue;

			final Location otherLocation = otherply.getLocation();
			if ((int)(otherLocation.getX() * 8.0D) != x)
				continue;

			if ((int)(otherLocation.getZ() * 8.0D) != z)
				continue;

			if ((int)(otherLocation.getY() * 8.0D) != y)
				continue;

			return false;
		}

		return true;
	}
}
