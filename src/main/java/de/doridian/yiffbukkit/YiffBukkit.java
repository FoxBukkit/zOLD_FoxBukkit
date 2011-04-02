package de.doridian.yiffbukkit;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import de.doridian.yiffbukkit.advertisement.AdvertismentSigns;
import de.doridian.yiffbukkit.commands.ICommand;
import de.doridian.yiffbukkit.jail.JailEngine;
import de.doridian.yiffbukkit.portals.PortalEngine;
import de.doridian.yiffbukkit.remote.YiffBukkitRemote;
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
	private YiffBukkitEntityListener yiffBukkitEntityListener;
	@SuppressWarnings("unused")
	private YiffBukkitPacketListener yiffBukkitPacketListener;
	@SuppressWarnings("unused")
	private VanishPacketListener vanishPacketListener;
	private YiffBukkitRemote remote;
	public PlayerHelper playerHelper = null;
	public final Utils utils;
	public Permissions permissions;
	public WorldEditPlugin worldEdit;
	public AdvertismentSigns adHandler;
	public WarpEngine warpEngine;
	public JailEngine jailEngine;
	public PortalEngine portalEngine;

	public YiffBukkit() {
		utils = new Utils(this);
	}

	public void onDisable() {
		remote.stopme();
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
		loadMCServer();

		playerHelper = new PlayerHelper(this);
		warpEngine = new WarpEngine(this);
		jailEngine = new JailEngine(this);
		//portalEngine = new PortalEngine(this);
		playerListener = new YiffBukkitPlayerListener(this);
		blockListener = new YiffBukkitBlockListener(this);
		yiffBukkitPacketListener = new YiffBukkitPacketListener(this);
		yiffBukkitEntityListener = new YiffBukkitEntityListener(this);
		vanishPacketListener = new VanishPacketListener(this);
		adHandler = new AdvertismentSigns(this);

		remote = new YiffBukkitRemote(this, playerListener);
		remote.start();

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

	private static MinecraftServer mcServer;
	private static boolean mcServerHack = false;
	//this will do a hack to get MinecraftServer


	private void loadMCServer() {
		Server server = getServer();
		if (server instanceof CraftServer) {
			CraftServer s = (CraftServer) server;
			Field f;
			try {
				f = CraftServer.class.getDeclaredField("console");
				f.setAccessible(true);
				mcServer = (MinecraftServer) f.get(s);
				mcServerHack = true;
			} catch (Exception e) {
				Logger.getLogger("Minecraft").log(Level.SEVERE, null, e);
			}
		}
	}

	public void stopServer(){
		mcServer.a();
	}
	public void sendServerCmd(String cmd, CommandSender sender){
		if (mcServerHack && !mcServer.g && MinecraftServer.a(mcServer)) {
			mcServer.a(cmd, mcServer);
		} else {
			if (sender != null) {
				sender.sendMessage(ChatColor.RED + "Can't send console command!");
			} else {
				Logger.getLogger("Minecraft").log(Level.WARNING, "Can't send console command!");
			}
		}
	}
}