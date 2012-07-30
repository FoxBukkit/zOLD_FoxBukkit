package de.doridian.yiffbukkitsplit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;
import de.doridian.yiffbukkit.advanced.listeners.YiffBukkitHeadChopOffListener;
import de.doridian.yiffbukkit.advanced.listeners.YiffBukkitPacketListener;
import de.doridian.yiffbukkit.chat.ChatListener;
import de.doridian.yiffbukkit.chat.manager.ChatManager;
import de.doridian.yiffbukkit.fun.listeners.MinecartCollisionListener;
import de.doridian.yiffbukkit.main.StateContainer;
import de.doridian.yiffbukkit.main.commands.CommandSystem;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.listeners.YiffBukkitPlayerListener;
import de.doridian.yiffbukkit.main.util.Configuration;
import de.doridian.yiffbukkit.main.util.PersistentScheduler;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissionHandler;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissions;
import de.doridian.yiffbukkit.warp.WarpEngine;
import de.doridian.yiffbukkit.main.console.YiffBukkitConsoleCommands;
import de.doridian.yiffbukkit.irc.Ircbot;
import de.doridian.yiffbukkit.warp.jail.JailEngine;
import de.doridian.yiffbukkit.warp.portals.SignPortalPlayerListener;
import de.doridian.yiffbukkitsplit.listeners.YiffBukkitBlockListener;
import de.doridian.yiffbukkitsplit.listeners.YiffBukkitEntityListener;
import de.doridian.yiffbukkit.mcbans.MCBans;
import de.doridian.yiffbukkit.warp.portals.PortalEngine;
import de.doridian.yiffbukkit.yiffpoints.YBBank;
import de.doridian.yiffbukkit.remote.YiffBukkitRemote;
import de.doridian.yiffbukkit.spawning.SpawnUtils;
import de.doridian.yiffbukkit.ssl.ServerSSLSocket;
import de.doridian.yiffbukkit.transmute.Transmute;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.dynmap.ChatEvent;
import org.dynmap.DynmapPlugin;
import org.dynmap.Event;
import org.dynmap.Event.Listener;
import org.dynmap.SimpleWebChatComponent;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * YiffBukkit
 * @author Doridian
 */
public class YiffBukkit extends JavaPlugin {
	public static YiffBukkit instance;
	@SuppressWarnings("unused")
	private YiffBukkitPlayerListener playerListener;
	@SuppressWarnings("unused")
	private YiffBukkitBlockListener blockListener;
	@SuppressWarnings("unused")
	private YiffBukkitPacketListener yiffBukkitPacketListener;
	@SuppressWarnings("unused")
	private YiffBukkitEntityListener yiffBukkitEntityListener;
	@SuppressWarnings("unused")
	private MinecartCollisionListener minecartCollisionListener;
	@SuppressWarnings("unused")
	private SignPortalPlayerListener signPortalPlayerListener;
	@SuppressWarnings("unused")
	private ChatListener chatListener;
	@SuppressWarnings("unused")
	private YiffBukkitConsoleCommands consoleCommands;
	@SuppressWarnings("unused")
	private YiffBukkitHeadChopOffListener headChopOffListener;

	public Transmute transmute;
	private YiffBukkitRemote remote;
	public PlayerHelper playerHelper = null;
	public final Utils utils = new Utils(this);
	public final SpawnUtils spawnUtils = new SpawnUtils(this);
	public WarpEngine warpEngine;
	public JailEngine jailEngine;
	public PortalEngine portalEngine;
	public ChatManager chatManager;
	public PersistentScheduler persistentScheduler;

	public MCBans mcbans;
	public Ircbot ircbot;
	public WorldEditPlugin worldEdit;
	public DynmapPlugin dynmap;
	public Consumer logBlockConsumer;

	public ServerSSLSocket serverSSLSocket;

	public boolean serverClosed = false;
	public CommandSystem commandSystem;
	public final YBBank bank = new YBBank();

	public YiffBukkit() {
		instance = this;
	}

	public void onDisable() {
		if (serverSSLSocket != null)
			serverSSLSocket.stopme();
		remote.stopme();
		log("Plugin disabled!" ) ;
	}

	public void setupIPC() {
		final PluginManager pm = getServer().getPluginManager();

		worldEdit = (WorldEditPlugin) pm.getPlugin("WorldEdit");
		if (worldEdit != null)
			log( "Found WorldEdit!" );

		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				try {
					Plugin tmp = getServer().getPluginManager().getPlugin("dynmap");
					if (tmp == null)
						return;
					dynmap = (DynmapPlugin)tmp;

					Event<?> event = dynmap.events.events.get("webchat");

					// listeners = event.listeners;
					List<Event.Listener<ChatEvent>> listeners = Utils.getPrivateValue(Event.class, event, "listeners");
					if (listeners == null)
						return;

					// Remove the old listener
					for (Iterator<Listener<ChatEvent>> it = listeners.iterator(); it.hasNext(); ) {
						Listener<ChatEvent> foo = it.next();
						if (!foo.getClass().getEnclosingClass().equals(SimpleWebChatComponent.class))
							continue;

						it.remove();
					}

					listeners.add(new Listener<ChatEvent>() {
						@Override
						public void triggered(ChatEvent t) {
							String name = t.name.replace('\u00a7','$');
							name = playerHelper.getPlayerNameByIP(name);
							ircbot.sendToChannel("[WEB] " + name + ": " + t.message.replace('\u00a7','$'));
							getServer().broadcast(Server.BROADCAST_CHANNEL_USERS, "[WEB]" + name + ": " + t.message.replace('\u00a7','$'));
						}
					});
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});

		LogBlock logBlock = (LogBlock) pm.getPlugin("LogBlock");

		if (logBlock != null) {
			logBlockConsumer = logBlock.getConsumer();
			log( "Found LogBlock!" );
		}
	}

	public void onEnable() {
		setupIPC();

		YiffBukkitPermissionHandler.instance.load();

		playerHelper = new PlayerHelper(this);
		warpEngine = new WarpEngine(this);
		jailEngine = new JailEngine(this);
		persistentScheduler = new PersistentScheduler();
		//portalEngine = new PortalEngine(this);
		log("State components loaded.");
		StateContainer.loadAll();
		log("State component config loaded.");
		chatManager = new ChatManager(this);

		commandSystem = new CommandSystem(this);
		playerListener = new YiffBukkitPlayerListener();
		blockListener = new YiffBukkitBlockListener(this);
		yiffBukkitPacketListener = new YiffBukkitPacketListener(this);
		yiffBukkitEntityListener = new YiffBukkitEntityListener(this);
		minecartCollisionListener = new MinecartCollisionListener(this);
		signPortalPlayerListener = new SignPortalPlayerListener(this);
		transmute = new Transmute(this);
		chatListener = new ChatListener(this);
		consoleCommands = new YiffBukkitConsoleCommands(this);
		headChopOffListener = new YiffBukkitHeadChopOffListener(this);

		log("Core components loaded.");
		mcbans = new MCBans(this);
		log("MCBans loaded.");
		ircbot = new Ircbot(this).init();
		log("IRC bot loaded.");

		remote = new YiffBukkitRemote(this);
		remote.start();
		log("Remote loaded.");

		try {
			serverSSLSocket = new ServerSSLSocket(this);
			serverSSLSocket.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				List<LivingEntity> removeList = new ArrayList<LivingEntity >(); 
				for (World world : getServer().getWorlds()) {
					for (LivingEntity livingEntity : world.getLivingEntities()) {
						if (livingEntity instanceof Slime) {
							removeList.add(livingEntity);
						}
					}
				}

				for (LivingEntity livingEntity : removeList) {
					livingEntity.remove();
				}
			}
		}, 1000, 200);

		log( "Plugin enabled!" );

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "yiffcraft");
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

	public World getOrCreateWorld(String name, Environment env) {
		name = name.toLowerCase();
		/*for (World world : getServer().getWorlds()) {
			if (world.getName().equals(name)) return world;
		}*/
		return getServer().getWorld(name);
	}

	public boolean hasSSL() {
		return serverSSLSocket != null;
	}
}
