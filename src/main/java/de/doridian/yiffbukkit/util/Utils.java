package de.doridian.yiffbukkit.util;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.World.Environment;

import de.doridian.yiffbukkit.YiffBukkit;

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
		return new Location(plugin.GetOrCreateWorld(split[5], Environment.valueOf(split[6])), Double.valueOf(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]), Float.valueOf(split[3]), Float.valueOf(split[4]));
	}

	@SuppressWarnings("unchecked")
	public static <T, E> T getPrivateValue(Class<? super E> class1, E instance, String field) {
		try
		{
			Field f = class1.getDeclaredField(field);
			f.setAccessible(true);
			return (T) f.get(instance);
		} catch (Exception e) {

		}
		return null;
	}

	public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, String field, E value) {
		try
		{
			Field field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);


			Field f = instanceclass.getDeclaredField(field);
			int modifiers = field_modifiers.getInt(f);
			if ((modifiers & 0x10) != 0)
				field_modifiers.setInt(f, modifiers & 0xFFFFFFEF);
			f.setAccessible(true);
			f.set(instance, value);
		} catch (Exception e) { }
	}
}
