package de.doridian.yiffbukkit;

import java.util.Hashtable;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import de.doridian.yiffbukkit.advertisement.AdvertismentSigns;
import de.doridian.yiffbukkit.commands.ICommand;
import de.doridian.yiffbukkit.jail.JailEngine;
import de.doridian.yiffbukkit.util.PlayerHelper;
import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.warp.WarpEngine;

/**
 * YiffBukkit
 * @author Doridian
 */
public class YiffBukkit extends JavaPlugin {
	private YiffBukkitPlayerListener playerListener;
	@SuppressWarnings("unused")
	private YiffBukkitBlockListener blockListener;
	@SuppressWarnings("unused")
	private YiffBukkitPacketListener yiffBukkitPacketListener;
	@SuppressWarnings("unused")
	private VanishPacketListener vanishPacketListener;
	public PlayerHelper playerHelper = null;
	public final Utils utils;
	public Permissions permissions;
	public WorldEditPlugin worldEdit;
	public AdvertismentSigns adHandler;
	public WarpEngine warpEngine;
	public JailEngine jailEngine;

	public YiffBukkit() {
		utils = new Utils(this);
	}

	public void onDisable() {
		System.out.println( "YiffBukkit is disabled!" );
	}

	public void setupIPC() {
		permissions = (Permissions)getServer().getPluginManager().getPlugin("Permissions");
		System.out.println( "YiffBukkit found Permissions!" );

		worldEdit = (WorldEditPlugin)getServer().getPluginManager().getPlugin("WorldEdit");
		System.out.println( "YiffBukkit found WorldEdit!" );
	}

	public void onEnable() {
		setupIPC();
		playerHelper = new PlayerHelper(this);
		warpEngine = new WarpEngine(this);
		jailEngine = new JailEngine(this);
		playerListener = new YiffBukkitPlayerListener(this);
		blockListener = new YiffBukkitBlockListener(this);
		yiffBukkitPacketListener = new YiffBukkitPacketListener(this);
		vanishPacketListener = new VanishPacketListener(this);
		adHandler = new AdvertismentSigns(this);

		System.out.println( "YiffBukkit is enabled!" );
	}

	public Hashtable<String,ICommand> GetCommands() {
		return playerListener.commands;
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