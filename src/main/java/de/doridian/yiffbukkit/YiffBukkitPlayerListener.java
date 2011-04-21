package de.doridian.yiffbukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

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

		String packageName = "de.doridian.yiffbukkit.commands";
		String packageFolderName = "/"+packageName.replace('.','/');

		URL url = ICommand.class.getResource(packageFolderName);
		File directory = new File(url.getFile());
		if (directory.exists()) {
			// Get the list of the files contained in the package
			for (String fileName : directory.list()) {
				// we are only interested in .class files
				if (fileName.endsWith(".class")) {
					// removes the .class extension
					String classname = fileName.substring(0,fileName.length()-6);
					try {
						final Class<?> classObject = Class.forName(packageName+"."+classname);
						final Class<? extends ICommand> classICommand = classObject.asSubclass(ICommand.class);
						// Try to create an instance of the object
						classICommand.newInstance();
					} catch (ClassNotFoundException cnfex) {
						System.err.println(cnfex);
					} catch (ClassCastException cnfex) {
						continue;
					} catch (InstantiationException iex) {
						// We try to instantiate an interface
						// or an object that does not have a 
						// default constructor
						continue;
					} catch (IllegalAccessException iaex) {
						// The class is not public
						continue;
					}
				}
			}
		}
		
		//new MeCommand();
		commands.put("pm", new PmCommand(this));

		commands.put("who", new WhoCommand());
		commands.put("help", new HelpCommand(this));

		commands.put("setrank", new SetRankCommand(this));
		commands.put("settag", new SetTagCommand(this));
		commands.put("setnick", new SetNickCommand(this));

		commands.put("kick", new KickCommand(this));
		//new BanCommand();
		commands.put("unban", new UnbanCommand(this));
		commands.put("pardon", new UnbanCommand(this));
		commands.put("kickall", new KickAllCommand(this));
		commands.put("mute", new MuteCommand(this));

		commands.put("banish", new BanishCommand(this));
		commands.put("vanish", new VanishCommand(this));

		commands.put("tp", new TpCommand(this));
		commands.put("summon", new SummonCommand(this));
		commands.put("send", new SendCommand(this));

		commands.put("notp", new NoTpCommand(this));
		commands.put("nosummon", new NoSummonCommand(this));
		commands.put("noport", new NoPortCommand(this));

		commands.put("home", new HomeCommand(this));
		commands.put("sethome", new SetHomeCommand(this));
		commands.put("spawn", new SpawnCommand(this));
		commands.put("setspawn", new SetSpawnCommand(this));
		//new CompassCommand();

		commands.put("give", new GiveCommand(this));
		//new ThrowCommand();
		commands.put("clear", new ClearCommand(this));
		//new ButcherCommand();

		commands.put("time", new TimeCommand(this));
		commands.put("servertime", new ServerTimeCommand(this));

		commands.put("reloadads", new ReloadAdsCommand(this));

		commands.put("warp", new WarpCommand(this));
		commands.put("setwarp", new SetWarpCommand(this));

		commands.put("jail", new JailCommand(this));
		commands.put("setjail", new SetJailCommand(this));

		commands.put("setportal", new SetPortalCommand(this));

		commands.put("isee", new ISeeCommand(this));
		commands.put("leash", new LeashCommand(this));
		commands.put("bind", new BindCommand(this));
		commands.put("god", new GodCommand(this));
		commands.put("heal", new HealCommand(this));

		commands.put("§", new CheaterCommand(this));

		commands.put("setpass", new PasswordCommand(this));

		commands.put("rcon", new ConsoleCommand(this));

		//new ConversationCommand();

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

		if (playerFileExists(player))
			event.setJoinMessage("§2[+] §e" + plugin.playerHelper.GetFullPlayerName(player) + "§e joined!");
		else
			event.setJoinMessage("§2[+] §e" + plugin.playerHelper.GetFullPlayerName(player) + "§e joined for the first time!");

		plugin.playerHelper.updateToolMappings(player);
	}

	private boolean playerFileExists(Player player) {
		File playerFile = new File("world/players/"+player.getName()+".dat");
		return playerFile.exists();
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage("§4[-] §e" + plugin.playerHelper.GetFullPlayerName(event.getPlayer()) + "§e disconnected!");
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		event.setLeaveMessage("§4[-] §e" + plugin.playerHelper.GetFullPlayerName(event.getPlayer()) + "§e was kicked (" + event.getReason() + ")!");
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

		String conversationTarget = playerHelper.conversations.get(ply.getName());
		if (conversationTarget == null)
			return;

		String formattedMessage = String.format(event.getFormat(), ply.getDisplayName(), event.getMessage());
		formattedMessage = "§e[CONV]§f "+formattedMessage;

		ply.sendMessage(formattedMessage);
		plugin.getServer().getPlayer(conversationTarget).sendMessage(formattedMessage);

		event.setCancelled(true);
	}

	public Hashtable<String,ICommand> commands = new Hashtable<String,ICommand>();
	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled())
			return;

		if (runCommand(event.getPlayer(), event.getMessage().substring(1).trim())) {
			event.setCancelled(true);
			event.setMessage("/youdontwantthiscommand "+event.getMessage());
		}
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
		switch (event.getAction()) {
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			try {
				Material itemMaterial = event.getMaterial();
				Player ply = event.getPlayer();
				Integer selflvl = playerHelper.GetPlayerLevel(ply);
				// This will not be logged by bigbrother so I only allowed it for ops+ for now.
				// A fix would be to modify the event a bit to make BB log this. 
				if (selflvl >= 3 && itemMaterial == Material.INK_SACK) {
					Block block = event.getClickedBlock();
					if (block.getType() == Material.WOOL) {
						ItemStack item = event.getItem();
						block.setData((byte)(15 - item.getDurability()));
						int newAmount = item.getAmount()-1;
						if (newAmount > 0)
							item.setAmount(newAmount);
						else
							ply.setItemInHand(null);
					}
				}
			}
			finally {
				Player ply = event.getPlayer();
				Material itemMaterial = event.getMaterial();

				String key = ply.getName()+" "+itemMaterial.name();
				ToolBind runnable = plugin.playerHelper.toolMappings.get(key);
				if (runnable != null) {
					try {
						runnable.run(event);
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
