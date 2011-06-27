package com.nijikokun.bukkit.Permissions;

import java.io.File;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissionHandler;

public class Permissions extends JavaPlugin {

	public static final PermissionHandler Security = new YiffBukkitPermissionHandler();
	
	public Permissions(YiffBukkit mainPlug, ClassLoader classLoad, File pluginFile) {
		super();
		this.initialize(mainPlug.getPluginLoader(), mainPlug.getServer(), new PluginDescriptionFile("Permissions", "1.3.3.7", "com.nijikokun.bukkit.Permissions.Permissions"), mainPlug.getDataFolder(), pluginFile, classLoad);
		Security.load();
	}
	
	@Override
	public void onDisable() {
		
	}

	@Override
	public void onEnable() {
		
	}
	
	public PermissionHandler getHandler() {
		return Security;
	}
}
