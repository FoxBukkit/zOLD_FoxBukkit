package de.doridian.yiffbukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.ChatEvent;
import org.dynmap.DynmapPlugin;
import org.dynmap.Event;
import org.dynmap.SimpleWebChatComponent;
import org.dynmap.Event.Listener;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;
import de.doridian.yiffbukkit.advertisement.AdvertismentSigns;
import de.doridian.yiffbukkit.chatmanager.ChatManager;
import de.doridian.yiffbukkit.commands.ICommand;
import de.doridian.yiffbukkit.irc.Ircbot;
import de.doridian.yiffbukkit.jail.JailEngine;
import de.doridian.yiffbukkit.listeners.InventoryPacketListener;
import de.doridian.yiffbukkit.listeners.SignPortalPlayerListener;
import de.doridian.yiffbukkit.listeners.YiffBukkitBlockListener;
import de.doridian.yiffbukkit.listeners.YiffBukkitEntityListener;
import de.doridian.yiffbukkit.listeners.YiffBukkitPacketListener;
import de.doridian.yiffbukkit.listeners.YiffBukkitPlayerListener;
import de.doridian.yiffbukkit.listeners.YiffBukkitVehicleListener;
import de.doridian.yiffbukkit.mcbans.MCBans;
import de.doridian.yiffbukkit.noexplode.NoExplode;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissionHandler;
import de.doridian.yiffbukkit.portals.PortalEngine;
import de.doridian.yiffbukkit.remote.YiffBukkitRemote;
import de.doridian.yiffbukkit.transmute.Transmute;
import de.doridian.yiffbukkit.util.PlayerHelper;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.vanish.Vanish;
import de.doridian.yiffbukkit.warp.WarpEngine;

/**
 * YiffBukkit
 * @author Doridian
 */
public class YiffBukkit extends JavaPlugin {
	public YiffBukkitPlayerListener playerListener;
	@SuppressWarnings("unused")
	private YiffBukkitBlockListener blockListener;
	@SuppressWarnings("unused")
	private YiffBukkitPacketListener yiffBukkitPacketListener;
	@SuppressWarnings("unused")
	private YiffBukkitEntityListener yiffBukkitEntityListener;
	@SuppressWarnings("unused")
	private YiffBukkitVehicleListener yiffBukkitVehicleListener;
	@SuppressWarnings("unused")
	private InventoryPacketListener inventoryPacketListener;
	@SuppressWarnings("unused")
	private SignPortalPlayerListener signPortalPlayerListener;

	public Vanish vanish;
	public Transmute transmute;
	private YiffBukkitRemote remote;
	public PlayerHelper playerHelper = null;
	public final Utils utils = new Utils(this);
	public AdvertismentSigns adHandler;
	public WarpEngine warpEngine;
	public JailEngine jailEngine;
	public SignSaver signSaver;
	public PortalEngine portalEngine;
	public ChatManager chatManager;

	public Permissions permissions;
	public YiffBukkitPermissionHandler permissionHandler;
	public MCBans mcbans;
	public Ircbot ircbot;
	public WorldEditPlugin worldEdit;
	public DynmapPlugin dynmap;
	public Consumer logBlockConsumer;
	public WorldGuardPlugin worldGuard;

	public boolean serverClosed = false;

	@Override
	@SuppressWarnings("unchecked")
	public void onLoad() {
		final SimplePluginManager pm = (SimplePluginManager)getServer().getPluginManager();
		permissions = new Permissions(this,this.getClassLoader(),this.getFile());

		((List<Plugin>)Utils.getPrivateValue(SimplePluginManager.class, pm, "plugins")).add(permissions);
		((HashMap<String,Plugin>)Utils.getPrivateValue(SimplePluginManager.class, pm, "lookupNames")).put("Permissions", permissions);

		System.out.println( "YiffBukkit started YiffBukkitPermissions!" );
		permissionHandler = (YiffBukkitPermissionHandler)permissions.getHandler();
	}

	public void onDisable() {
		remote.stopme();
		System.out.println( "YiffBukkit is disabled!" );
	}

	public void setupIPC() {
		final PluginManager pm = getServer().getPluginManager();

		worldEdit = (WorldEditPlugin) pm.getPlugin("WorldEdit");
		if (worldEdit != null)
			System.out.println( "YiffBukkit found WorldEdit!" );
		
		worldGuard = (WorldGuardPlugin) pm.getPlugin("WorldGuard");
		if (worldGuard != null)
			System.out.println( "YiffBukkit found WorldGuard!" );

		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				dynmap = (DynmapPlugin) getServer().getPluginManager().getPlugin("dynmap");
				if (dynmap == null)
					return;

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
			}
		});

		LogBlock logBlock = (LogBlock) pm.getPlugin("LogBlock");

		if (logBlock != null) {
			logBlockConsumer = logBlock.getConsumer();
			System.out.println( "YiffBukkit found LogBlock!" );
		}
	}

	public void onEnable() {
		setupIPC();

		new NoExplode(this);
		playerHelper = new PlayerHelper(this);
		warpEngine = new WarpEngine(this);
		jailEngine = new JailEngine(this);
		signSaver = new SignSaver(this);
		//portalEngine = new PortalEngine(this);
		System.out.println("YiffBukkit state components loaded.");
		StateContainer.loadAll();
		System.out.println("YiffBukkit state component config loaded.");
		chatManager = new ChatManager(this);

		playerListener = new YiffBukkitPlayerListener(this);
		blockListener = new YiffBukkitBlockListener(this);
		yiffBukkitPacketListener = new YiffBukkitPacketListener(this);
		yiffBukkitEntityListener = new YiffBukkitEntityListener(this);
		yiffBukkitVehicleListener = new YiffBukkitVehicleListener(this);
		inventoryPacketListener = new InventoryPacketListener(this);
		signPortalPlayerListener = new SignPortalPlayerListener(this);
		vanish = new Vanish(this);
		transmute = new Transmute(this);
		adHandler = new AdvertismentSigns(this);

		System.out.println("YiffBukkit components loaded.");
		mcbans = new MCBans(this);
		System.out.println("YiffBukkit MCBans loaded.");
		ircbot = new Ircbot(this).init();
		System.out.println("YiffBukkit IRC bot loaded.");

		remote = new YiffBukkitRemote(this, playerListener);
		remote.start();
		System.out.println("YiffBukkit Remote loaded.");

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

		System.out.println( "YiffBukkit is enabled!" );
	}

	public Hashtable<String,ICommand> getCommands() {
		return playerListener.commands;
	}

	public World getOrCreateWorld(String name, Environment env) {
		name = name.toLowerCase();
		/*for (World world : getServer().getWorlds()) {
			if (world.getName().equals(name)) return world;
		}*/
		return getServer().createWorld(name, env);
	}
}