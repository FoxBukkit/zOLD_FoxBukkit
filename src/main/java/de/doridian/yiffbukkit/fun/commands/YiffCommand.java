package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet34EntityTeleport;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Random;

@ICommand.Names({"yiff","raep"})
@ICommand.Help("RAEP! -s = stop, -v = Anti-Anti-Vanish [FU NODUS c:]")
@ICommand.Usage("[-sv] <target>")
@ICommand.BooleanFlags("sv")
@ICommand.Permission("yiffbukkit.yiffyiffyiff")
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
		private final int taskID;

		private final Packet20NamedEntitySpawn packet20NamedEntitySpawn;
		private final Packet29DestroyEntity packet29DestroyEntity;
		private final Packet34EntityTeleport packet34EntityTeleport;

		private final int mode;

		private RaepRunnable(Player target, int mode) {
			this.target = target;
			this.taskID = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, this, 0, 1);
			this.packet20NamedEntitySpawn = new Packet20NamedEntitySpawn();

			packet20NamedEntitySpawn.b = "DoriBot";
			packet20NamedEntitySpawn.f = 0; //Yaw
			packet20NamedEntitySpawn.g = 0; //Pitch
			packet20NamedEntitySpawn.h = 0; //ItemID

			this.packet29DestroyEntity = new Packet29DestroyEntity();

			this.packet34EntityTeleport = new Packet34EntityTeleport();
			packet34EntityTeleport.f = 0;
			packet34EntityTeleport.e = 0;

			this.mode = mode;
		}

		private int lastEntID = 0;

		public void stop() {
			plugin.getServer().getScheduler().cancelTask(taskID);
		}

		@Override
		public synchronized void run() {
			if(!target.isOnline()) stop();

			Location pos = target.getLocation();

			for(int i=0;i<100;i++) {
				packet20NamedEntitySpawn.a = lastEntID;

				double x = (pos.getX() + (rand.nextGaussian() * 2.0D));
				double z = (pos.getZ() + (rand.nextGaussian() * 2.0D));
				packet20NamedEntitySpawn.c = MathHelper.floor(x * 32.0D);
				packet20NamedEntitySpawn.e = MathHelper.floor(z * 32.0D);
				packet20NamedEntitySpawn.d = MathHelper.floor(pos.getY() * 32.0D);

				PlayerHelper.sendPacketToPlayer(target, packet20NamedEntitySpawn);

				if(mode == 1) {
					packet29DestroyEntity.a = new int[] { lastEntID };

					packet34EntityTeleport.a = lastEntID;
					packet34EntityTeleport.b = packet20NamedEntitySpawn.c;
					packet34EntityTeleport.c = packet20NamedEntitySpawn.d + 1;
					packet34EntityTeleport.d = packet20NamedEntitySpawn.e;

					PlayerHelper.sendPacketToPlayer(target, packet29DestroyEntity);

					PlayerHelper.sendPacketToPlayer(target, packet34EntityTeleport);
				}

				lastEntID++;
				if(lastEntID > 65535) lastEntID = 0;
			}
		}
	}
}
