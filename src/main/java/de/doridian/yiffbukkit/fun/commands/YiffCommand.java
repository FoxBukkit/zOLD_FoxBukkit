package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.*;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.Packet20NamedEntitySpawn;
import net.minecraft.server.v1_7_R1.Packet29DestroyEntity;
import net.minecraft.server.v1_7_R1.Packet34EntityTeleport;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Random;

@Names({"yiff","raep"})
@Help("RAEP! -s = stop, -v = Anti-Anti-Vanish [FU NODUS c:]")
@Usage("[-sv] <target>")
@BooleanFlags("sv")
@Permission("yiffbukkit.yiffyiffyiff")
@AbusePotential
public class YiffCommand extends ICommand {
	private final static Random rand = new Random();

	private static HashMap<String, RaepRunnable> raepedPlayers = new HashMap<String, RaepRunnable>();

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		Player target = null;
		try {
			target = playerHelper.matchPlayerSingle(args[0]);
		} catch(YiffBukkitCommandException e) { throw e; } catch(Exception e) { }
		if(target == null) throw new YiffBukkitCommandException("Sorry, invalid player!");

		if(booleanFlags.contains('s')) {
			stopRaep(target);
		} else if(booleanFlags.contains('v')) {
			startRaep(target, 1);
		} else {
			startRaep(target, 0);
		}
	}

	public void startRaep(Player target, int mode) {
		stopRaep(target);
		raepedPlayers.put(target.getName().toLowerCase(), new RaepRunnable(target, mode));
	}

	public void stopRaep(Player target) {
		String name = target.getName().toLowerCase();
		if(raepedPlayers.containsKey(name)) {
			raepedPlayers.get(name).stop();
			raepedPlayers.remove(name);
		}
	}

	class RaepRunnable implements Runnable {
		private final Player target;

		private final Packet20NamedEntitySpawn packet20NamedEntitySpawn;
		private final Packet29DestroyEntity packet29DestroyEntity;
		private final Packet34EntityTeleport packet34EntityTeleport;

		private final int mode;

		private RaepRunnable(Player target, int mode) {
			this.target = target;
			// TODO: make sure packets are sent before being reused or stop reusing them.
			this.packet20NamedEntitySpawn = new Packet20NamedEntitySpawn();

			packet20NamedEntitySpawn.b = "DoriBot"; // v1_6_R2

			this.packet29DestroyEntity = new Packet29DestroyEntity(0);

			this.packet34EntityTeleport = new Packet34EntityTeleport();

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
				packet20NamedEntitySpawn.a = lastEntID; // v1_6_R2

				final double x = pos.getX() + rand.nextGaussian() * 2;
				final double y = pos.getY();
				final double z = pos.getZ() + rand.nextGaussian() * 2;

				packet20NamedEntitySpawn.c = MathHelper.floor(x * 32.0D); // v1_6_R2
				packet20NamedEntitySpawn.d = MathHelper.floor(y * 32.0D); // v1_6_R2
				packet20NamedEntitySpawn.e = MathHelper.floor(z * 32.0D); // v1_6_R2

				PlayerHelper.sendPacketToPlayer(target, packet20NamedEntitySpawn);

				if (mode == 1) {
					packet29DestroyEntity.a[0] = lastEntID; // v1_6_R2

					packet34EntityTeleport.a = lastEntID; // v1_6_R2
					packet34EntityTeleport.b = packet20NamedEntitySpawn.c; // v1_6_R2
					packet34EntityTeleport.c = packet20NamedEntitySpawn.d + 1; // v1_6_R2
					packet34EntityTeleport.d = packet20NamedEntitySpawn.e; // v1_6_R2

					PlayerHelper.sendPacketToPlayer(target, packet29DestroyEntity);

					PlayerHelper.sendPacketToPlayer(target, packet34EntityTeleport);
				}

				lastEntID++;
				if (lastEntID > 65535) lastEntID = 0; // TODO: pick a better range
				if (cancelled) return;
			}
		}
	}
}
