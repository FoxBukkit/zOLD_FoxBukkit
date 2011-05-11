package de.doridian.yiffbukkit.listeners;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.ToolBind;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.*;
import de.doridian.yiffbukkit.util.PlayerHelper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

/**
 * Handle events for all Player related events
 * @author Doridian
 */
public class YiffBukkitPlayerListener extends PlayerListener {
	public static YiffBukkitPlayerListener instance;
	public final YiffBukkit plugin;
	private final PlayerHelper playerHelper;

	public YiffBukkitPlayerListener(YiffBukkit plug) {
		instance = this;
		plugin = plug;
		playerHelper = plugin.playerHelper;

		for (Class<? extends ICommand> commandClass : getSubClasses(ICommand.class)) {
			try {
				commandClass.newInstance();
			}
			catch (InstantiationException e) {
				// We try to instantiate an interface
				// or an object that does not have a 
				// default constructor
				continue;
			}
			catch (IllegalAccessException e) {
				// The class/ctor is not public
				continue;
			}
			catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_LOGIN, this, Priority.Highest, plugin);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this, Priority.Highest, plugin);
		pm.registerEvent(Event.Type.PLAYER_KICK, this, Priority.Highest, plugin);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this, Priority.Highest, plugin);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this, Priority.Lowest, plugin);
		pm.registerEvent(Event.Type.PLAYER_CHAT, this, Priority.High, plugin);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this, Priority.Normal, plugin);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this, Priority.Normal, plugin);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, this, Priority.Normal, plugin);

		pm.registerEvent(Event.Type.PLAYER_CHAT, new PlayerListener() {
			public void onPlayerChat(PlayerChatEvent event) {
				if (event.isCancelled())
					return;

				final Player ply = event.getPlayer();
				String conversationTarget = playerHelper.conversations.get(ply.getName());
				String formattedMessage = String.format(event.getFormat(), ply.getDisplayName(), event.getMessage());
				if (conversationTarget == null) {
					plugin.chatManager.pushCurrentOrigin(ply);
					plugin.getServer().broadcastMessage(formattedMessage);
					plugin.chatManager.popCurrentOrigin();

					event.setCancelled(true);
					return;
				}

				formattedMessage = "§e[CONV]§f "+formattedMessage;

				plugin.chatManager.pushCurrentOrigin(ply);
				ply.sendMessage(formattedMessage);
				plugin.getServer().getPlayer(conversationTarget).sendMessage(formattedMessage);
				plugin.chatManager.popCurrentOrigin();

				event.setCancelled(true);
			}
		}, Priority.Monitor, plugin);
	}

	private static <T> List<Class<? extends T>> getSubClasses(Class<T> baseClass) {
		final List<Class<? extends T>> ret = new ArrayList<Class<? extends T>>();
		final File file;
		try {
			final ProtectionDomain protectionDomain = baseClass.getProtectionDomain();
			final CodeSource codeSource = protectionDomain.getCodeSource();
			final URL location = codeSource.getLocation();
			final URI uri = location.toURI();
			file = new File(uri);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
			return ret;
		}
		final String[] fileList;

		String packageName = baseClass.getPackage().getName();
		if (file.isDirectory()) {
			String packageFolderName = "/"+packageName.replace('.','/');

			URL url = baseClass.getResource(packageFolderName);
			if (url == null)
				return ret;

			File directory = new File(url.getFile());
			if (!directory.exists())
				return ret;

			// Get the list of the files contained in the package
			fileList = directory.list();
		}
		else if (file.isFile()) {
			final List<String> tmp = new ArrayList<String>();
			final JarFile jarFile;
			try {
				jarFile = new JarFile(file);
			}
			catch (IOException e) {
				e.printStackTrace();
				return ret;
			}

			Pattern pathPattern = Pattern.compile(packageName.replace('.','/')+"/(.+\\.class)");
			final Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				Matcher matcher = pathPattern.matcher(entries.nextElement().getName());
				if (!matcher.matches())
					continue;

				tmp.add(matcher.group(1));
			}

			fileList = tmp.toArray(new String[tmp.size()]);
		}
		else {
			return ret;
		}

		Pattern classFilePattern = Pattern.compile("(.+)\\.class");
		for (String fileName : fileList) {
			// we are only interested in .class files
			Matcher matcher = classFilePattern.matcher(fileName);
			if (!matcher.matches())
				continue;

			// removes the .class extension
			String classname = matcher.group(1);
			try {
				final Class<?> classObject = Class.forName(packageName+"."+classname);
				final Class<? extends T> classT = classObject.asSubclass(baseClass);

				// Try to create an instance of the object
				ret.add(classT);
			}
			catch (ClassNotFoundException e) {
				System.err.println(e);
			}
			catch (ClassCastException e) {
				continue;
			}
		}

		return ret;
	}

	public void registerCommand(String name, ICommand command) {
		commands.put(name, command);
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		final String playerName = event.getPlayer().getName();
		if (!playerName.matches("^.*[A-Za-z].*$")) {
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "[YB] Sorry, get some letters into your name.");
			return;
		}

		String rank = plugin.playerHelper.GetPlayerRank(event.getPlayer());
		if (rank.equals("banned")) {
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "[YB] You're banned");
			return;
		}
		(new HackCheckThread(playerName)).start();
	}

	class HackCheckThread extends Thread {
		private String plyName;
		public HackCheckThread(String plyNameX) {
			plyName = plyNameX;
		}

		public void run() {
			try {
				URL url = new URL("http://cursecraft.com/mc_validation.php?username=" + plyName);
				URLConnection conn = url.openConnection();
				conn.connect();
				BufferedReader buffre = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String ret = buffre.readLine().trim().toLowerCase();
				buffre.close();
				if (ret.equals("yes")) {
					Player ply = plugin.playerHelper.MatchPlayerSingle(plyName);

					plugin.playerHelper.SendDirectedMessage(ply, "Your account might have been hacked!", '4');
					plugin.playerHelper.SendDirectedMessage(ply, "Check http://bit.ly/eLIUhb for further info!", '4');

					plugin.playerHelper.SendServerMessage("The account of " + ply.getName() + " might have been hacked!", 3, '4');
					plugin.playerHelper.SendServerMessage("Check http://bit.ly/eLIUhb for further info!", 3, '4');
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		String nick = plugin.playerHelper.GetPlayerNick(player.getName());
		if (nick == null)
			nick = player.getName();
		player.setDisplayName(nick);

		final File playerFile = PlayerHelper.getPlayerFile(player.getName(), "world");
		event.setJoinMessage(null);
		plugin.chatManager.pushCurrentOrigin(player);
		if (playerFile != null && playerFile.exists())
			plugin.getServer().broadcastMessage("§2[+] §e" + plugin.playerHelper.GetFullPlayerName(player) + "§e joined!");
		else
			plugin.getServer().broadcastMessage("§2[+] §e" + plugin.playerHelper.GetFullPlayerName(player) + "§e joined for the first time!");

		plugin.playerHelper.updateToolMappings(player);
		plugin.chatManager.popCurrentOrigin();
		plugin.playerHelper.pushWeather(player);
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		plugin.chatManager.pushCurrentOrigin(event.getPlayer());
		plugin.getServer().broadcastMessage("§4[-] §e" + plugin.playerHelper.GetFullPlayerName(event.getPlayer()) + "§e disconnected!");
		plugin.chatManager.popCurrentOrigin();

		for (Map<Player, ?> map : playerHelper.registeredMaps) {
			map.remove(event.getPlayer());
		}
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		event.setLeaveMessage(null);
		plugin.chatManager.pushCurrentOrigin(event.getPlayer());
		plugin.getServer().broadcastMessage("§4[-] §e" + plugin.playerHelper.GetFullPlayerName(event.getPlayer()) + "§e was kicked (" + event.getReason() + ")!");
		plugin.chatManager.popCurrentOrigin();
	}

	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		Player ply = event.getPlayer();
		if (ply.getHealth() <= 0) {
			event.setCancelled(true);
			return;
		}
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player ply = event.getPlayer();
		Location location = ply.getWorld().getSpawnLocation();
		location.setX(location.getX()+0.5);
		location.setZ(location.getZ()+0.5);
		event.setRespawnLocation(location);
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled())
			return;

		String msg = event.getMessage();
		final Player ply = event.getPlayer();
		if (msg.charAt(0) == '!') {
			plugin.playerHelper.SendDirectedMessage(ply, "!commands are disabled because they show up in the web chat. Please use /commands.");
			event.setCancelled(true);
			return;
		}

		event.setFormat(plugin.playerHelper.GetPlayerTag(ply) + "%s:§f %s");
	}

	public Hashtable<String,ICommand> commands = new Hashtable<String,ICommand>();
	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled())
			return;

		final Player ply = event.getPlayer();
		plugin.chatManager.pushCurrentOrigin(ply);
		if (runCommand(ply, event.getMessage().substring(1).trim())) {
			event.setCancelled(true);
			event.setMessage("/youdontwantthiscommand "+event.getMessage());
		}
		plugin.chatManager.popCurrentOrigin();
	}

	public boolean runCommand(Player ply, String baseCmd) {
		int posSpace = baseCmd.indexOf(' ');
		String cmd; String args[]; String argStr;
		if (posSpace < 0) {
			cmd = baseCmd;
			args = new String[0];
			argStr = "";
		}
		else {
			cmd = baseCmd.substring(0, posSpace).trim();
			argStr = baseCmd.substring(posSpace).trim();
			args = argStr.split(" +");
		}
		if (commands.containsKey(cmd)) {
			ICommand icmd = commands.get(cmd);
			try {
				if(!icmd.CanPlayerUseCommand(ply)) {
					throw new PermissionDeniedException();
				}
				Logger.getLogger("Minecraft").log(Level.INFO, "Command: "+ply.getName()+": "+baseCmd);
				icmd.Run(ply,args,argStr);
			}
			catch (YiffBukkitCommandException e) {
				plugin.playerHelper.SendDirectedMessage(ply,e.getMessage(), e.getColor());
			}
			catch (Exception e) {
				if (plugin.playerHelper.GetPlayerLevel(ply) >= 4) {
					plugin.playerHelper.SendDirectedMessage(ply,"Command error: "+e+" in "+e.getStackTrace()[0]);
					e.printStackTrace();
				}
				else {
					plugin.playerHelper.SendDirectedMessage(ply,"Command error!");
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		/*if (event.isCancelled())
			return;*/

		Player ply = event.getPlayer();
		Integer selflvl = playerHelper.GetPlayerLevel(ply);
		Block clickedBlock = event.getClickedBlock();
		switch (event.getAction()) {
		case LEFT_CLICK_BLOCK:
			if (selflvl < 1)
				break;

			final Block block = clickedBlock.getFace(event.getBlockFace());
			if (block.getTypeId() == 51) // fire
				block.setTypeId(0);

			break;

		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			try {
				Material itemMaterial = event.getMaterial();
				// This will not be logged by bigbrother so I only allowed it for ops+ for now.
				// A fix would be to modify the event a bit to make BB log this. 
				if (selflvl >= 3 && itemMaterial == Material.INK_SACK) {
					if (clickedBlock.getType() == Material.WOOL) {
						ItemStack item = event.getItem();
						clickedBlock.setData((byte)(15 - item.getDurability()));
						int newAmount = item.getAmount()-1;
						if (newAmount > 0)
							item.setAmount(newAmount);
						else
							ply.setItemInHand(null);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Material itemMaterial = event.getMaterial();

				String key = ply.getName()+" "+itemMaterial.name();
				ToolBind toolBind = plugin.playerHelper.toolMappings.get(key);
				if (toolBind != null) {
					event.setCancelled(true);
					try {
						toolBind.run(event);
					}
					catch (YiffBukkitCommandException e) {
						plugin.playerHelper.SendDirectedMessage(ply,e.getMessage(), e.getColor());
					}
					catch (Exception e) {
						if (plugin.playerHelper.GetPlayerLevel(ply) >= 4) {
							plugin.playerHelper.SendDirectedMessage(ply,"Command error: "+e+" in "+e.getStackTrace()[0]);
							e.printStackTrace();
						}
						else {
							plugin.playerHelper.SendDirectedMessage(ply,"Command error!");
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			break;
		}
	}

	@Override
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		ItemStack item = event.getItemStack();
		Material itemMaterial = item.getType();
		if(itemMaterial == Material.AIR) return;

		Player ply = event.getPlayer();
		if(playerHelper.isPlayerDisabled(ply)) {
			item.setType(Material.GOLD_HOE);
			item.setAmount(1);
			item.setDurability(Short.MAX_VALUE);
			return;
		}

		Integer selflvl = playerHelper.GetPlayerLevel(ply);
		if(selflvl < 0 || (YiffBukkitBlockListener.blocklevels.containsKey(itemMaterial) && selflvl < YiffBukkitBlockListener.blocklevels.get(itemMaterial))) {
			playerHelper.SendServerMessage(ply.getName() + " tried to spawn illegal block " + itemMaterial.toString());
			item.setType(Material.GOLD_HOE);
			item.setAmount(1);
			item.setDurability(Short.MAX_VALUE);
			return;
		}
	}
}
