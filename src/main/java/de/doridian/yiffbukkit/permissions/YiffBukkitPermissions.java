package de.doridian.yiffbukkit.permissions;

import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;

public class YiffBukkitPermissions {
	public static void init() {
		PermissionPlayerListener listener = new PermissionPlayerListener();
		Bukkit.getPluginManager().registerEvents(listener, YiffBukkit.instance);

		try {
			checkOffPlayers.clear();
			BufferedReader reader = new BufferedReader(new FileReader(new File(YiffBukkit.instance.getDataFolder(), "coplayers.txt")));
			String line;
			while((line = reader.readLine()) != null) {
				checkOffPlayers.add(line.toLowerCase());
			}
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static HashSet<String> checkOffPlayers = new HashSet<String>();

	public static void addCOPlayer(Player player) {
		addCOPlayer(player.getName());
	}
	public static void addCOPlayer(String player) {
		player = player.toLowerCase();
		if(!checkOffPlayers.contains(player)) {
			checkOffPlayers.add(player);
			saveCO();
		}
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
