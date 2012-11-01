package de.doridian.yiffbukkit.advanced.listeners;

import java.lang.reflect.Field;

import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet62NamedSoundEffect;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;

import de.doridian.yiffbukkit.componentsystem.YBListener;
import de.doridian.yiffbukkitsplit.YiffBukkit;

public class SoundPacketListener extends PacketListener implements YBListener {
	public SoundPacketListener(YiffBukkit plugin) {
		addPacketListener(true, 62, this, plugin);
	}

	private static final Field Packet62NamedSoundEffect_a;
	private static final Field Packet62NamedSoundEffect_b;
	private static final Field Packet62NamedSoundEffect_c;
	private static final Field Packet62NamedSoundEffect_d;
	static {
		try {
			Packet62NamedSoundEffect_a = Packet62NamedSoundEffect.class.getDeclaredField("a");
			Packet62NamedSoundEffect_b = Packet62NamedSoundEffect.class.getDeclaredField("b");
			Packet62NamedSoundEffect_c = Packet62NamedSoundEffect.class.getDeclaredField("c");
			Packet62NamedSoundEffect_d = Packet62NamedSoundEffect.class.getDeclaredField("d");
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		Packet62NamedSoundEffect_a.setAccessible(true);
		Packet62NamedSoundEffect_b.setAccessible(true);
		Packet62NamedSoundEffect_c.setAccessible(true);
		Packet62NamedSoundEffect_d.setAccessible(true);
	}
	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		final int x;
		try {
			x = (Integer) Packet62NamedSoundEffect_b.get(packet);
		} catch (IllegalAccessException e) {
			return true;
		}

		final Location location = ply.getLocation();
		if (Math.abs(MathHelper.floor(location.getX() * 8.0D) - x) > 512*8)
			return false;

		final int z;
		try {
			z = (Integer) Packet62NamedSoundEffect_d.get(packet);
		} catch (IllegalAccessException e) {
			return true;
		}
		if (Math.abs(MathHelper.floor(location.getZ() * 8.0D) - z) > 512*8)
			return false;

		final String soundName;
		try {
			soundName = (String) Packet62NamedSoundEffect_a.get(packet);
		} catch (IllegalAccessException e) {
			return true;
		}

		if (!soundName.startsWith("step.") && !soundName.equals("random.splash") && !soundName.startsWith("liquid.") && !soundName.startsWith("damage."))
			return true;

		final int y;
		try {
			y = (Integer) Packet62NamedSoundEffect_c.get(packet);
		} catch (IllegalAccessException e) {
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
