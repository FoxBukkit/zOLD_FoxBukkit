package de.doridian.yiffbukkit.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet70Bed;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.StateContainer;
import de.doridian.yiffbukkit.ToolBind;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.offlinebukkit.OfflinePlayer;
import de.doridian.yiffbukkit.remote.YiffBukkitRemote;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.warp.WarpException;

public class PlayerHelper extends StateContainer {
	private YiffBukkit plugin;
	public Map<String, String> conversations = new HashMap<String, String>();

	public PlayerHelper(YiffBukkit plug) {
		plugin = plug;
	}

	private Player literalMatch(String name) {
		Player onlinePlayer = plugin.getServer().getPlayer(name);
		if (onlinePlayer != null)
			return onlinePlayer;

		return new OfflinePlayer(plugin.getServer(), plugin.getOrCreateWorld("world", Environment.NORMAL), name);
	}

	private static final Pattern quotePattern = Pattern.compile("^\"(.*)\"$");
	public Player matchPlayerSingle(String subString, boolean implicitlyLiteral) throws PlayerNotFoundException, MultiplePlayersFoundException {
		Matcher matcher = quotePattern.matcher(subString);

		if (matcher.matches())
			return literalMatch(matcher.group(1));

		List<Player> players = plugin.getServer().matchPlayer(subString);

		int c = players.size();
		if (c < 1)
			if (implicitlyLiteral)
				return literalMatch(subString);
			else
				throw new PlayerNotFoundException();

		if (c > 1)
			throw new MultiplePlayersFoundException(players);

		return players.get(0);
	}

	public Player matchPlayerSingle(String subString) throws PlayerNotFoundException, MultiplePlayersFoundException {
		return matchPlayerSingle(subString, false);
	}

	public String completePlayerName(String subString, boolean implicitlyLiteralNames) {
		Matcher matcher = quotePattern.matcher(subString);

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
		return getPlayerTag(ply) + ply.getDisplayName();
	}

	//Home position stuff
	private Hashtable<String,Location> playerhomepos = new Hashtable<String,Location>();
	public Location getPlayerHomePosition(Player ply) {
		String name = ply.getName().toLowerCase();
		if(playerhomepos.containsKey(name))
			return playerhomepos.get(name);
		else
			return getPlayerSpawnPosition(ply);
	}
	public void setPlayerHomePosition(Player ply, Location pos) {
		String name = ply.getName().toLowerCase();
		playerhomepos.put(name, pos);
		savePlayerHomePositions();
	}

	@Loader({ "homepositions", "home_positions", "homes", "home" })
	public void loadPlayerHomePositions() {
		playerhomepos.clear();
		try {
			BufferedReader stream = new BufferedReader(new FileReader("player-homepositions.txt"));
			String line; int lpos;
			while((line = stream.readLine()) != null) {
				lpos = line.lastIndexOf('=');
				playerhomepos.put(line.substring(0,lpos), plugin.utils.unserializeLocation(line.substring(lpos+1)));
			}
			stream.close();
		}
		catch (Exception e) { }
	}
	@Saver({ "homepositions", "home_positions", "homes", "home" })
	public void savePlayerHomePositions() {
		try {
			BufferedWriter stream = new BufferedWriter(new FileWriter("player-homepositions.txt"));
			Enumeration<String> e = playerhomepos.keys();
			while(e.hasMoreElements()) {
				String key = e.nextElement();
				stream.write(key + "=" + Utils.serializeLocation(playerhomepos.get(key)));
				stream.newLine();
			}
			stream.close();
		}
		catch(Exception e) { }
	}

	//Messaging stuff
	public void sendServerMessage(String msg) {
		sendServerMessage(msg,'5');
	}
	public void sendServerMessage(String msg, char colorCode) {
		msg = "§"+colorCode+"[YB]§f " + msg;
		plugin.getServer().broadcastMessage(msg);

		if(YiffBukkitRemote.currentCommandSender != null) YiffBukkitRemote.currentCommandSender.sendMessage(msg);
	}

	public void sendServerMessage(String msg, int minLevel) {
		sendServerMessage(msg, minLevel, '5');
	}
	public void sendServerMessage(String msg, int minLevel, char colorCode) {
		msg = "§"+colorCode+"[YB]§f " + msg;

		Player[] players = plugin.getServer().getOnlinePlayers();

		for (Player player : players) {
			if (getPlayerLevel(player) < minLevel)
				continue;

			player.sendMessage(msg);
		}

		if(YiffBukkitRemote.currentCommandSender != null) YiffBukkitRemote.currentCommandSender.sendMessage(msg);
	}

	public void sendServerMessage(String msg, CommandSender... exceptPlayers) {
		sendServerMessage(msg, '5', exceptPlayers);
	}
	public void sendServerMessage(String msg, char colorCode, CommandSender... exceptPlayers) {
		msg = "§"+colorCode+"[YB]§f " + msg;

		Set<Player> exceptPlayersSet = new HashSet<Player>();
		for (CommandSender exceptPlayer : exceptPlayers) {
			if (!(exceptPlayer instanceof Player))
				continue;

			exceptPlayersSet.add((Player)exceptPlayer);
		}

		Player[] players = plugin.getServer().getOnlinePlayers();

		for (Player player : players) {
			if (exceptPlayersSet.contains(player))
				continue;

			player.sendMessage(msg);
		}

		if(YiffBukkitRemote.currentCommandSender != null) YiffBukkitRemote.currentCommandSender.sendMessage(msg);
	}

	public void sendDirectedMessage(CommandSender commandSender, String msg, char colorCode) {
		commandSender.sendMessage("§"+colorCode+"[YB]§f " + msg);
	}
	public void sendDirectedMessage(CommandSender commandSender, String msg) {
		sendDirectedMessage(commandSender, msg, '5');
	}

	//Ranks
	private Hashtable<String,String> playerranks = new Hashtable<String,String>();
	public String getPlayerRank(Player ply) {
		return getPlayerRank(ply.getName());
	}
	public String getPlayerRank(String name) {
		name = plugin.permissionHandler.getGroup(name);
		if(name == null) name = "guest";
		return name;
	}
	public void setPlayerRank(String name, String rankname) {
		if(getPlayerRank(name).equalsIgnoreCase(rankname)) return;
		plugin.permissionHandler.setGroup(name, rankname);
	}

	//Permission levels
	public Hashtable<String,Integer> ranklevels = new Hashtable<String,Integer>();
	public Integer getPlayerLevel(CommandSender ply) {
		return getPlayerLevel(ply.getName());
	}

	public Integer getPlayerLevel(String name) {
		if(name.equals("[CONSOLE]"))
			return 9999;

		return getRankLevel(getPlayerRank(name));
	}
	public Integer getRankLevel(String rankname) {
		rankname = rankname.toLowerCase();
		if(ranklevels.containsKey(rankname))
			return ranklevels.get(rankname);
		else
			return 0;
	}

	@Loader({ "ranks", "ranknames" })
	public void loadRanks() {
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
	@Saver({ "ranks", "ranknames", "rank_names" })
	public void saveRanks() {
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
	public String getPlayerTag(CommandSender commandSender) {
		return getPlayerTag(commandSender.getName());
	}
	public String getPlayerTag(String name) {
		name = name.toLowerCase();
		String rank = getPlayerRank(name).toLowerCase();
		if(playertags.containsKey(name))
			return playertags.get(name);
		else if(ranktags.containsKey(rank))
			return ranktags.get(rank);
		else
			return "§7";
	}
	public void setPlayerTag(String name, String tag) {
		name = name.toLowerCase();
		if (tag == null)
			playertags.remove(name);
		else
			playertags.put(name, tag);
		savePlayerTags();
	}

	@Loader({ "playertags", "player_tags", "tags" })
	public void loadPlayerTags() {
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
	@Saver({ "playertags", "player_tags", "tags" })
	public void savePlayerTags() {
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

	private Hashtable<String,String> playernicks = new Hashtable<String,String>();
	@Loader({ "nicks", "nick", "nicknames", "nickname", "nick_names", "nick_name" })
	public void loadPlayerNicks() {
		playernicks.clear();
		try {
			BufferedReader stream = new BufferedReader(new FileReader("player-nicks.txt"));
			String line; int lpos;
			while((line = stream.readLine()) != null) {
				lpos = line.lastIndexOf('=');
				playernicks.put(line.substring(0,lpos), line.substring(lpos+1));
			}
			stream.close();
		}
		catch (Exception e) { }
	}
	@Saver({ "nicks", "nick", "nicknames", "nickname", "nick_names", "nick_name" })
	public void savePlayerNicks() {
		try {
			BufferedWriter stream = new BufferedWriter(new FileWriter("player-nicks.txt"));
			Enumeration<String> e = playernicks.keys();
			while(e.hasMoreElements()) {
				String key = e.nextElement();
				stream.write(key + "=" + playernicks.get(key));
				stream.newLine();
			}
			stream.close();
		}
		catch(Exception e) { }
	}

	public String getPlayerNick(String name) {
		name = name.toLowerCase();
		if(playernicks.containsKey(name))
			return playernicks.get(name);
		else
			return null;
	}

	public void setPlayerNick(String name, String tag) {
		name = name.toLowerCase();
		if (tag == null)
		{
			playernicks.remove(name);
		}
		else
			playernicks.put(name, tag);
		savePlayerNicks();
	}

	public Set<String> playerTpPermissions = new HashSet<String>();
	public Set<String> playerSummonPermissions = new HashSet<String>();

	public boolean canTp(CommandSender commandSender, Player target) {
		// Prevent teleporting out of jail.
		if ((commandSender instanceof Player) && plugin.jailEngine.isJailed((Player)commandSender))
			return false;

		return canPort(playerTpPermissions, commandSender, target);
	}
	public boolean canSummon(CommandSender commandSender, Player target) {
		// Prevent summoning someone out of jail.
		if (plugin.jailEngine.isJailed(target))
			return false;

		return canPort(playerSummonPermissions, commandSender, target);
	}
	private boolean canPort(Set<String> playerPortPermissions, CommandSender commandSender, Player target) {
		int commandSenderLevel = getPlayerLevel(commandSender);
		int targetLevel = getPlayerLevel(target);

		String commandSenderName = commandSender.getName();
		String targetName = target.getName();

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

	@Loader({ "portpermissions", "port_permissions", "noport" })
	public void loadPortPermissions() {
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
	@Saver({ "portpermissions", "port_permissions", "noport" })
	public void savePortPermissions() {
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

	public final void sendPacketToPlayer(final Player ply, final Packet packet) {
		((CraftPlayer)ply).getHandle().netServerHandler.sendPacket(packet);
	}

	public final void sendPacketToPlayersAround(final Location location, final double radius, final Packet packet) {
		sendPacketToPlayersAround(location, radius, packet, null);
	}
	public final void sendPacketToPlayersAround(final Location location, final double radius, final Packet packet, final Player except) {
		sendPacketToPlayersAround(location, radius, packet, except, Integer.MAX_VALUE);
	}
	public final void sendPacketToPlayersAround(final Location location, double radius, final Packet packet, final Player except, final int maxLevel) {
		radius *= radius;
		final Vector locationVector = location.toVector();
		final World world = location.getWorld();
		for (Player ply : plugin.getServer().getOnlinePlayers()) {
			if (ply.equals(except))
				continue;

			if (world != ply.getWorld())
				continue;

			if (getPlayerLevel(ply) >= maxLevel)
				continue;

			if (locationVector.distanceSquared(ply.getLocation().toVector()) > radius)
				continue;

			sendPacketToPlayer(ply, packet);
		}
	}

	public boolean isPlayerDisabled(Player ply) {
		return ply.getHealth() <= 0 || plugin.jailEngine.isJailed(ply);
	}

	public Map<String, ToolBind> toolMappings = new HashMap<String, ToolBind>();

	public void addToolMapping(Player ply, Material toolType, ToolBind runnable) {
		String key = ply.getName()+" "+toolType.name();

		if (runnable == null)
			toolMappings.remove(key);
		else
			toolMappings.put(key, runnable);
	}

	public void updateToolMappings(Player player) {
		String playerName = player.getName();
		for (Entry<String, ToolBind> entry : toolMappings.entrySet()) {
			ToolBind toolBind = entry.getValue();
			if (playerName.equals(toolBind.playerName)) {
				String toolName = entry.getKey();
				toolName = toolName.substring(toolName.indexOf(' ')+1);
				sendDirectedMessage(player, "Restored bind §e"+toolName+"§f => §9"+toolBind.name);
			}
		}
	}

	Map<String, String> leashMasters = new HashMap<String, String>();
	private int leashTaskId;

	public boolean toggleLeash(Player master, Player slave) {
		if (!leashMasters.containsKey(slave.getName())) {
			addLeash(master, slave);
			return true;
		}
		else if (leashMasters.get(slave.getName()).equals(master.getName())) {
			removeLeash(slave);
			return false;
		}
		else {
			addLeash(master, slave);
			return true;
		}
	}

	public void addLeash(Player master, Player slave) {
		if (leashMasters.isEmpty()) {
			final Server server = plugin.getServer();

			Runnable task = new Runnable() {
				public void run() {
					//for (Entry<String, String> entry : leashMasters.entrySet()) {
					for ( Iterator<Entry<String, String>> leashMastersIter = leashMasters.entrySet().iterator(); leashMastersIter.hasNext(); ) {
						Entry<String, String> entry = leashMastersIter.next();

						final String slaveName = entry.getKey();
						final Player slave = server.getPlayer(slaveName);

						if (slave == null)
							continue;

						final String masterName = entry.getValue();
						final Player master = server.getPlayer(masterName);

						if (master == null || !master.isOnline()) {
							leashMastersIter.remove();
							sendServerMessage(masterName+" left, unleashing "+slaveName+".");
							removeHandler(slave);
							continue;
						}

						final Vector slavePos = slave.getLocation().toVector();
						final Vector masterPos = master.getLocation().toVector();

						final Vector masterVelocity = master.getVelocity();

						final Vector directionXZ = masterPos.clone().subtract(slavePos);
						double directionY = directionXZ.getY();
						directionXZ.setY(0D);

						final double distanceXZ = directionXZ.length();

						final double targetDistanceXZ = 2;
						final double maxSpeed = Math.max(0, 0.1+masterVelocity.clone().setY(0).length()+0.5*Math.max(0,distanceXZ-targetDistanceXZ));
						final double maxYSpeed = 0.5;

						final Vector velocity = new Vector();
						if (distanceXZ > targetDistanceXZ)
							velocity.add(directionXZ.clone().normalize().multiply(maxSpeed));

						if (directionY < -2 || directionY > 2)
							velocity.setY(Math.signum(directionY)*maxSpeed);
						else if (distanceXZ > targetDistanceXZ && directionY > 0)
							velocity.setY(maxYSpeed);
						else
							velocity.setY(slave.getVelocity().getY()*0.8 + masterVelocity.getY()*0.2);

						final EntityPlayer eply = ((CraftPlayer)slave).getHandle();
						if (!eply.onGround)
							velocity.multiply(0.5);

						slave.setVelocity(velocity);
					}
				}
			};
			leashTaskId = server.getScheduler().scheduleSyncRepeatingTask(plugin, task, 0, 10);
		}

		leashMasters.put(slave.getName(), master.getName());
		/*Control permissionsHandler = (Control)plugin.permissions.getHandler();

		permissionsHandler.setCacheItem(slave.getWorld().getName(), slave.getName(), "nocheat.speedhack", true);
		permissionsHandler.setCacheItem(slave.getWorld().getName(), slave.getName(), "nocheat.moving", true);*/
	}

	private void removeHandler(Player slave) {
		if (leashMasters.isEmpty()) {
			plugin.getServer().getScheduler().cancelTask(leashTaskId);
		}

		/*Control permissionsHandler = (Control)plugin.permissions.getHandler();

		permissionsHandler.removeCachedItem(slave.getWorld().getName(), slave.getName(), "nocheat.speedhack");
		permissionsHandler.removeCachedItem(slave.getWorld().getName(), slave.getName(), "nocheat.moving");*/
	}

	public void removeLeash(Player slave) {
		leashMasters.remove(slave.getName());

		removeHandler(slave);
	}

	public static Date lastLogout(Player player) {
		File playerFile = getPlayerFile(player.getName(), "world");
		if (playerFile == null)
			return null;

		if (!playerFile.exists())
			return null;

		return new Date(playerFile.lastModified());
	}

	public static Date lastLogoutBackup(Player player) {
		File playerFile = getPlayerFile(player.getName(), "world_backup");
		if (playerFile == null)
			return null;

		if (!playerFile.exists())
			return null;

		return new Date(playerFile.lastModified());
	}

	public static File getPlayerFile(String playerName, String world) {
		File directory = new File(world+"/players/");

		if (!directory.exists())
			return null;

		if (!directory.isDirectory())
			return null;

		for (String file : directory.list()){
			if (!file.equalsIgnoreCase(playerName+".dat"))
				continue;

			return new File(world+"/players/"+file);
		}
		return null;
	}

	public enum WeatherType {
		CLEAR("clear"), RAIN("rain"), THUNDERSTORM("thunderstorm");

		public final String name;

		private WeatherType(String name) {
			this.name = name;
		}
	}
	public Hashtable<String, WeatherType> frozenWeathers = new Hashtable<String, WeatherType>();
	public WeatherType frozenServerWeather;

	public void pushWeather(Player ply) {
		WeatherType weatherType = WeatherType.CLEAR;
		final World world = ply.getWorld();
		if (world.isThundering())
			weatherType = WeatherType.THUNDERSTORM;
		else if (world.hasStorm())
			weatherType = WeatherType.RAIN;

		WeatherType frozenWeather = frozenWeathers.get(ply.getName());

		if (frozenWeather != null) {
			weatherType = frozenWeather;
		}
		else if (frozenServerWeather != null) {
			weatherType = frozenServerWeather;
		}

		int reason = weatherType == WeatherType.CLEAR ? 2 : 1;
		sendPacketToPlayer(ply, new Packet70Bed(reason));
	}

	public void pushWeather() {
		for (Player ply : plugin.getServer().getOnlinePlayers()) {
			pushWeather(ply);
		}
	}

	public Set<Map<Player,?>> registeredMaps = new HashSet<Map<Player,?>>();
	public void registerMap(Map<Player,?> map) {
		registeredMaps.add(map);
	}

	public Map<String, List<String>> autoexecs = new HashMap<String, List<String>>();

	private static final Pattern sectionPattern = Pattern.compile("^\\[(.*)\\]$");
	@Loader({ "autoexecs", "autoexec" })
	public void loadAutoexecs() {
		autoexecs.clear();
		try {
			BufferedReader stream = new BufferedReader(new FileReader("autoexecs.txt"));

			String line;
			String currentPlayerName = null;
			List<String> commands = null;
			while((line = stream.readLine()) != null) {
				if (line.isEmpty())
					continue;

				final Matcher matcher = sectionPattern.matcher(line);
				if (matcher.matches()) {
					if (commands != null && !commands.isEmpty()) {
						autoexecs.put(currentPlayerName, commands);
					}

					currentPlayerName = matcher.group(1);
					commands = new ArrayList<String>();
					continue;
				}

				if (commands == null) {
					System.err.println("Line before any section in autoexecs.txt");
					continue;
				}

				commands.add(line);
			}

			if (commands != null && !commands.isEmpty()) {
				autoexecs.put(currentPlayerName, commands);
			}

			stream.close();
		}
		catch (IOException e) { }
	}
	@Saver({ "autoexecs", "autoexec" })
	public void saveAutoexecs() {
		try {
			BufferedWriter stream = new BufferedWriter(new FileWriter("autoexecs.txt"));
			for (Entry<String, List<String>> entry : autoexecs.entrySet()) {
				stream.write('['+entry.getKey()+']');
				stream.newLine();

				for (String command : entry.getValue()) {
					if (command.charAt(0) == '[')
						continue;

					stream.write(command);
					stream.newLine();
				}
				stream.newLine();
			}
			stream.close();
		}
		catch(IOException e) { }
	}

	public Location getPlayerSpawnPosition(Player ply) {
		try {
			WarpDescriptor warpDescriptor = plugin.warpEngine.getWarp(null, getPlayerRank(ply)+"_spawn");
			if (warpDescriptor == null)
				throw new WarpException("");

			return warpDescriptor.location;
		} catch (WarpException e) {
			final Location location = ply.getWorld().getSpawnLocation();
			location.setX(location.getX()+0.5);
			location.setZ(location.getZ()+0.5);
			return location;
		}
	}

	public String getPlayerNameByIP(String name) {
		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
			final String address = onlinePlayer.getAddress().getAddress().getHostAddress();
			if (!address.equals(name))
				continue;

			name = onlinePlayer.getName();
			break;
		}
		return name;
	}
}
