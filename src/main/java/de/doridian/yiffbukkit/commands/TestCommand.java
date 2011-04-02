package de.doridian.yiffbukkit.commands;

import net.minecraft.server.Packet101CloseWindow;
import net.minecraft.server.Packet1Login;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet9Respawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class TestCommand extends ICommand {

	public int GetMinLevel() {
		return 5;
	}

	public TestCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		TeleportThread t = new TeleportThread(ply);
		t.start();
	}
	
	private class TeleportThread extends Thread {
		private Player ply;
		public TeleportThread(Player plyx) {
			ply =  plyx;
		}
		public void run() {
			try {
				Location loc = ply.getLocation();
				World tmp = plugin.GetOrCreateWorld("temp", Environment.NORMAL);
				
				ply.teleportTo(tmp.getSpawnLocation());
				Thread.sleep(100);
				playerHelper.sendPacketToPlayer(ply, new Packet1Login("","",ply.getEntityId(),1000,(byte)-1));
				Thread.sleep(100);
				ply.teleportTo(loc);
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
	}
}
