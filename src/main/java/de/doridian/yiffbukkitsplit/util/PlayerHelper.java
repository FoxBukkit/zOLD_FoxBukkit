package de.doridian.yiffbukkitsplit.util;

import de.doridian.yiffbukkit.main.StateContainer;
import de.doridian.yiffbukkit.main.ToolBind;
import de.doridian.yiffbukkit.main.util.MultiplePlayersFoundException;
import de.doridian.yiffbukkit.main.util.PlayerNotFoundException;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissionHandler;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.warp.WarpException;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkit.main.config.ConfigFileReader;
import de.doridian.yiffbukkit.main.config.ConfigFileWriter;
import de.doridian.yiffbukkit.main.offlinebukkit.OfflinePlayer;
import de.doridian.yiffbukkit.remote.YiffBukkitRemote;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet32EntityLook;
import net.minecraft.server.Packet70Bed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

		return new OfflinePlayer(plugin.getServer(), name);
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

		List<Player> otherplys = plugin.getServer().matchPlayer(subString);
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
			BufferedReader stream = new BufferedReader(new ConfigFileReader("player-homepositions.txt"));
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
			BufferedWriter stream = new BufferedWriter(new ConfigFileWriter("player-homepositions.txt"));
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
		msg = "\u00a7"+colorCode+"[YB]\u00a7f " + msg;
		plugin.getServer().broadcastMessage(msg);

		if(YiffBukkitRemote.currentCommandSender != null) YiffBukkitRemote.currentCommandSender.sendMessage(msg);
	}

	public void sendServerMessage(String msg, int minLevel) {
		sendServerMessage(msg, minLevel, '5');
	}
	public void sendServerMessage(String msg, int minLevel, char colorCode) {
		msg = "\u00a7"+colorCode+"[YB]\u00a7f " + msg;

		Player[] players = plugin.getServer().getOnlinePlayers();

		for (Player player : players) {
			if (getPlayerLevel(player) < minLevel)
				continue;

			player.sendMessage(msg);
		}

		if(YiffBukkitRemote.currentCommandSender != null) YiffBukkitRemote.currentCommandSender.sendMessage(msg);
	}

	
	/** Broadcasts a message to all players with the given permission, prefixed with [YB] in purple.
	 * @param msg
	 * @param permission
	 */
	public void sendServerMessage(String msg, String permission) {
		sendServerMessage(msg, permission, '5');
	}
	/** Broadcasts a message to all players with the given permission, prefixed with [YB] in the given color.
	 * @param msg
	 * @param permission
	 */
	public void sendServerMessage(String msg, String permission, char colorCode) {
		broadcastMessage("\u00a7"+colorCode+"[YB]\u00a7f " + msg, permission);
	}

	/** Broadcasts a message to all players with the given permission.
	 * @param msg
	 * @param permission
	 */
	public void broadcastMessage(String msg, String permission) {
		Player[] players = plugin.getServer().getOnlinePlayers();

		for (Player player : players) {
			if (!player.hasPermission(permission))
				continue;

			player.sendMessage(msg);
		}

		if(YiffBukkitRemote.currentCommandSender != null) YiffBukkitRemote.currentCommandSender.sendMessage(msg);
	}

	public void sendServerMessage(String msg, CommandSender... exceptPlayers) {
		sendServerMessage(msg, '5', exceptPlayers);
	}
	public void sendServerMessage(String msg, char colorCode, CommandSender... exceptPlayers) {
		msg = "\u00a7"+colorCode+"[YB]\u00a7f " + msg;

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
		commandSender.sendMessage("\u00a7"+colorCode+"[YB]\u00a7f " + msg);
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
		name = YiffBukkitPermissionHandler.instance.getGroup(name);
		if(name == null) name = "guest";
		return name;
	}
	public void setPlayerRank(String name, String rankname) {
		if(getPlayerRank(name).equalsIgnoreCase(rankname)) return;
		YiffBukkitPermissionHandler.instance.setGroup(name, rankname);

		Player ply = plugin.getServer().getPlayerExact(name);
		if (ply == null) return;

		setPlayerListName(ply);
	}
	
	public void setPlayerListName(Player ply) {
		try {
			String listName = formatPlayer(ply);
			if(listName.length() > 16) listName = listName.substring(0, 15);
			ply.setPlayerListName(listName);
		} catch(Exception e) { }
	}

	//Permission levels
	public Hashtable<String,Integer> ranklevels = new Hashtable<String,Integer>();
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

	@Loader({ "ranks", "ranknames" })
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
	@Saver({ "ranks", "ranknames", "rank_names" })
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
			return "\u00a77";
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
			BufferedReader stream = new BufferedReader(new ConfigFileReader("player-tags.txt"));
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
			BufferedWriter stream = new BufferedWriter(new ConfigFileWriter("player-tags.txt"));
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
			BufferedReader stream = new BufferedReader(new ConfigFileReader("player-nicks.txt"));
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
			BufferedWriter stream = new BufferedWriter(new ConfigFileWriter("player-nicks.txt"));
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
			BufferedReader stream = new BufferedReader(new ConfigFileReader("player-notp.txt"));
			String line;
			while((line = stream.readLine()) != null) {
				playerTpPermissions.add(line);
			}
			stream.close();
		}
		catch (Exception e) { }

		playerSummonPermissions.clear();
		try {
			BufferedReader stream = new BufferedReader(new ConfigFileReader("player-nosummon.txt"));
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
			BufferedWriter stream = new BufferedWriter(new ConfigFileWriter("player-notp.txt"));
			for (String element : playerTpPermissions) {
				stream.write(element);
				stream.newLine();
			}
			stream.close();
		}
		catch(Exception e) { }

		try {
			BufferedWriter stream = new BufferedWriter(new ConfigFileWriter("player-nosummon.txt"));
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

	public static final void sendPacketToPlayer(final Player ply, final Packet packet) {
		((CraftPlayer)ply).getHandle().netServerHandler.sendPacket((net.minecraft.server.Packet) packet);
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
		for (Player ply : world.getPlayers()) {
			if (ply.equals(except))
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
				sendDirectedMessage(player, "Restored bind \u00a7e"+toolName+"\u00a7f => \u00a79"+toolBind.name);
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
		//@TODO fixme
		sendPacketToPlayer(ply, new Packet70Bed(reason, 0));
	}

	public void pushWeather() {
		for (Player ply : plugin.getServer().getOnlinePlayers()) {
			pushWeather(ply);
		}
	}

	public Set<Map<Player,?>> registeredMaps = new HashSet<Map<Player,?>>();
	public Set<Set<Player>> registeredSets = new HashSet<Set<Player>>();
	public void registerMap(Map<Player,?> map) {
		registeredMaps.add(map);
	}
	public void registerSet(Set<Player> set) {
		registeredSets.add(set);
	}

	public Map<String, List<String>> autoexecs = new HashMap<String, List<String>>();

	private static final Pattern sectionPattern = Pattern.compile("^\\[(.*)\\]$");
	@Loader({ "autoexecs", "autoexec" })
	public void loadAutoexecs() {
		autoexecs.clear();
		try {
			BufferedReader stream = new BufferedReader(new ConfigFileReader("autoexecs.txt"));

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
			BufferedWriter stream = new BufferedWriter(new ConfigFileWriter("autoexecs.txt"));
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

	public String getPlayerNameByIP(String ip) {
		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
			final String address = onlinePlayer.getAddress().getAddress().getHostAddress();
			if (!address.equals(ip))
				continue;

			return onlinePlayer.getName();
		}

		/*String offlinePlayerName = plugin.playerListener.offlinePlayers.get(ip);
		if (offlinePlayerName != null)
			return "\u00a77"+offlinePlayerName+"\u00a7f";*/

		return ip;
	}

	public String formatPlayer(Player player) {
		int playerLevel = getPlayerLevel(player);
		final String playerName = player.getName();

		if (playerLevel < 0)
			return "\u00a70"+playerName;

		switch (playerLevel) {
		case 0:
			return "\u00a77"+playerName;

		case 1:
			return "\u00a7a"+playerName;

		case 2:
			return "\u00a72"+playerName;
			
		case 3:
			return "\u00a79"+playerName;

		case 4:
			return "\u00a7b"+playerName;

		default:
			return "\u00a75"+playerName;
		}
	}

	private final Set<Entity> raging = Collections.synchronizedSet(new HashSet<Entity>());

	public boolean rage(final LivingEntity entity, final int ticks) {
		synchronized (raging) {
			if (raging.contains(entity))
				return false;

			raging.add(entity);
		}

		final EntityLiving notchEntity = ((CraftLivingEntity) entity).getHandle();
		final ArrayList<Integer> taskIdContainer = new ArrayList<Integer>(1);
		final Random random = new Random();
		taskIdContainer.add(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			int i = 0;
			public void run() {
				Location location = entity.getLocation();

				// damage animation
				sendPacketToPlayersAround(location, 32, new Packet18ArmAnimation(notchEntity, 2));

				byte yaw = (byte)(random.nextInt(255)-128);
				byte pitch = (byte)(random.nextInt(255)-128);
				if (entity instanceof Player) {
					// arm animation
					sendPacketToPlayersAround(location, 32, new Packet18ArmAnimation(notchEntity, 1));
					// random looking
					sendPacketToPlayersAround(location, 32, new Packet32EntityLook(entity.getEntityId(), yaw, pitch), (Player) entity);
				}
				else {
					sendPacketToPlayersAround(location, 32, new Packet32EntityLook(entity.getEntityId(), yaw, pitch), null);
				}

				if (++i > ticks && !taskIdContainer.isEmpty()) {
					plugin.getServer().getScheduler().cancelTask(taskIdContainer.get(0));
					synchronized (raging) {
						raging.remove(entity);
					}
				}
			}
		}, 0, 1));

		return true;
	}

	public HashSet<String> yiffcraftPlayers = new HashSet<String>();
	public void setYiffcraftState(Player ply, boolean hasYC) {
		String plyName = ply.getName().toLowerCase();
		if(hasYC) {
			if(!yiffcraftPlayers.contains(plyName)) {
				yiffcraftPlayers.add(plyName);
			}
		} else {
			if(yiffcraftPlayers.contains(plyName)) {
				yiffcraftPlayers.remove(plyName);
			}
		}
	}

	public void sendYiffcraftClientCommand(Player ply, char command, String... args) {
		StringBuilder sb = new StringBuilder();
		boolean notfirst = false;
		for (String arg : args) {
			if (notfirst)
				sb.append('|');

			notfirst = true;

			sb.append(arg);
		}

		sendYiffcraftClientCommand(ply, command, sb);
	}

	public void sendYiffcraftClientCommand(Player ply, char command, CharSequence args) {
		if(!yiffcraftPlayers.contains(ply.getName().toLowerCase())) return;
		ply.sendPluginMessage(plugin, "yiffcraft", (command + args.toString()).getBytes());
	}

	public HashMap<String, LinkedList<Location>> teleportHistory = new HashMap<String, LinkedList<Location>>();
	public void pushPlayerLocationOntoTeleportStack(Player ply) {
		String name = ply.getName().toLowerCase();

		LinkedList<Location> locs = teleportHistory.get(name);
		if(locs == null) {
			locs = new LinkedList<Location>();
			teleportHistory.put(name, locs);
		}

		locs.push(ply.getLocation());
		if(locs.size() > 10) locs.removeFirst();
	}

	public void teleportWithHistory(Player ply, Location to) {
		pushPlayerLocationOntoTeleportStack(ply);
		ply.teleport(to);
	}

	public void teleportWithHistory(Player ply, Entity to) {
		pushPlayerLocationOntoTeleportStack(ply);
		ply.teleport(to);
	}
}
