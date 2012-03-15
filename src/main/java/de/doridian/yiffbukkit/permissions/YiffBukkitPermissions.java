package de.doridian.yiffbukkit.permissions;

import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.Bukkit;

public class YiffBukkitPermissions {
	public static void init() {
		PermissionPlayerListener listener = new PermissionPlayerListener();
		Bukkit.getPluginManager().registerEvents(listener, YiffBukkit.instance);
	}
}
