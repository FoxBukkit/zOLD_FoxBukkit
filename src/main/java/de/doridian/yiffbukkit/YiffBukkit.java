package de.doridian.yiffbukkit;

import java.util.Hashtable;
import java.util.List;

import net.minecraft.server.NetServerHandler;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.nijikokun.bukkit.Permissions.Permissions;

import de.doridian.yiffbukkit.commands.ICommand;
import de.doridian.yiffbukkit.warp.WarpEngine;

/**
 * YiffBukkit
 * @author Doridian
 */
public class YiffBukkit extends JavaPlugin {
	private YiffBukkitPlayerListener playerListener;
	private YiffBukkitBlockListener blockListener;
	public PlayerHelper playerHelper = null;
	public final Utils utils;
	public Permissions permissions;
	public AdvertismentSigns adHandler;
	public WarpEngine warpEngine;

	public YiffBukkit() {
		super();
		utils = new Utils(this);
	}

	public void onDisable() {
		System.out.println( "YiffBukkit is disabled!" );
	}

	public void setupPermissions() {
		permissions = (Permissions)getServer().getPluginManager().getPlugin("Permissions");
		System.out.println( "Permissions loaded!" );
	}

	public void onEnable() {
		setupPermissions();
		playerHelper = new PlayerHelper(this);
		warpEngine = new WarpEngine(this);

		PluginManager pm = getServer().getPluginManager();
		playerListener = new YiffBukkitPlayerListener(this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);

		blockListener = new YiffBukkitBlockListener(this);
		pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener, Priority.Normal, this);
		//pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);

		adHandler = new AdvertismentSigns(this);

		//NetLoginHandler.offlineLoginVerifier = new DoriLoginVerifier();
		NetServerHandler.addPacketListener(true, 4, new YiffBukkitPacketListener(this));
		
		VanishPacketListener vanishPacketListener = new VanishPacketListener(this);
		NetServerHandler.addPacketListener(true, 18, vanishPacketListener);
		NetServerHandler.addPacketListener(true, 20, vanishPacketListener);
		NetServerHandler.addPacketListener(true, 30, vanishPacketListener);
		NetServerHandler.addPacketListener(true, 31, vanishPacketListener);
		NetServerHandler.addPacketListener(true, 32, vanishPacketListener);
		NetServerHandler.addPacketListener(true, 33, vanishPacketListener);
		NetServerHandler.addPacketListener(true, 34, vanishPacketListener);

		System.out.println( "YiffBukkit is enabled!" );
	}

	public Hashtable<String,ICommand> GetCommands() {
		return playerListener.commands;
	}

	public Location TogglePlayerWorlds(Player ply, Location pos) {
		World world = ToggleWorlds(ply.getWorld());
		pos.setWorld(world);
		pos.setY(world.getHighestBlockYAt(pos.getBlockX(), pos.getBlockZ()));
		return pos;
	}

	public World ToggleWorlds(World world) {
		if(world.getEnvironment() == Environment.NORMAL)
			return GetOrCreateWorld(world.getName() + "_nether", Environment.NETHER);
		else
			return GetOrCreateWorld(world.getName().substring(0, world.getName().length() - 7), Environment.NORMAL);
	}

	public World GetOrCreateWorld(String name, Environment env) {
		name = name.toLowerCase();
		List<World> worlds = getServer().getWorlds();
		for(World world : worlds) {
			if(world.getName().equals(name)) return world;
		}
		World world = getServer().createWorld(name, env);
		return world;
	}
}