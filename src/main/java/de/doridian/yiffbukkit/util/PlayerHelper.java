package de.doridian.yiffbukkit.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.server.Packet;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.YiffBukkit;

public class PlayerHelper {
	private YiffBukkit plugin;
	public PlayerHelper(YiffBukkit plug) {
		plugin = plug;
		ReloadAll();
	}

	public void ReloadAll() {
		LoadRanks();
		LoadPlayerRanks();
		LoadPlayerTags();
		LoadPlayerHomePositions();
		LoadPortPermissions();
	}

	public Player MatchPlayerSingle(String subString) throws PlayerNotFoundException, MultiplePlayersFoundException {
		java.util.List<Player> otherplys = plugin.getServer().matchPlayer(subString);
		int c = otherplys.size();
		if(c <= 0)
			throw new PlayerNotFoundException();

		if(c > 1)
			throw new MultiplePlayersFoundException();

		return otherplys.get(0);
	}

	public String CompletePlayerName(String subString, boolean implicitlyLiteralNames) {
		Matcher matcher = Pattern.compile("^\"(.*)\"$").matcher(subString);

		if (matcher.matches())
			return matcher.group(1);

		java.util.List<Player> otherplys = plugin.getServer().matchPlayer(subString);
		int c = otherplys.size();

		if (c == 0 && implicitlyLiteralNames)
			return subString;

		if (c == 1)
			return otherplys.get(0).getName();

		return null;
	}

	public String GetFullPlayerName(Player ply) {
		return GetPlayerTag(ply) + ply.getName();
	}

	//Home position stuff
	private Hashtable<String,Location> playerhomepos = new Hashtable<String,Location>();
	public Location GetPlayerHomePosition(Player ply) {
		String name = ply.getName().toLowerCase();
		if(playerhomepos.containsKey(name))
			return playerhomepos.get(name);
		else
			return ply.getWorld().getSpawnLocation();
	}
	public void SetPlayerHomePosition(Player ply, Location pos) {
		String name = ply.getName().toLowerCase();
		playerhomepos.put(name, pos);
		SavePlayerHomePositions();
	}

	public void LoadPlayerHomePositions() {
		playerhomepos.clear();
		try {
			BufferedReader stream = new BufferedReader(new FileReader("player-homepositions.txt"));
			String line; int lpos;
			while((line = stream.readLine()) != null) {
				lpos = line.lastIndexOf('=');
				playerhomepos.put(line.substring(0,lpos), plugin.utils.UnserializeLocation(line.substring(lpos+1)));
			}
			stream.close();
		}
		catch (Exception e) { }
	}
	public void SavePlayerHomePositions() {
		try {
			BufferedWriter stream = new BufferedWriter(new FileWriter("player-homepositions.txt"));
			Enumeration<String> e = playerhomepos.keys();
			while(e.hasMoreElements()) {
				String key = e.nextElement();
				stream.write(key + "=" + Utils.SerializeLocation(playerhomepos.get(key)));
				stream.newLine();
			}
			stream.close();
		}
		catch(Exception e) { }
	}

	//Messaging stuff
	public void SendServerMessage(String msg) {
		SendServerMessage(msg,'5');
	}
	public void SendServerMessage(String msg, char colorCode) {
		plugin.getServer().broadcastMessage("§"+colorCode+"[YB]§f " + msg);
	}
	
	public void SendServerMessage(String msg, int minLevel) {
		SendServerMessage(msg, minLevel, '5');
	}
	public void SendServerMessage(String msg, int minLevel, char colorCode) {
		msg = "§"+colorCode+"[YB]§f " + msg;
		
		Player[] players = plugin.getServer().getOnlinePlayers();

		for (Player player : players) {
			if (GetPlayerLevel(player) < minLevel)
				continue;

			player.sendMessage(msg);
		}
	}
	
	public void SendDirectedMessage(Player ply, String msg, char colorCode) {
		ply.sendMessage("§"+colorCode+"[YB]§f " + msg);
	}
	public void SendDirectedMessage(Player ply, String msg) {
		SendDirectedMessage(ply, msg, '5');
	}

	//Ranks
	private Hashtable<String,String> playerranks = new Hashtable<String,String>();
	public String GetPlayerRank(Player ply) {
		return GetPlayerRank(ply.getName());
	}
	public String GetPlayerRank(String name) {
		name = plugin.permissions.getHandler().getGroup("world", name);
		if(name == null) name = "guest";
		return name;
	}
	public void SetPlayerRank(String name, String rankname) {
		try {
			BufferedReader fileread = new BufferedReader(new FileReader("plugins/Permissions/world.yml"));
			String filebuff = ""; byte state = 0;
			String line; String newline = System.getProperty("line.separator"); String newtab = "    ";
			while((line = fileread.readLine()) != null) {
				switch(state) {
				case 0:
					if(line.equals("users:")) {
						state++;
					}
					break;
				case 1:
					if(line.equalsIgnoreCase(newtab + name + ":")) {
						state++;
					}
					break;
				case 2:
					if(line.startsWith(newtab + newtab + "group:")) {
						line = newtab + newtab + "group: " + rankname;
						state++;
					}
					break;
				}
				filebuff += line + newline;
			}
			if(state == 0) {
				filebuff += "users:" + newline;
			}
			if(state < 2) {
				filebuff += newtab + name + ":" + newline + newtab + newtab + "group: " + rankname + newline;
			}
			fileread.close();
			BufferedWriter filewrite = new BufferedWriter(new FileWriter("plugins/Permissions/world.yml"));
			filewrite.write(filebuff);
			filewrite.close();
			try {
				plugin.permissions.getHandler().reload();
			} catch(Exception e) { }
		}
		catch(Exception e) {

		}
	}

	public void LoadPlayerRanks() {
		//playerranks.clear();
		try {
			BufferedReader stream = new BufferedReader(new FileReader("ranks.txt"));
			String line; int lpos;
			while((line = stream.readLine()) != null) {
				lpos = line.lastIndexOf('=');
				if(lpos < 0) continue;
				SetPlayerRank(line.substring(0,lpos), line.substring(lpos+1));
			}
			stream.close();
		}
		catch (Exception e) { }
	}
	public void SavePlayerRanks() {
		/*try {
    		BufferedWriter stream = new BufferedWriter(new FileWriter("ranks.txt"));
    		Enumeration<String> e = playerranks.keys();
    		while(e.hasMoreElements()) {
    			String key = e.nextElement();
    			String value = playerranks.get(key);
    			if(value.equals("guest")) continue;
    			stream.write(key + "=" + value);
        		stream.newLine();
    		}
    		stream.close();
    	}
    	catch(Exception e) { }*/
	}

	//Permission levels
	public Hashtable<String,Integer> ranklevels = new Hashtable<String,Integer>();
	public Integer GetPlayerLevel(Player ply) {
		return GetPlayerLevel(ply.getName());
	}

	public Integer GetPlayerLevel(String name) {
		return GetRankLevel(GetPlayerRank(name));
	}
	public Integer GetRankLevel(String rankname) {
		rankname = rankname.toLowerCase();
		if(ranklevels.containsKey(rankname))
			return ranklevels.get(rankname);
		else
			return 0;
	}

	public void LoadRanks() {
		ranklevels.clear();
		ranktags.clear();
		try {
			BufferedReader stream = new BufferedReader(new FileReader("ranks-config.txt"));
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
	public void SaveRanks() {
		try {
			BufferedWriter stream = new BufferedWriter(new FileWriter("ranks-config.txt"));
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

	//Tags
	private Hashtable<String,String> ranktags = new Hashtable<String,String>();
	private Hashtable<String,String> playertags = new Hashtable<String,String>();
	public String GetPlayerTag(Player ply) {
		return GetPlayerTag(ply.getName());
	}
	public String GetPlayerTag(String name) {
		name = name.toLowerCase();
		String rank = GetPlayerRank(name).toLowerCase();
		if(playertags.containsKey(name))
			return playertags.get(name);
		else if(ranktags.containsKey(rank))
			return ranktags.get(rank);
		else
			return "§7";
	}
	public void SetPlayerTag(String name, String tag) {
		name = name.toLowerCase();
		if (tag == null)
			playertags.remove(name);
		else
			playertags.put(name, tag);
		SavePlayerTags();
	}

	public void LoadPlayerTags() {
		playertags.clear();
		try {
			BufferedReader stream = new BufferedReader(new FileReader("player-tags.txt"));
			String line; int lpos;
			while((line = stream.readLine()) != null) {
				lpos = line.lastIndexOf('=');
				playertags.put(line.substring(0,lpos), line.substring(lpos+1));
			}
			stream.close();
		}
		catch (Exception e) { }
	}
	public void SavePlayerTags() {
		try {
			BufferedWriter stream = new BufferedWriter(new FileWriter("player-tags.txt"));
			Enumeration<String> e = playertags.keys();
			while(e.hasMoreElements()) {
				String key = e.nextElement();
				stream.write(key + "=" + playertags.get(key));
				stream.newLine();
			}
			stream.close();
		}
		catch(Exception e) { }
	}

	public Set<String> playerTpPermissions = new HashSet<String>();
	public Set<String> playerSummonPermissions = new HashSet<String>();

	public boolean CanTp(Player commandSender, Player target) {
		return CanPort(playerTpPermissions, commandSender, target);
	}
	public boolean CanSummon(Player commandSender, Player target) {
		return CanPort(playerSummonPermissions, commandSender, target);
	}
	private boolean CanPort(Set<String> playerPortPermissions, Player commandSender, Player target) {
		int commandSenderLevel = GetPlayerLevel(commandSender);
		int targetLevel = GetPlayerLevel(target);

		String commandSenderName = commandSender.getName();
		String targetName = target.getName();

		if (plugin.jailEngine.isJailed(commandSender))
			return false;

		if (plugin.jailEngine.isJailed(target))
			return false;

		// Was an exception given?
		if (playerPortPermissions.contains(targetName+" "+commandSenderName))
			return true;

		// Lower-ranked people can only port if an exception is given.
		if (commandSenderLevel < targetLevel)
			return false;

		// Higher-ranked people can always port.
		if (commandSenderLevel > targetLevel)
			return true;

		// Same-ranked people can deny each other teleportation.
		if (playerPortPermissions.contains(targetName))
			return false;

		// Yay not denied!
		return true;
	}

	public void LoadPortPermissions() {
		playerTpPermissions.clear();
		try {
			BufferedReader stream = new BufferedReader(new FileReader("player-notp.txt"));
			String line;
			while((line = stream.readLine()) != null) {
				playerTpPermissions.add(line);
			}
			stream.close();
		}
		catch (Exception e) { }

		playerSummonPermissions.clear();
		try {
			BufferedReader stream = new BufferedReader(new FileReader("player-nosummon.txt"));
			String line;
			while((line = stream.readLine()) != null) {
				playerSummonPermissions.add(line);
			}
			stream.close();
		}
		catch (Exception e) { }
	}
	public void SavePortPermissions() {
		try {
			BufferedWriter stream = new BufferedWriter(new FileWriter("player-notp.txt"));
			for (String element : playerTpPermissions) {
				stream.write(element);
				stream.newLine();
			}
			stream.close();
		}
		catch(Exception e) { }

		try {
			BufferedWriter stream = new BufferedWriter(new FileWriter("player-nosummon.txt"));
			for (String element : playerSummonPermissions) {
				stream.write(element);
				stream.newLine();
			}
			stream.close();
		}
		catch(Exception e) { }
	}

	public Hashtable<String, Long> frozenTimes = new Hashtable<String, Long>();
	public Long frozenServerTime;
	public HashSet<String> vanishedPlayers = new HashSet<String>();

	public void sendPacketToPlayer(Player ply, Packet packet) {
		((CraftPlayer)ply).getHandle().a.b(packet);
	}

	public void sendPacketToPlayersAround(Location location, double radius, Packet packet) {
		sendPacketToPlayersAround(location, radius, packet, null);
	}
	public void sendPacketToPlayersAround(Location location, double radius, Packet packet, Player except) {
		sendPacketToPlayersAround(location, radius, packet, except, Integer.MAX_VALUE);
	}
	public void sendPacketToPlayersAround(Location location, double radius, Packet packet, Player except, int maxLevel) {
		radius *= radius;
		Vector locationVector = location.toVector();
		for (Player ply : plugin.getServer().getOnlinePlayers()) {
			if (ply.equals(except))
				continue;

			if (!ply.getWorld().equals(location.getWorld()))
				continue;

			if (GetPlayerLevel(ply) >= maxLevel)
				continue;

			if (locationVector.distanceSquared(ply.getLocation().toVector()) > radius)
				continue;

			sendPacketToPlayer(ply, packet);
		}
	}
}
