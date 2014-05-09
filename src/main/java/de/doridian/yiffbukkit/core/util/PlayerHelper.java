package de.doridian.yiffbukkit.core.util;

import de.doridian.yiffbukkit.bans.FishBansResolver;
import de.doridian.yiffbukkit.permissions.AbusePotentialManager;
import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.jail.JailComponent;
import de.doridian.yiffbukkit.main.StateContainer;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.config.ConfigFileReader;
import de.doridian.yiffbukkit.main.config.ConfigFileWriter;
import de.doridian.yiffbukkit.main.offlinebukkit.OfflinePlayer;
import de.doridian.yiffbukkit.main.util.MultiplePlayersFoundException;
import de.doridian.yiffbukkit.main.util.PlayerNotFoundException;
import de.doridian.yiffbukkit.main.util.RedisManager;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissionHandler;
import de.doridian.yiffbukkit.warp.WarpDescriptor;
import de.doridian.yiffbukkit.warp.WarpException;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TObjectIntProcedure;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_7_R3.command.CraftConsoleCommandSender;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerHelper extends StateContainer {
	private YiffBukkit plugin;
	public Map<UUID, UUID> conversations = new HashMap<>();

	public PlayerHelper(YiffBukkit plug) {
		plugin = plug;
	}

	public Player getPlayerByUUID(UUID uuid) {
		Player ply = Bukkit.getPlayer(uuid);
		if(ply == null)
			ply = new OfflinePlayer(plugin.getServer(), uuid);
		return ply;
	}

	public Player literalMatch(String name) {
		Player onlinePlayer = plugin.getServer().getPlayer(name);
		if (onlinePlayer != null)
			return onlinePlayer;

		return new OfflinePlayer(plugin.getServer(), name);
	}

	private static final Pattern quotePattern = Pattern.compile("^\"(.*)\"$");
	public Player matchPlayerSingle(String subString, boolean implicitlyLiteral) throws PlayerNotFoundException, MultiplePlayersFoundException {
		if (implicitlyLiteral)
			return literalMatch(subString);

		final Matcher matcher = quotePattern.matcher(subString);

		if (matcher.matches())
			return literalMatch(matcher.group(1));

		final List<Player> players = Utils.matchPlayer(subString);

		final int c = players.size();
		if (c < 1)
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

		List<Player> otherplys = Utils.matchPlayer(subString);
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
	private TObjectIntHashMap<UUID> playerHomePosLimits = new TObjectIntHashMap<>();
	private HashMap<UUID,HashMap<String,Location>> playerhomepos = new HashMap<>();
	public Location getPlayerHomePosition(Player ply, String posName) throws YiffBukkitCommandException {
		UUID uuid = ply.getUniqueId();
		posName = posName.toLowerCase();
		if(playerhomepos.containsKey(uuid)) {
			HashMap<String, Location> playersPositions = playerhomepos.get(uuid);
			if(playersPositions.containsKey(posName)) {
				return playersPositions.get(posName);
			}
			else if (posName.equals("default")) {
				return getPlayerSpawnPosition(ply);
			}
			else {
				throw new YiffBukkitCommandException("Home position with that name was not found");
			}
		} else {
			return getPlayerSpawnPosition(ply);
		}
	}
	public void setPlayerHomePosition(Player ply, String posName, Location pos) throws YiffBukkitCommandException {
		UUID uuid = ply.getUniqueId();
		posName = posName.toLowerCase();
		HashMap<String, Location> playerHomePositions = getPlayersHomePositions(uuid);
		if(pos == null) {
			if(playerHomePositions.containsKey(posName)) {
				playerHomePositions.remove(posName);
			} else {
				throw new YiffBukkitCommandException("Home position with that name was not found");
			}
		} else if(playerHomePositions.containsKey(posName) || posName.equals("default") || playerHomePositions.size() <= getPlayerHomePositionLimit(uuid)) {
			playerHomePositions.put(posName, pos);
		} else {
			throw new YiffBukkitCommandException("You cannot set more home positions");
		}
		savePlayerHomePositions();
	}

	public Set<String> getPlayerHomePositionNames(Player ply) {
		return getPlayersHomePositions(ply.getUniqueId()).keySet();
	}

	public int getPlayerHomePositionLimit(UUID uuid) {
		if(playerHomePosLimits.containsKey(uuid)) {
			return playerHomePosLimits.get(uuid);
		} else {
			return 0;
		}
	}

	public void setPlayerHomePositionLimit(UUID uuid, int value) {
		playerHomePosLimits.put(uuid, value);
		savePlayerHomePositions();
	}

	private HashMap<String, Location> getPlayersHomePositions(UUID uuid) {
		HashMap<String, Location> playersPositions;
		if(playerhomepos.containsKey(uuid)) {
			playersPositions = playerhomepos.get(uuid);
		} else {
			playersPositions = new HashMap<>();
			playerhomepos.put(uuid, playersPositions);
		}
		return playersPositions;
	}

	public void clearPlayerHomePositionsAndTeleportHistory(Player ply) {
		playerhomepos.remove(ply.getUniqueId());
		teleportHistory.remove(ply.getUniqueId());
		savePlayerHomePositions();
	}

	@Loader({ "homepositions", "home_positions", "homes", "home" })
	public void loadPlayerHomePositions() {
		playerhomepos.clear();
		try {
			BufferedReader stream = new BufferedReader(new ConfigFileReader("player-homepositions.txt"));
			String line; String[] lineSplit;
			while((line = stream.readLine()) != null) {
				lineSplit = line.split("=");
				String locName = "default";
				if(lineSplit.length == 3) {
					locName = lineSplit[2];
				}
				if(locName.equals("LIMIT")) {
					playerHomePosLimits.put(UUID.fromString(lineSplit[0]), Integer.parseInt(lineSplit[1]));
				} else {
					getPlayersHomePositions(UUID.fromString(lineSplit[0])).put(locName, plugin.utils.unserializeLocation(lineSplit[1]));
				}
			}
			stream.close();
		}
		catch (Exception ignored) { }
	}
	@Saver({ "homepositions", "home_positions", "homes", "home" })
	public void savePlayerHomePositions() {
		try {
			final BufferedWriter stream = new BufferedWriter(new ConfigFileWriter("player-homepositions.txt"));
			for(Entry<UUID, HashMap<String, Location>> entry : playerhomepos.entrySet()) {
				for(Entry<String, Location> homepos : entry.getValue().entrySet()) {
					stream.write(entry.getKey().toString() + "=" + Utils.serializeLocation(homepos.getValue()) + "=" + homepos.getKey());
					stream.newLine();
				}
			}
			playerHomePosLimits.forEachEntry(new TObjectIntProcedure<UUID>() {
				@Override
				public boolean execute(UUID name, int limit) {
					try {
						stream.write(name.toString() + "=" + limit + "=LIMIT");
						stream.newLine();
					} catch(Exception e) {
						return false;
					}
					return true;
				}
			});
			stream.close();
		}
		catch(Exception ignored) { }
	}

	//Messaging stuff
	public static void sendServerMessage(String msg) {
		sendServerMessage(msg,'5');
	}
	public static void sendServerMessage(String msg, char colorCode) {
		msg = "\u00a7"+colorCode+"[YB]\u00a7f " + msg;
		Bukkit.broadcastMessage(msg);
	}

	public static void sendServerMessage(String msg, int minLevel) {
		sendServerMessage(msg, minLevel, '5');
	}
	public static void sendServerMessage(String msg, int minLevel, char colorCode) {
		msg = "\u00a7"+colorCode+"[YB]\u00a7f " + msg;

		Player[] players = Bukkit.getOnlinePlayers();

		for (Player player : players) {
			if (getPlayerLevel(player) < minLevel)
				continue;

			player.sendMessage(msg);
		}
	}

	
	/**
	 * Broadcasts a message to all players with the given permission, prefixed with [YB] in purple.
	 *
	 * @param message The message to send
	 * @param permission The permission required to receive the message
	 */
	public static void sendServerMessage(String message, String permission) {
		sendServerMessage(message, permission, '5');
	}
	/**
	 * Broadcasts a message to all players with the given permission, prefixed with [YB] in the given color.
	 *
	 * @param message The message to send
	 * @param permission The permission required to receive the message
	 * @param colorCode The color code to prefix
	 */
	public static void sendServerMessage(String message, String permission, char colorCode) {
		broadcastMessage("\u00a7"+colorCode+"[YB]\u00a7f " + message, permission);
	}

	/**
	 * Broadcasts a message to all players with the given permission.
	 *
	 * @param message The message to send
	 * @param permission The permission required to receive the message
	 */
	public static void broadcastMessage(String message, String permission) {
		Player[] players = Bukkit.getOnlinePlayers();

		for (Player player : players) {
			if (!player.hasPermission(permission))
				continue;

			player.sendMessage(message);
		}
	}

	public static void sendServerMessage(String msg, CommandSender... exceptPlayers) {
		sendServerMessage(msg, '5', exceptPlayers);
	}
	public static void sendServerMessage(String msg, char colorCode, CommandSender... exceptPlayers) {
		msg = "\u00a7"+colorCode+"[YB]\u00a7f " + msg;

		Set<Player> exceptPlayersSet = new HashSet<>();
		for (CommandSender exceptPlayer : exceptPlayers) {
			if (!(exceptPlayer instanceof Player))
				continue;

			exceptPlayersSet.add((Player)exceptPlayer);
		}

		Player[] players = Bukkit.getOnlinePlayers();

		for (Player player : players) {
			if (exceptPlayersSet.contains(player))
				continue;

			player.sendMessage(msg);
		}
	}

	public static void sendDirectedMessage(CommandSender commandSender, String msg, char colorCode) {
		commandSender.sendMessage("\u00a7"+colorCode+"[YB]\u00a7f " + msg);
	}
	public static void sendDirectedMessage(CommandSender commandSender, String msg) {
		sendDirectedMessage(commandSender, msg, '5');
	}

	//Ranks
	public static String getPlayerRank(Player ply) {
		return getPlayerRank(ply.getUniqueId());
	}
	public static String getPlayerRank(UUID uuid) {
		final String rank = YiffBukkitPermissionHandler.instance.getGroup(uuid);
		if (rank == null)
			return "guest";

		return rank;
	}
	public void setPlayerRank(UUID uuid, String rankname) {
		if(getPlayerRank(uuid).equalsIgnoreCase(rankname)) return;
		YiffBukkitPermissionHandler.instance.setGroup(uuid, rankname);

		Player ply = plugin.getServer().getPlayer(uuid);
		if (ply == null) return;

		setPlayerListName(ply);
	}
	
	public void setPlayerListName(Player ply) {
		try {
			String listName = formatPlayer(ply);
			if(listName.length() > 16) listName = listName.substring(0, 15);
			ply.setPlayerListName(listName);
		} catch(Exception ignored) { }
	}

	//Permission levels
	public Map<String,String> ranklevels = RedisManager.createCachedRedisMap("ranklevels");
	public static int getPlayerLevel(CommandSender ply) {
		if (ply instanceof ConsoleCommandSender) {
			return 9999;
		}
		if (ply instanceof BlockCommandSender) {
			return 9998;
		}
		return getPlayerLevel(ply.getUniqueId());
	}

	public static int getPlayerLevel(UUID uuid) {
		if(CraftConsoleCommandSender.CONSOLE_UUID.equals(uuid))
			return 9999;

		return YiffBukkit.instance.playerHelper.getRankLevel(getPlayerRank(uuid));
	}

	public int getRankLevel(String rankname) {
		rankname = rankname.toLowerCase();
		if (rankname.equals("doridian"))
			return 666;

		final String rankLevelString = ranklevels.get(rankname);
		if (rankLevelString == null)
			return 0;

		return Integer.parseInt(rankLevelString);
	}

	//Tags
	private final Map<String,String> rankTags = RedisManager.createCachedRedisMap("ranktags");
	private final Map<String,String> playerTags = RedisManager.createCachedRedisMap("playerTags");
	private final Map<String,String> playerRankTags = RedisManager.createCachedRedisMap("playerRankTags");

	public String getPlayerTag(CommandSender commandSender) {
		return getPlayerTag(commandSender.getUniqueId());
	}

	public String getPlayerRankTag(UUID uuid) {
		final String rank = getPlayerRank(uuid).toLowerCase();
		if (playerRankTags.containsKey(uuid.toString()))
			return playerRankTags.get(uuid.toString());

		if (rankTags.containsKey(rank))
			return rankTags.get(rank);

		return "\u00a77";
	}

	public String getPlayerTag(UUID uuid) {
		final String rankTag = getPlayerRankTag(uuid);

		if (playerTags.containsKey(uuid.toString()))
			return playerTags.get(uuid.toString()) + " " + rankTag;

		return rankTag;
	}
	public void setPlayerTag(UUID uuid, String tag, boolean rankTag) {
		final Map<String, String> tags = rankTag ? playerRankTags : playerTags;
		if (tag == null)
			tags.remove(uuid.toString());
		else
			tags.put(uuid.toString(), tag);
	}

	public String getPlayerTagRaw(UUID uuid, boolean rankTag) {
		final Map<String, String> tags = rankTag ? playerRankTags : playerTags;
		return tags.get(uuid.toString());
	}


	private Map<String,String> playernicks = RedisManager.createCachedRedisMap("playernicks");

	public String getPlayerNick(UUID uuid) {
		if(playernicks.containsKey(uuid.toString()))
			return playernicks.get(uuid.toString());
		else
			return null;
	}

	public void setPlayerDisplayName(Player player) {
		String nick = getPlayerNick(player.getUniqueId());
		if (nick == null)
			nick = player.getName();
		player.setDisplayName(nick);
	}

	public void setPlayerNick(UUID uuid, String nick) {
		if (nick == null)
			playernicks.remove(uuid.toString());
		else
			playernicks.put(uuid.toString(), nick);
	}

	public Set<String> playerTpPermissions = new HashSet<>();
	public Set<String> playerSummonPermissions = new HashSet<>();

	public boolean canTp(CommandSender commandSender, Player target) {
		// Prevent teleporting out of jail.
		//noinspection SimplifiableIfStatement
		if ((commandSender instanceof Player) && isPlayerJailed((Player) commandSender))
			return false;

		return canPort(playerTpPermissions, commandSender, target);
	}
	public boolean canSummon(CommandSender commandSender, Player target) {
		// Prevent summoning someone out of jail.
		//noinspection SimplifiableIfStatement
		if (isPlayerJailed(target))
			return false;

		return canPort(playerSummonPermissions, commandSender, target);
	}
	private static boolean canPort(Set<String> playerPortPermissions, CommandSender commandSender, Player target) {
		int commandSenderLevel = getPlayerLevel(commandSender);
		int targetLevel = getPlayerLevel(target);

		String commandSenderName = commandSender.getUniqueId().toString();
		String targetName = target.getUniqueId().toString();

		// Was an exception given?
		if (playerPortPermissions.contains(targetName+" "+commandSenderName))
			return true;

		// Lower-ranked people can only port if an exception is given.
		if (commandSenderLevel < targetLevel)
			return false;

		// Higher-ranked people can always port.
		if (commandSenderLevel > targetLevel && !AbusePotentialManager.isAbusive(commandSender.getUniqueId()))
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
		catch (Exception ignored) { }

		playerSummonPermissions.clear();
		try {
			BufferedReader stream = new BufferedReader(new ConfigFileReader("player-nosummon.txt"));
			String line;
			while((line = stream.readLine()) != null) {
				playerSummonPermissions.add(line);
			}
			stream.close();
		}
		catch (Exception ignored) { }
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
		catch(Exception ignored) { }

		try {
			BufferedWriter stream = new BufferedWriter(new ConfigFileWriter("player-nosummon.txt"));
			for (String element : playerSummonPermissions) {
				stream.write(element);
				stream.newLine();
			}
			stream.close();
		}
		catch(Exception ignored) { }
	}

	private Hashtable<UUID, Long> frozenTimes = new Hashtable<>();
	private Long frozenServerTime;

	public static void sendPacketToPlayer(final Player ply, final Packet packet) {
		((CraftPlayer)ply).getHandle().playerConnection.sendPacket(packet);
	}

	public final void sendPacketToPlayersAround(final Location location, final double radius, final Packet packet) {
		sendPacketToPlayersAround(location, radius, packet, null);
	}
	public final void sendPacketToPlayersAround(final Location location, final double radius, final Packet packet, final Player except) {
		sendPacketToPlayersAround(location, radius, packet, except, Integer.MAX_VALUE);
	}
	public static void sendPacketToPlayersAround(final Location location, double radius, final Packet packet, final Player except, final int maxLevel) {
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
		return ply.getHealth() <= 0 || isPlayerJailed(ply);
	}

	private JailComponent jail;
	public boolean isPlayerJailed(Player ply) {
		if (jail == null)
			jail = (JailComponent) plugin.componentSystem.getComponent("jail");

		return jail.engine.isJailed(ply);
	}

	Map<UUID, UUID> leashMasters = new HashMap<>();
	private int leashTaskId;

	public boolean toggleLeash(Player master, Player slave) {
		if (!leashMasters.containsKey(slave.getUniqueId())) {
			addLeash(master, slave);
			return true;
		}
		else if (leashMasters.get(slave.getUniqueId()).equals(master.getUniqueId())) {
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
					for ( Iterator<Entry<UUID, UUID>> leashMastersIter = leashMasters.entrySet().iterator(); leashMastersIter.hasNext(); ) {
						Entry<UUID, UUID> entry = leashMastersIter.next();

						final UUID slaveName = entry.getKey();
						final Player slave = server.getPlayer(slaveName);

						if (slave == null)
							continue;

						final UUID masterName = entry.getValue();
						final Player master = server.getPlayer(masterName);

						if (master == null || !master.isOnline()) {
							leashMastersIter.remove();
							sendServerMessage("Player "+slave.getName()+" was unleashed automatically.");
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

		leashMasters.put(slave.getUniqueId(), master.getUniqueId());
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
		leashMasters.remove(slave.getUniqueId());

		removeHandler(slave);
	}

	public static Date lastLogout(Player player) {
		File playerFile = getPlayerFile(player.getUniqueId(), "world");
		if (playerFile == null)
			return null;

		if (!playerFile.exists())
			return null;

		return new Date(playerFile.lastModified());
	}

	public static File getPlayerFile(String playerName, String world) {
		return getPlayerFile(FishBansResolver.getUUID(playerName), world);
	}

	public static File getPlayerFile(UUID playerUUID, String world) {
		return new File(world+"/playerdata/"+playerUUID.toString()+".dat");
	}

	public String formatPlayerFull(UUID uuid) {
		String nick = getPlayerNick(uuid);
		if (nick == null)
			nick = getPlayerByUUID(uuid).getName();

		return getPlayerTag(uuid) + nick;
	}

	public enum WeatherType {
		CLEAR("clear"), RAIN("rain"), THUNDERSTORM("thunderstorm");

		public final String name;

		private WeatherType(String name) {
			this.name = name;
		}
	}
	public Hashtable<UUID, WeatherType> frozenWeathers = new Hashtable<>();
	public WeatherType frozenServerWeather;

	public void pushWeather(Player ply) {
		WeatherType weatherType = WeatherType.CLEAR;
		final World world = ply.getWorld();
		if (world.isThundering())
			weatherType = WeatherType.THUNDERSTORM;
		else if (world.hasStorm())
			weatherType = WeatherType.RAIN;

		WeatherType frozenWeather = frozenWeathers.get(ply.getUniqueId());

		if (frozenWeather != null) {
			weatherType = frozenWeather;
		}
		else if (frozenServerWeather != null) {
			weatherType = frozenServerWeather;
		}

		int reason = weatherType == WeatherType.CLEAR ? 1 : 2;
		//@TODO fixme
		sendPacketToPlayer(ply, new PacketPlayOutGameStateChange(reason, 0));
	}

	public void pushWeather() {
		for (Player ply : plugin.getServer().getOnlinePlayers()) {
			pushWeather(ply);
		}
	}

	public Map<UUID, List<String>> autoexecs = new HashMap<>();

	private static final Pattern sectionPattern = Pattern.compile("^\\[(.*)\\]$");
	@Loader({ "autoexecs", "autoexec" })
	public void loadAutoexecs() {
		autoexecs.clear();
		try {
			BufferedReader stream = new BufferedReader(new ConfigFileReader("autoexecs.txt"));

			String line;
			UUID currentPlayerName = null;
			List<String> commands = null;
			while((line = stream.readLine()) != null) {
				if (line.isEmpty())
					continue;

				final Matcher matcher = sectionPattern.matcher(line);
				if (matcher.matches()) {
					if (commands != null && !commands.isEmpty()) {
						autoexecs.put(currentPlayerName, commands);
					}

					currentPlayerName = UUID.fromString(matcher.group(1));
					commands = new ArrayList<>();
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
		catch (IOException ignored) { }
	}
	@Saver({ "autoexecs", "autoexec" })
	public void saveAutoexecs() {
		try {
			BufferedWriter stream = new BufferedWriter(new ConfigFileWriter("autoexecs.txt"));
			for (Entry<UUID, List<String>> entry : autoexecs.entrySet()) {
				stream.write('['+entry.getKey().toString()+']');
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
		catch(IOException ignored) { }
	}

	public Location getPlayerSpawnPosition(Player ply) {
		return getRankSpawnPosition(ply.getWorld(), getPlayerRank(ply));
	}

	public Location getRankSpawnPosition(World world, String rank) {
		try {
			WarpDescriptor warpDescriptor = plugin.warpEngine.getWarp(null, rank +"_spawn");
			if (warpDescriptor == null)
				throw new WarpException("");

			return warpDescriptor.location;
		} catch (WarpException e) {
			final Location location = world.getSpawnLocation();
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
		final String playerName = player.getName();
		return getPlayerRankTag(player.getUniqueId()) + playerName;
	}

	public HashMap<UUID, LinkedList<Location>> teleportHistory = new HashMap<>();
	public void pushPlayerLocationOntoTeleportStack(Player ply) {
		UUID uuid = ply.getUniqueId();

		LinkedList<Location> locs = teleportHistory.get(uuid);
		if(locs == null) {
			locs = new LinkedList<>();
			teleportHistory.put(uuid, locs);
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

	public void vanish(Player player) {
		// TODO: send to vanish plugin.
	}


	public void setFrozenServerTime(long frozenServerTime) {
		this.frozenServerTime = frozenServerTime;
		applyTime();
	}

	public void resetFrozenServerTime() {
		this.frozenServerTime = null;
		applyTime();
	}

	public void setFrozenServerTime(Player player, long setTime) {
		frozenTimes.put(player.getUniqueId(), setTime);
		player.setPlayerTime(setTime, false);
		applyTime(player);
	}

	public void resetFrozenServerTime(Player player) {
		frozenTimes.remove(player.getUniqueId());
		applyTime(player);
	}

	private void applyTime() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			applyTime(player);
		}
	}

	public void applyTime(Player player) {
		Long frozenTime = frozenTimes.get(player.getUniqueId());

		if (frozenTime != null) {
			player.setPlayerTime(frozenTime, false);
		}
		else if (frozenServerTime != null) {
			player.setPlayerTime(frozenServerTime, false);
		}
		else {
			player.resetPlayerTime();
		}
	}

	private static final Set<String> guestRanks = new HashSet<>(Arrays.asList("guest", "pohr"));
	public boolean isGuest(final Player player) {
		return isGuestRank(getPlayerRank(player));
	}

	public static boolean isGuestRank(final String rank) {
		return guestRanks.contains(rank);
	}

    public static final HashMap<UUID, String> playerHosts = new HashMap<>();
    public static final HashMap<UUID, String> playerIPs = new HashMap<>();

    public static String getPlayerIP(CommandSender player) {
        return getPlayerIP(player.getUniqueId());
    }

    public static String getPlayerIP(UUID uuid) {
        synchronized(PlayerHelper.playerIPs) {
            return playerIPs.get(uuid);
        }
    }

    public static String getPlayerHost(CommandSender player) {
        return getPlayerHost(player.getUniqueId());
    }

    public static String getPlayerHost(UUID uuid) {
        synchronized(PlayerHelper.playerIPs) {
            return playerHosts.get(uuid);
        }
    }
}
