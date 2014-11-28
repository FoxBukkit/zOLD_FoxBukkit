/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.core;

import com.foxelbox.dependencies.threading.SimpleThreadCreator;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;
import com.foxelbox.dependencies.config.Configuration;
import com.foxelbox.dependencies.redis.RedisManager;
import com.foxelbox.foxbukkit.bans.Bans;
import com.foxelbox.foxbukkit.chat.listeners.ChatListener;
import com.foxelbox.foxbukkit.componentsystem.ComponentSystem;
import com.foxelbox.foxbukkit.core.listeners.FoxBukkitBlockListener;
import com.foxelbox.foxbukkit.core.listeners.FoxBukkitBungeeLink;
import com.foxelbox.foxbukkit.core.listeners.FoxBukkitEntityListener;
import com.foxelbox.foxbukkit.core.util.AutoCleanup;
import com.foxelbox.foxbukkit.core.util.MessageHelper;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.foxpoints.FBBank;
import com.foxelbox.foxbukkit.main.StateContainer;
import com.foxelbox.foxbukkit.main.commands.system.CommandSystem;
import com.foxelbox.foxbukkit.main.console.FoxBukkitConsoleCommands;
import com.foxelbox.foxbukkit.main.listeners.FoxBukkitPlayerListener;
import com.foxelbox.foxbukkit.main.util.PersistentScheduler;
import com.foxelbox.foxbukkit.main.util.Utils;
import com.foxelbox.foxbukkit.permissions.AbusePotentialManager;
import com.foxelbox.foxbukkit.permissions.FoxBukkitPermissionHandler;
import com.foxelbox.foxbukkit.permissions.FoxBukkitPermissions;
import com.foxelbox.foxbukkit.portal.PortalEngine;
import com.foxelbox.foxbukkit.spawning.SpawnUtils;
import com.foxelbox.foxbukkit.transmute.Transmute;
import com.foxelbox.foxbukkit.warp.WarpEngine;
import com.foxelbox.foxbukkit.warp.listeners.SignPortalPlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_8_R1.command.ColouredConsoleSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * FoxBukkit
 * @author Doridian
 */
public class FoxBukkit extends JavaPlugin {
	public static FoxBukkit instance;
	public ComponentSystem componentSystem = new ComponentSystem();

	public Transmute transmute;
	public PlayerHelper playerHelper;
	@SuppressWarnings("unused")
	public final MessageHelper messageHelper = new MessageHelper();
	public final Utils utils = new Utils(this);
	public final SpawnUtils spawnUtils = new SpawnUtils(this);
	public WarpEngine warpEngine;
	public PortalEngine portalEngine;
	public PersistentScheduler persistentScheduler;

	public Bans bans;
	public WorldEditPlugin worldEdit;
	public LogBlock logBlock;
	public Consumer logBlockConsumer;

	public CommandSystem commandSystem;
	public final FBBank bank = new FBBank();

	public Configuration configuration;

	public RedisManager redisManager;

	public FoxBukkit() {
		instance = this;
		componentSystem.registerComponents();
	}

	public void onDisable() {
		log("Plugin disabled!" ) ;
	}

	public void setupIPC() {
		final PluginManager pm = getServer().getPluginManager();

		worldEdit = (WorldEditPlugin) pm.getPlugin("WorldEdit");
		if (worldEdit != null)
			log( "Found WorldEdit!" );

		logBlock = (LogBlock) pm.getPlugin("LogBlock");
		if (logBlock != null) {
			logBlockConsumer = logBlock.getConsumer();
			log( "Found LogBlock!" );
		}
	}

	public void onEnable() {
		getDataFolder().mkdirs();

		configuration = new Configuration(getDataFolder());

		setupIPC();

		redisManager = new RedisManager(new SimpleThreadCreator(), configuration);

		playerHelper = new PlayerHelper(this);

		FoxBukkitPermissionHandler.instance.load();

		warpEngine = new WarpEngine(this);
		persistentScheduler = new PersistentScheduler();
		//portalEngine = new PortalEngine(this);
		new AbusePotentialManager();
		log("State components loaded.");
		StateContainer.loadAll();
		log("State component config loaded.");

		commandSystem = new CommandSystem(this);
		componentSystem.registerCommands();
		new AutoCleanup();
		new FoxBukkitPlayerListener();
		new FoxBukkitBlockListener();
		new FoxBukkitEntityListener();
		new SignPortalPlayerListener();
		transmute = new Transmute(this);
		new ChatListener();
		new FoxBukkitConsoleCommands(this);
		componentSystem.registerListeners();
		new FoxBukkitBungeeLink();

		log("Core components loaded.");
		bans = new Bans(this);
		log("Bans loaded.");

		log( "Plugin enabled!" );

		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		FoxBukkitPermissions.init();

		playerHelper.refreshPlayerListRedis(null);
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
			msg = "\u00a7d[FB]\u00a7f " + msg;
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
