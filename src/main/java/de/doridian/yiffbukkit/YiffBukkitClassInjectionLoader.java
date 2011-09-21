package de.doridian.yiffbukkit;

import java.net.URL;
import java.net.URLClassLoader;

public class YiffBukkitClassInjectionLoader extends URLClassLoader {
	public YiffBukkitClassInjectionLoader(YiffBukkit plugin) {
		super(new URL[] {YiffBukkitClassInjectionLoader.class.getProtectionDomain().getCodeSource().getLocation()});
		try {
			loadClass("org.bukkit.craftbukkit.entity.CraftPlayer");
		} catch (ClassNotFoundException e) { }
	}
}
