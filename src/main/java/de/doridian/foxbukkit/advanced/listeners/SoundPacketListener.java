/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.foxbukkit.advanced.listeners;

import de.doridian.foxbukkit.advanced.packetlistener.FBPacketListener;
import de.doridian.foxbukkit.componentsystem.FBListener;
import de.doridian.foxbukkit.core.FoxBukkit;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutNamedSoundEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SoundPacketListener extends FBPacketListener implements FBListener {
	public SoundPacketListener(FoxBukkit plugin) {
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
