package de.doridian.yiffbukkit.advanced.listeners;

import net.minecraft.server.Packet62NamedSoundEffect;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;

import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.YiffBukkit;

public class SoundPacketListener extends PacketListener {
	public SoundPacketListener(YiffBukkit plugin) {
		addPacketListener(true, 62, this, plugin);
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		Packet62NamedSoundEffect p62 = (Packet62NamedSoundEffect) packet;

		final String soundName = Utils.getPrivateValue(Packet62NamedSoundEffect.class, p62, "a");

		if (!soundName.startsWith("step.") && !soundName.equals("random.splash"))
			return true;

		final int x = Utils.getPrivateValue(Packet62NamedSoundEffect.class, p62, "b");
		final int y = Utils.getPrivateValue(Packet62NamedSoundEffect.class, p62, "c");
		final int z = Utils.getPrivateValue(Packet62NamedSoundEffect.class, p62, "d");

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
