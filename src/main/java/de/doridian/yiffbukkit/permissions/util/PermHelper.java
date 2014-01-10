package de.doridian.yiffbukkit.permissions.util;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.main.StateContainer;
import de.doridian.yiffbukkit.main.config.ConfigFileReader;
import de.doridian.yiffbukkit.main.config.ConfigFileWriter;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Enumeration;
import java.util.Hashtable;

public class PermHelper extends StateContainer {
	private Hashtable<String,String> ranktags = new Hashtable<>();
	//Ranks

	private Hashtable<String,String> playerranks = new Hashtable<>();
	public String getPlayerRank(Player ply) {
		return getPlayerRank(ply.getName());
	}
	public String getPlayerRank(String name) {
		final String rank = YiffBukkitPermissionHandler.instance.getGroup(name);
		if (rank == null)
			return "guest";

		return rank;
	}
	public void setPlayerRank(String name, String rankname) {
		if(getPlayerRank(name).equalsIgnoreCase(rankname)) return;
		YiffBukkitPermissionHandler.instance.setGroup(name, rankname);

		Player ply = Bukkit.getServer().getPlayerExact(name);
		if (ply == null) return;

		YiffBukkit.instance.playerHelper.setPlayerListName(ply);
	}

	//Permission levels
	public Hashtable<String,Integer> ranklevels = new Hashtable<>();
	public int getPlayerLevel(CommandSender ply) {
		return getPlayerLevel(ply.getName());
	}

	public int getPlayerLevel(String name) {
		if(name.equals("[CONSOLE]"))
			return 9999;

		return getRankLevel(getPlayerRank(name));
	}

	public int getRankLevel(String rankname) {
		rankname = rankname.toLowerCase();
		final Integer rankLevel = ranklevels.get(rankname);
		if (rankLevel == null)
			return 0;

		return rankLevel;
	}

	@StateContainer.Loader({ "ranks", "ranknames" })
	public void loadRanks() {
		ranklevels.clear();
		ranktags.clear();
		try {
			BufferedReader stream = new BufferedReader(new ConfigFileReader("ranks-config.txt"));
			String line; String[] split;
			while((line = stream.readLine()) != null) {
				split = line.split("=");
				ranklevels.put(split[0], Integer.valueOf(split[1]));
				ranktags.put(split[0], split[2]);
			}
			stream.close();
		}
		catch (Exception e) { }
	}
	@StateContainer.Saver({ "ranks", "ranknames", "rank_names" })
	public void saveRanks() {
		try {
			BufferedWriter stream = new BufferedWriter(new ConfigFileWriter("ranks-config.txt"));
			Enumeration<String> e = playerranks.keys();
			while(e.hasMoreElements()) {
				String key = e.nextElement();
				stream.write(key + "=" + playerranks.get(key) + "=" + ranktags.get(key));
				stream.newLine();
			}
			stream.close();
		}
		catch(Exception e) { }
	}
}
