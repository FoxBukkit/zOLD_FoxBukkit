package com.bukkit.doridian.yiffbukkit;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.World.Environment;

public class Utils {
	private YiffBukkit plugin;
	public Utils(YiffBukkit iface) {
		plugin = iface;
	}
	
	public static String concatArray(String[] array, int start, String def) {
		if(array.length <= start) return def;
		if(array.length <= start + 1) return array[start];
		String ret = array[start];
		for(int i=start+1;i<array.length;i++) {
			ret += " " + array[i];
		}
		return ret;
	}
	
	public String SerializeLocation(Location loc) {
		return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch() + ";" + loc.getWorld().getName() + ";" + loc.getWorld().getEnvironment().name();
	}
	
	public Location UnserializeLocation(String str) {
		String[] split = str.split(";");
		return new Location(this.plugin.GetOrCreateWorld(split[5], Environment.valueOf(split[6])), Double.valueOf(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]), Float.valueOf(split[3]), Float.valueOf(split[4]));
	}

	@SuppressWarnings("unchecked")
	public static <T, E> T getPrivateValue(Class<? super E> class1, E permissions, String field)
	{
		try
		{
			Field f = class1.getDeclaredField(field);
			f.setAccessible(true);
			return (T) f.get(permissions);
		} catch (Exception e) {
	
		}
		return null;
	}
}
