package de.doridian.yiffbukkit.commands;

import net.minecraft.server.Packet101CloseWindow;
import net.minecraft.server.Packet9Respawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitPlayerListener;

public class TestCommand extends ICommand {

	@Override
	public int GetMinLevel() {
		return 5;
	}

	public TestCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	@Override
	public void Run(final Player ply, String[] args, String argStr) {
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					Location loc = ply.getLocation();
					World tmp = plugin.GetOrCreateWorld("temp", Environment.NORMAL);

					ply.teleport(tmp.getSpawnLocation());
					Thread.sleep(100);
					//playerHelper.sendPacketToPlayer(ply, new Packet1Login("","",ply.getEntityId(),1000,(byte)-1));
					Thread.sleep(100);
					ply.teleport(loc);
					Thread.sleep(100);
					playerHelper.sendPacketToPlayer(ply, new Packet9Respawn());
					Thread.sleep(100);
					playerHelper.sendPacketToPlayer(ply, new Packet101CloseWindow());
					Thread.sleep(2000);
					playerHelper.sendPacketToPlayer(ply, new Packet101CloseWindow());
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
}
