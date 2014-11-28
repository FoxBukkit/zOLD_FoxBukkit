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
package com.foxelbox.foxbukkit.fun.commands;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.AbusePotential;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.BooleanFlags;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import com.foxelbox.foxbukkit.main.util.GameProfileUtil;
import net.minecraft.server.v1_8_R1.MathHelper;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R1.PacketPlayOutNamedEntitySpawn;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

@Names({"yiff","raep"})
@Help("RAEP! -s = stop, -v = Anti-Anti-Vanish [FU NODUS c:]")
@Usage("[-sv] <target>")
@BooleanFlags("sv")
@Permission("foxbukkit.yiffyiffyiff")
@AbusePotential
public class YiffCommand extends ICommand {
	private final static Random rand = new Random();

	private static HashMap<String, RaepRunnable> raepedPlayers = new HashMap<>();

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		args = parseFlags(args);

		Player target = null;
		try {
			target = playerHelper.matchPlayerSingle(args[0]);
		}
		catch (FoxBukkitCommandException e) {
			throw e;
		}
		catch (Exception ignored) { }

		if (target == null)
			throw new FoxBukkitCommandException("Sorry, invalid player!");

		if (booleanFlags.contains('s')) {
			stopRaep(target);
		}
		else if (booleanFlags.contains('v')) {
			startRaep(target, 1);
		}
		else {
			startRaep(target, 0);
		}
	}

	public void startRaep(Player target, int mode) {
		stopRaep(target);
		raepedPlayers.put(target.getName().toLowerCase(), new RaepRunnable(target, mode));
	}

	public void stopRaep(Player target) {
		String name = target.getName().toLowerCase();
		if (raepedPlayers.containsKey(name)) {
			raepedPlayers.get(name).stop();
			raepedPlayers.remove(name);
		}
	}

	class RaepRunnable implements Runnable {
		private final Player target;

		private final PacketPlayOutNamedEntitySpawn packet20NamedEntitySpawn;
		private final PacketPlayOutEntityDestroy packet29DestroyEntity;
		private final PacketPlayOutEntityTeleport packet34EntityTeleport;

		private final int mode;

		private RaepRunnable(Player target, int mode) {
			this.target = target;
			// TODO: make sure packets are sent before being reused or stop reusing them.
			this.packet20NamedEntitySpawn = new PacketPlayOutNamedEntitySpawn();
			Player offlinePlayer = playerHelper.getPlayerByUUID(UUID.fromString("eff24a67-0f3d-47a6-8f45-47aac90e6c9b"));
			packet20NamedEntitySpawn.b = offlinePlayer.getUniqueId();
			packet20NamedEntitySpawn.i = ((CraftPlayer)target).getHandle().getDataWatcher();
			this.packet29DestroyEntity = new PacketPlayOutEntityDestroy(0);
			this.packet34EntityTeleport = new PacketPlayOutEntityTeleport();

			this.mode = mode;

			new Thread(this).start();
		}

		private int lastEntID = 0;

		private boolean cancelled = false;

		public void stop() {
			this.cancelled = true;
		}

		@Override
		public synchronized void run() {
			if (!target.isOnline()) {
				stop();
				return;
			}

			final Location pos = target.getLocation();

			for (int i = 0; i < 100; ++i) {
				packet20NamedEntitySpawn.a = lastEntID; // v1_7_R1

				final double x = pos.getX() + rand.nextGaussian() * 2;
				final double y = pos.getY();
				final double z = pos.getZ() + rand.nextGaussian() * 2;

				packet20NamedEntitySpawn.c = MathHelper.floor(x * 32.0D); // v1_7_R1
				packet20NamedEntitySpawn.d = MathHelper.floor(y * 32.0D); // v1_7_R1
				packet20NamedEntitySpawn.e = MathHelper.floor(z * 32.0D); // v1_7_R1

				PlayerHelper.sendPacketToPlayer(target, packet20NamedEntitySpawn);

				if (mode == 1) {
					packet29DestroyEntity.a[0] = lastEntID; // v1_7_R1

					packet34EntityTeleport.a = lastEntID; // v1_7_R1
					packet34EntityTeleport.b = packet20NamedEntitySpawn.c; // v1_7_R1
					packet34EntityTeleport.c = packet20NamedEntitySpawn.d + 1; // v1_7_R1
					packet34EntityTeleport.d = packet20NamedEntitySpawn.e; // v1_7_R1

					PlayerHelper.sendPacketToPlayer(target, packet29DestroyEntity);

					PlayerHelper.sendPacketToPlayer(target, packet34EntityTeleport);
				}

				lastEntID++;
				if (lastEntID > 65535)
					lastEntID = 0; // TODO: pick a better range

				if (cancelled)
					return;
			}
		}
	}
}
