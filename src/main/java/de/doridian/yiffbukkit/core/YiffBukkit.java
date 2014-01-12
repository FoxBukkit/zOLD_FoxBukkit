package de.doridian.yiffbukkit.core;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;
import de.doridian.yiffbukkit.bans.Bans;
import de.doridian.yiffbukkit.chat.listeners.ChatListener;
import de.doridian.yiffbukkit.chat.manager.ChatManager;
import de.doridian.yiffbukkit.componentsystem.ComponentSystem;
import de.doridian.yiffbukkit.core.listeners.YiffBukkitBlockListener;
import de.doridian.yiffbukkit.core.listeners.YiffBukkitBungeeLink;
import de.doridian.yiffbukkit.core.listeners.YiffBukkitEntityListener;
import de.doridian.yiffbukkit.core.util.AutoCleanup;
import de.doridian.yiffbukkit.core.util.MessageHelper;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.StateContainer;
import de.doridian.yiffbukkit.main.commands.system.CommandSystem;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.console.YiffBukkitConsoleCommands;
import de.doridian.yiffbukkit.main.listeners.YiffBukkitPlayerListener;
import de.doridian.yiffbukkit.main.util.Configuration;
import de.doridian.yiffbukkit.main.util.PersistentScheduler;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.permissions.AbusePotentialManager;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissionHandler;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissions;
import de.doridian.yiffbukkit.portal.PortalEngine;
import de.doridian.yiffbukkit.remote.YiffBukkitRemote;
import de.doridian.yiffbukkit.spawning.SpawnUtils;
import de.doridian.yiffbukkit.transmute.Transmute;
import de.doridian.yiffbukkit.warp.WarpEngine;
import de.doridian.yiffbukkit.warp.listeners.SignPortalPlayerListener;
import de.doridian.yiffbukkit.yiffpoints.YBBank;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_7_R1.command.ColouredConsoleSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;

/**
 * YiffBukkit
 * @author Doridian
 */
public class YiffBukkit extends JavaPlugin {
	public static YiffBukkit instance;
	public ComponentSystem componentSystem = new ComponentSystem();

	public Transmute transmute;
	private YiffBukkitRemote remote;
	public final PlayerHelper playerHelper = new PlayerHelper(this);
	@SuppressWarnings("unused")
	public final MessageHelper messageHelper = new MessageHelper();
	public final Utils utils = new Utils(this);
	public final SpawnUtils spawnUtils = new SpawnUtils(this);
	public WarpEngine warpEngine;
	public PortalEngine portalEngine;
	public ChatManager chatManager;
	public PersistentScheduler persistentScheduler;

	public Bans bans;
	public WorldEditPlugin worldEdit;
	public Consumer logBlockConsumer;

	public CommandSystem commandSystem;
	public final YBBank bank = new YBBank();

	public YiffBukkit() {
		instance = this;
		componentSystem.registerComponents();
	}

	public void onDisable() {
		remote.stopme();
		log("Plugin disabled!" ) ;
	}

	public void setupIPC() {
		final PluginManager pm = getServer().getPluginManager();

		worldEdit = (WorldEditPlugin) pm.getPlugin("WorldEdit");
		if (worldEdit != null)
			log( "Found WorldEdit!" );

		LogBlock logBlock = (LogBlock) pm.getPlugin("LogBlock");

		if (logBlock != null) {
			logBlockConsumer = logBlock.getConsumer();
			log( "Found LogBlock!" );
		}
	}

	public void onEnable() {
		setupIPC();

		YiffBukkitPermissionHandler.instance.load();

		warpEngine = new WarpEngine(this);
		persistentScheduler = new PersistentScheduler();
		//portalEngine = new PortalEngine(this);
		new AbusePotentialManager();
		log("State components loaded.");
		StateContainer.loadAll();
		log("State component config loaded.");
		chatManager = new ChatManager(this);

		commandSystem = new CommandSystem(this);
		componentSystem.registerCommands();
		new AutoCleanup();
		new YiffBukkitPlayerListener();
		new YiffBukkitBlockListener();
		new YiffBukkitEntityListener();
		new SignPortalPlayerListener();
		transmute = new Transmute(this);
		new ChatListener();
		new YiffBukkitConsoleCommands(this);
		componentSystem.registerListeners();
		new YiffBukkitBungeeLink();

		log("Core components loaded.");
		bans = new Bans(this);
		log("Bans loaded.");

		remote = new YiffBukkitRemote(this);
		remote.start();
		log("Remote loaded.");

		log( "Plugin enabled!" );

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "yiffcraft");
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "yiffcraftp");
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "yiffcraft", new PluginMessageListener() {
			@Override
			public void onPluginMessageReceived(String s, Player ply, byte[] bytes) {
				String argStr = new String(bytes);

				playerHelper.setYiffcraftState(ply, true);
				//SSLUtils.nagIfNoSSL(playerHelper, ply);

				if(argStr.equalsIgnoreCase("getcommands")) {
					playerHelper.sendYiffcraftClientCommand(ply, 'c', Configuration.getValue("yiffcraft-command-url", "http://commands.yiffcraft.net/servers/mc_doridian_de.txt"));
				} else if(argStr.equalsIgnoreCase("writecommands")) {
					try {
						Map<String, ICommand> commands = commandSystem.getCommands();

						PrintWriter writer = new PrintWriter(new FileWriter("yb_commands.txt"));

						for(Map.Entry<String, ICommand> command : commands.entrySet()) {
							ICommand cmd = command.getValue();
							String help = cmd.getHelp();
							if(help.indexOf("\n") > 0) {
								help = help.substring(0, help.indexOf("\n"));
							}
							writer.println('/' + command.getKey() + '|' + cmd.getUsage() + " - " + help);
						}

						writer.close();
					}
					catch(Exception e) { PlayerHelper.sendDirectedMessage(ply, "Error: " + e.getMessage()); }
				}
			}
		});

		YiffBukkitPermissions.init();

		try {
			Runtime.getRuntime().exec("./server_online.sh");
		} catch (IOException e) {
			System.err.println("Could not run server_online.sh, skipping.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void log(String msg) {
		log(Level.INFO, msg);
	}

	public void log(Level level, String msg) {
		getLogger().log(level, msg);
	}
	
	public void sendConsoleMsg(String msg) {
		sendConsoleMsg(msg, true);
	}

	public void sendConsoleMsg(String msg, boolean addprefix) {
		if(addprefix) {
			msg = "\u00a7d[YB]\u00a7f " + msg;
		}
		ColouredConsoleSender.getInstance().sendMessage(msg);
	}

	public World getOrCreateWorld(String name) {
		name = name.toLowerCase();
		World world = getServer().getWorld(name);
		if(world == null) {
			return getServer().createWorld(WorldCreator.name(name));
		}
		return world;
	}

	public World getOrCreateWorld(String name, Environment env) {
		name = name.toLowerCase();
		World world = getServer().getWorld(name);
		if(world == null) {
			return getServer().createWorld(WorldCreator.name(name).environment(env));
		}
		return world;
	}
}
