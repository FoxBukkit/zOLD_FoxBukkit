package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Packet20NamedEntitySpawn;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

@ICommand.Names({"yiff","raep"})
@ICommand.Help("RAEP!")
@ICommand.Usage("<target>")
@ICommand.Permission("yiffbukkit.yiffyiffyiff")
public class YiffCommand extends ICommand {
	private final static Random rand = new Random();

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		Player target = null;
		try {
			target = playerHelper.matchPlayerSingle(args[0]);
		} catch(YiffBukkitCommandException e) { throw e; } catch(Exception e) { }
		if(target == null) throw new YiffBukkitCommandException("Sorry, invalid player!");

		new RaepRunnable(target);
	}

	class RaepRunnable implements Runnable {
		private final Player target;
		private final int taskID;
		private final Packet20NamedEntitySpawn packet;
		private RaepRunnable(Player target) {
			this.target = target;
			this.taskID = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, this, 0, 1);
			this.packet = new Packet20NamedEntitySpawn();

			packet.b = "DoriBot";
			packet.f = 0; //Yaw
			packet.g = 0; //Pitch
			packet.h = 0; //ItemID
		}

		private int lastEntID = 0;

		@Override
		public synchronized void run() {
			if(!target.isOnline()) plugin.getServer().getScheduler().cancelTask(taskID);

			Location pos = target.getLocation();

			for(int i=0;i<100;i++) {
				packet.a = lastEntID;
				lastEntID++;
				if(lastEntID > 65535) lastEntID = 0;

				double x = (pos.getX() + (rand.nextGaussian() * 2.0D));
				double z = (pos.getZ() + (rand.nextGaussian() * 2.0D));
				packet.c = MathHelper.floor(x * 32.0D);
				packet.e = MathHelper.floor(z * 32.0D);
				packet.d = MathHelper.floor(pos.getY() * 32.0D);

				PlayerHelper.sendPacketToPlayer(target, packet);
			}
		}
	}
}
