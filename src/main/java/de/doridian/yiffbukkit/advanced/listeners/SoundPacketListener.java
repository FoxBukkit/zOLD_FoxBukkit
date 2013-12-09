package de.doridian.yiffbukkit.advanced.listeners;

import de.doridian.yiffbukkit.advanced.packetlistener.YBPacketListener;
import de.doridian.yiffbukkit.componentsystem.YBListener;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedSoundEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SoundPacketListener extends YBPacketListener implements YBListener {
	public SoundPacketListener(YiffBukkit plugin) {
		register(PacketDirection.OUTGOING, 62);
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		final PacketPlayOutNamedSoundEffect packetPlayOutNamedSoundEffect = (PacketPlayOutNamedSoundEffect) packet;
		final Location location = ply.getLocation();

		final int x = packetPlayOutNamedSoundEffect.b; // v1_7_R1
		if (Math.abs(MathHelper.floor(location.getX() * 8.0D) - x) > 512*8)
			return false;

		final int z = packetPlayOutNamedSoundEffect.d; // v1_7_R1
		if (Math.abs(MathHelper.floor(location.getZ() * 8.0D) - z) > 512*8)
			return false;

		final String soundName = packetPlayOutNamedSoundEffect.a; // v1_7_R1
		if (!soundName.startsWith("step.") && !soundName.equals("random.splash") && !soundName.startsWith("liquid.") && !soundName.startsWith("damage."))
			return true;

		final int y = packetPlayOutNamedSoundEffect.c; // v1_7_R1

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
