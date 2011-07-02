package de.doridian.yiffbukkit.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet32EntityLook;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("rage")
@Level(3)
@Permission("yiffbukkit.experimental.rage")
public class RageCommand extends ICommand {
	private final Set<Player> raging = Collections.synchronizedSet(new HashSet<Player>());

	@Override
	public void Run(final Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		synchronized (raging) {
			if (raging.contains(ply))
				throw new YiffBukkitCommandException("Already raging!");

			raging.add(ply);
		}

		final EntityPlayer notchPlayer = ((CraftPlayer)ply).getHandle();
		final ArrayList<Integer> taskIdContainer = new ArrayList<Integer>(1);
		final Random random = new Random();
		taskIdContainer.add(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			int i = 0;
			public void run() {
				Location location = ply.getLocation();

				// arm animation
				playerHelper.sendPacketToPlayersAround(location, 32, new Packet18ArmAnimation(notchPlayer, 1));

				// damage animation
				playerHelper.sendPacketToPlayersAround(location, 32, new Packet18ArmAnimation(notchPlayer, 2));

				// random looking
				byte yaw = (byte)(random.nextInt(255)-128);
				byte pitch = (byte)(random.nextInt(255)-128);
				playerHelper.sendPacketToPlayersAround(location, 32, new Packet32EntityLook(ply.getEntityId(), yaw, pitch), ply);

				if (++i > 100 && !taskIdContainer.isEmpty()) {
					plugin.getServer().getScheduler().cancelTask(taskIdContainer.get(0));
					synchronized (raging) {
						raging.remove(ply);
					}
				}
			}
		}, 0, 1));
	}
}
