package de.doridian.yiffbukkit.permissions;

import de.doridian.yiffbukkit.permissions.listeners.PermissionPlayerListener;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

public class YiffBukkitPermissions {
	@SuppressWarnings("unused")
	private static PermissionPlayerListener playerListener;

	public static void init() {
		playerListener = new PermissionPlayerListener();

		try {
			final File file = new File(YiffBukkit.instance.getDataFolder(), "coplayers.txt");
			if (!file.exists())
				return;

			checkOffPlayers.clear();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null) {
				checkOffPlayers.add(line.toLowerCase());
			}
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

		/*new Thread() {
			@Override
			public void run() {
				Packet20NamedEntitySpawn packet20NamedEntitySpawn = new Packet20NamedEntitySpawn();

				packet20NamedEntitySpawn.b = "\u00a7kDoridian";
				packet20NamedEntitySpawn.f = 0; //Yaw
				packet20NamedEntitySpawn.g = 0; //Pitch
				packet20NamedEntitySpawn.h = 0; //ItemID

				Packet29DestroyEntity packet29DestroyEntity = new Packet29DestroyEntity();

				packet20NamedEntitySpawn.a = -1400;

				while(true) {
					for(int i=-1337;i<-337;i++) {
						packet20NamedEntitySpawn.a = i;
						for(Player ply : YiffBukkit.instance.getServer().getOnlinePlayers()) {
							if(YiffBukkit.instance.playerHelper.getPlayerLevel(ply) <= 0) {
								Location pos = ply.getLocation();
								packet20NamedEntitySpawn.c = MathHelper.floor((pos.getX() + 1024D) * 32.0D);
								packet20NamedEntitySpawn.d = MathHelper.floor((pos.getY() + 1024D) * 32.0D);
								packet20NamedEntitySpawn.e = MathHelper.floor((pos.getZ() + 1024D) * 32.0D);

								PlayerHelper.sendPacketToPlayer(ply, packet20NamedEntitySpawn);
							}
						}
						try {
							Thread.sleep(10);
						} catch(Exception e) { }
					}
					for(int i=-2666;i<-2333;i++) {
						packet29DestroyEntity.a = new int[] { i };
						for(Player ply : YiffBukkit.instance.getServer().getOnlinePlayers()) {
							PlayerHelper.sendPacketToPlayer(ply, packet29DestroyEntity);
						}
						try {
							Thread.sleep(10);
						} catch(Exception e) { }
					}
					for(int i=-2666;i<-2333;i++) {
						packet20NamedEntitySpawn.a = i;
						for(Player ply : YiffBukkit.instance.getServer().getOnlinePlayers()) {
							if(YiffBukkit.instance.playerHelper.getPlayerLevel(ply) <= 0) {
								Location pos = ply.getLocation();
								packet20NamedEntitySpawn.c = MathHelper.floor((pos.getX() + 1024D) * 32.0D);
								packet20NamedEntitySpawn.d = MathHelper.floor((pos.getY() + 1024D) * 32.0D);
								packet20NamedEntitySpawn.e = MathHelper.floor((pos.getZ() + 1024D) * 32.0D);

								PlayerHelper.sendPacketToPlayer(ply, packet20NamedEntitySpawn);
							}
						}
						try {
							Thread.sleep(10);
						} catch(Exception e) { }
					}
					for(int i=-1337;i<-337;i++) {
						packet29DestroyEntity.a = new int[] { i };
						for(Player ply : YiffBukkit.instance.getServer().getOnlinePlayers()) {
							PlayerHelper.sendPacketToPlayer(ply, packet29DestroyEntity);
						}
						try {
							Thread.sleep(10);
						} catch(Exception e) { }
					}
				}
			}
		}.start();*/
	}

	public static Set<String> checkOffPlayers = new LinkedHashSet<String>();

	public static boolean addCOPlayer(Player player) {
		return addCOPlayer(player.getName());
	}
	public static boolean addCOPlayer(String player) {
		player = player.toLowerCase();
		if(checkOffPlayers.contains(player))
			return false;

		checkOffPlayers.add(player);
		saveCO();

		return true;
	}
	public static boolean removeCOPlayer(Player player) {
		return removeCOPlayer(player.getName());
	}
	public static boolean removeCOPlayer(String player) {
		player = player.toLowerCase();
		if(checkOffPlayers.contains(player)) {
			checkOffPlayers.remove(player);
			saveCO();
			return true;
		}
		return false;
	}

	private static void saveCO() {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(new File(YiffBukkit.instance.getDataFolder(), "coplayers.txt")));
			String[] plys = checkOffPlayers.toArray(new String[checkOffPlayers.size()]);
			for(String ply : plys) {
				writer.println(ply.toLowerCase());
			}
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
