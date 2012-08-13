package de.doridian.yiffbukkit.main.util;

import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.regex.Pattern;

public class Utils {
	private YiffBukkit plugin;
	public Utils(YiffBukkit iface) {
		plugin = iface;
	}

	public static String concatArray(String[] array, int start, String def) {
		if (array.length <= start) return def;
		if (array.length <= start + 1) return array[start];
		StringBuilder ret = new StringBuilder(array[start]);
		for(int i=start+1;i<array.length;i++) {
			ret.append(' ');
			ret.append(array[i]);
		}
		return ret.toString();
	}

	public static String serializeLocation(Location loc) {
		return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch() + ";" + loc.getWorld().getName() + ";" + loc.getWorld().getEnvironment().name();
	}

	public Location unserializeLocation(String str) {
		String[] split = str.split(";");
		return new Location(plugin.getOrCreateWorld(split[5], Environment.valueOf(split[6])), Double.valueOf(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]), Float.valueOf(split[3]), Float.valueOf(split[4]));
	}

	@SuppressWarnings("unchecked")
	public static <T, E> T getPrivateValue(Class<? super E> class1, E instance, String field) {
		try
		{
			Field f = class1.getDeclaredField(field);
			f.setAccessible(true);
			return (T) f.get(instance);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static <T, E> void setPrivateValue(Class<? super T> instanceclass, T instance, String field, E value) {
		try
		{
			Field field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);

			Field f = instanceclass.getDeclaredField(field);
			int modifiers = field_modifiers.getInt(f);

			if ((modifiers & Modifier.FINAL) != 0)
				field_modifiers.setInt(f, modifiers & ~Modifier.FINAL);

			f.setAccessible(true);
			f.set(instance, value);
		}
		catch (Exception e) {
			System.err.println("Could not set field \"" + field + "\" of class \"" + instanceclass.getCanonicalName() + "\" because \"" + e.getMessage() + "\"");
		}
	}

	static String[] directions = { "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW" };
	public static String yawToDirection(double yaw) {
		yaw = (yaw%360+630)%360;

		int intdeg = (int) Math.round(yaw / 22.5F);
		if (intdeg < 0) intdeg += 16;
		if (intdeg >= 16) intdeg -= 16;

		return directions[intdeg];
	}

	public static double vectorToYaw(Vector offset) {
		return Math.toDegrees(Math.atan2(-offset.getX(), offset.getZ()));
	}


	public static Vector toWorldAxis(Location location, Vector axis) {
		final double yaw = Math.toRadians(location.getYaw());
		final double pitch = Math.toRadians(location.getPitch());

		final double cos_y = Math.cos(yaw);
		final double sin_y = Math.sin(yaw);
		final double cos_p = Math.cos(pitch);
		final double sin_p = Math.sin(pitch);

		final Vector forward = new Vector(
				-sin_y*cos_p,
				-sin_p,
				cos_y*cos_p
		);
		final Vector up = new Vector(
				-sin_y*sin_p,
				cos_p,
				cos_y*sin_p
		);
		final Vector left = new Vector(
				cos_y,
				0,
				sin_y
		);

		return forward.multiply(axis.getX()).add(up.multiply(axis.getY())).add(left.multiply(axis.getZ()));
	}

	public static Vector toLocalAxis(Location location, Vector axis) {
		final double yaw = Math.toRadians(location.getYaw());
		final double pitch = Math.toRadians(location.getPitch());

		final double cos_y = Math.cos(yaw);
		final double sin_y = Math.sin(yaw);
		final double cos_p = Math.cos(pitch);
		final double sin_p = Math.sin(pitch);

		final Vector xAxis = new Vector(
				-sin_y*cos_p,
				-sin_y*sin_p,
				cos_y
		);

		final Vector yAxis = new Vector(
				-sin_p,
				cos_p,
				0
		);

		final Vector zAxis = new Vector(
				cos_y*cos_p,
				cos_y*sin_p,
				sin_y
		);

		return xAxis.multiply(axis.getX()).add(yAxis.multiply(axis.getY())).add(zAxis.multiply(axis.getZ()));
	}

	public static Vector toWorld(Location location, Vector position) {
		return toWorldAxis(location, position).add(location.toVector());
	}

	public static Vector toLocal(Location location, Vector position) {
		return toWorldAxis(location, position.clone().subtract(location.toVector()));
	}

	public static String readableDate(Date date) {
		if (date == null)
			return "never";

		long difference = (System.currentTimeMillis() - date.getTime()) / 1000;

		if (difference < 0)
			return date+" (in the future)";

		if (difference == 0)
			return date+" (right now)";

		final long seconds = difference % 60L;
		difference -= seconds;
		difference /= 60;
		final long minutes = difference % 60L;
		difference -= minutes;
		difference /= 60;
		final long hours = difference % 24L;
		difference -= hours;
		difference /= 24;
		final long days = difference %7L;
		difference -= days;
		difference /= 7;
		final long weeks = difference;

		String ago = "ago)";
		if (seconds > 0)
			ago = seconds+"s "+ago;
		if (minutes > 0)
			ago = minutes+"m "+ago;
		if (hours > 0)
			ago = hours+"h "+ago;
		if (days > 0)
			ago = days+"d "+ago;
		if (weeks > 0)
			ago = weeks+"w "+ago;

		return date+" ("+ago;
	}

	public static String getCaller(String excludePattern) {
		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
		if (stackTraceElement.getMethodName().matches(excludePattern)) {
			stackTraceElement = Thread.currentThread().getStackTrace()[4];
		}

		final int lineNumber = stackTraceElement.getLineNumber();
		final String fileName = stackTraceElement.getFileName();
		return "("+fileName+":"+lineNumber+")";
	}

	/**
	 * Returns a uniformly distributed, random, normalized direction vector.
	 *
	 * @return
	 */
	public static Vector randvec() {
		double s, x,y;

		/*
		 * This is a variant of the algorithm for computing a random point
		 * on the unit sphere; the algorithm is suggested in Knuth, v2,
		 * 3rd ed, p136; and attributed to Robert E Knop, CACM, 13 (1970),
		 * 326.
		 */
		// translated to lua and then to java from http://mhda.asiaa.sinica.edu.tw/mhda/apps/gsl-1.6/randist/sphere.c

		// Begin with the polar method for getting x,y inside a unit circle
		do {
			x = Math.random() * 2 - 1;
			y = Math.random() * 2 - 1;
			s = x*x + y*y;
		}
		while (s > 1.0);

		double a = 2 * Math.sqrt(1 - s); // factor to adjust x,y so that x^2+y^2 is equal to 1-z^2
		return new Vector(x*a, y*a, s * 2 - 1); // z uniformly distributed from -1 to 1
	}

	public static void makeSound(Location location, String soundName, float volume, float pitch) {
		((CraftWorld) location.getWorld()).getHandle().makeSound(location.getX(), location.getY(), location.getZ(), soundName, volume, pitch);
	}

	public static Pattern compileWildcard(String wildcard) {
		final StringBuilder pattern = new StringBuilder("^");

		boolean first = true;
		for (String part : wildcard.split("\\*")) {
			if (!first)
				pattern.append(".*");

			first = false;
			if (part.isEmpty())
				continue;

			pattern.append(Pattern.quote(part));
		}

		pattern.append("$");
		
		return Pattern.compile(pattern.toString());
	}

	public static double fuzzySignum(final double a) {
		if (a > 0.0001)
			return 1;

		if (a < -0.0001)
			return -1;

		return 0;
	}
}
