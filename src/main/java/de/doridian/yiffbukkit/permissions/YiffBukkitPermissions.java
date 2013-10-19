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
	public static void init() {
		new PermissionPlayerListener();

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
	}

	public static Set<String> checkOffPlayers = new LinkedHashSet<>();

	public static boolean addCOPlayer(Player player) {
		return addCOPlayer(player.getName());
	}
	public static boolean addCOPlayer(String playerName) {
		playerName = playerName.toLowerCase();
		if(checkOffPlayers.contains(playerName))
			return false;

		checkOffPlayers.add(playerName);
		saveCO();

		return true;
	}
	public static boolean removeCOPlayer(Player player) {
		return removeCOPlayer(player.getName());
	}
	public static boolean removeCOPlayer(String playerName) {
		playerName = playerName.toLowerCase();
		if (!checkOffPlayers.contains(playerName))
			return false;

		checkOffPlayers.remove(playerName);
		saveCO();
		return true;
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
