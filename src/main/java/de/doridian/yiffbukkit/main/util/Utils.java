package de.doridian.yiffbukkit.main.util;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.server.v1_7_R1.Block;
import net.minecraft.server.v1_7_R1.DataWatcher;
import net.minecraft.server.v1_7_R1.EntityBat;
import net.minecraft.server.v1_7_R1.EntityFallingBlock;
import net.minecraft.server.v1_7_R1.Item;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_7_R1.WatchableObject;
import net.minecraft.server.v1_7_R1.Vec3D;
import net.minecraft.server.v1_7_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("UnusedDeclaration")
public class Utils {
	private YiffBukkit plugin;
	public Utils(YiffBukkit iface) {
		plugin = iface;
	}

	public static String concat(Collection<String> parts, int start, String defaultText) {
		// TODO: optimize
		return concatArray(parts.toArray(new String[parts.size()]), start, defaultText);
	}

	@SuppressWarnings("deprecation")
	public static String URLEncode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return URLEncoder.encode(str);
		}
	}

	public static String concatArray(String[] array, int start, String defaultText) {
		if (array.length <= start)
			return defaultText;

		if (array.length <= start + 1)
			return array[start]; // optimization

		StringBuilder ret = new StringBuilder(array[start]);
		for(int i = start + 1; i < array.length; i++) {
			ret.append(' ');
			ret.append(array[i]);
		}
		return ret.toString();
	}

	public static String serializeLocation(Location loc) {
		return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch() + ";" + loc.getWorld().getName() + ";" + loc.getWorld().getEnvironment().name();
	}

	public static List<Player> matchPlayer(String subString) {
		final List<Player> players = Bukkit.matchPlayer(subString);
		final String lowerCase = subString.toLowerCase();
		for (Player ply : Bukkit.getOnlinePlayers()) {
			if (players.contains(ply))
				continue;

			if (!ChatColor.stripColor(ply.getDisplayName().toLowerCase()).contains(lowerCase))
				continue;

			players.add(ply);
		}
		return players;
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

		final double cosYaw = Math.cos(yaw);
		final double sinYaw = Math.sin(yaw);
		final double cosPitch = Math.cos(pitch);
		final double sinPitch = Math.sin(pitch);

		final Vector left = new Vector(
				cosYaw,
				0,
				sinYaw
		);

		final Vector up = new Vector(
				-sinYaw*sinPitch,
				cosPitch,
				cosYaw*sinPitch
		);

		final Vector forward = new Vector(
				-sinYaw*cosPitch,
				-sinPitch,
				cosYaw*cosPitch
		);

		return left.multiply(axis.getX()).add(up.multiply(axis.getY())).add(forward.multiply(axis.getZ()));
	}

	public static Vector toLocalAxis(Location location, Vector axis) {
		final double yaw = Math.toRadians(location.getYaw());
		final double pitch = Math.toRadians(location.getPitch());

		final double cosYaw = Math.cos(yaw);
		final double sinYaw = Math.sin(yaw);
		final double cosPitch = Math.cos(pitch);
		final double sinPitch = Math.sin(pitch);

		final Vector xAxis = new Vector(
				cosYaw,
				-sinYaw*sinPitch,
				-sinYaw*cosPitch
		);

		final Vector yAxis = new Vector(
				0,
				cosPitch,
				-sinPitch
		);

		final Vector zAxis = new Vector(
				sinYaw,
				cosYaw*sinPitch,
				cosYaw*cosPitch
		);

		return xAxis.multiply(axis.getX()).add(yAxis.multiply(axis.getY())).add(zAxis.multiply(axis.getZ()));
	}

	public static Vector toWorld(Location location, Vector position) {
		return toWorldAxis(location, position).add(location.toVector());
	}

	public static Vector toLocal(Location location, Vector position) {
		return toLocalAxis(location, position.clone().subtract(location.toVector()));
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
	 * Generates a uniformly distributed, random, normalized direction vector.
	 *
	 * @return the vector
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

	public static void makeSound(Location location, String soundName, float volume, float pitch, Player player) {
		if (location == null || soundName == null) return;

		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();

		PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(soundName, x, y, z, volume, pitch);
		PlayerHelper.sendPacketToPlayer(player, packet);
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

	public static <T> List<Class<? extends T>> getSubClasses(Class<T> baseClass, String packageName) {
		final List<Class<? extends T>> ret = new ArrayList<>();
		final File file;
		try {
			final ProtectionDomain protectionDomain = baseClass.getProtectionDomain();
			final CodeSource codeSource = protectionDomain.getCodeSource();
			if (codeSource == null)
				return ret;

			final URL location = codeSource.getLocation();
			final URI uri = location.toURI();
			file = new File(uri);
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
			return ret;
		}
		final String[] fileList;

		if (file.isDirectory() || (file.isFile() && !file.getName().endsWith(".jar"))) {
			String packageFolderName = "/"+packageName.replace('.','/');

			URL url = baseClass.getResource(packageFolderName);
			if (url == null)
				return ret;

			File directory = new File(url.getFile());
			if (!directory.exists())
				return ret;

			// Get the list of the files contained in the package
			fileList = directory.list();
		}
		else if (file.isFile()) {
			final List<String> tmp = new ArrayList<>();
			final JarFile jarFile;
			try {
				jarFile = new JarFile(file);
			}
			catch (IOException e) {
				e.printStackTrace();
				return ret;
			}

			Pattern pathPattern = Pattern.compile(packageName.replace('.','/')+"/(.+\\.class)");
			final Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				Matcher matcher = pathPattern.matcher(entries.nextElement().getName());
				if (!matcher.matches())
					continue;

				tmp.add(matcher.group(1));
			}

			fileList = tmp.toArray(new String[tmp.size()]);
		}
		else {
			return ret;
		}

		Pattern classFilePattern = Pattern.compile("(.+)\\.class");
		for (String fileName : fileList) {
			// we are only interested in .class files
			Matcher matcher = classFilePattern.matcher(fileName);
			if (!matcher.matches())
				continue;

			// removes the .class extension
			String classname = matcher.group(1);
			try {
				final String qualifiedName = packageName+"."+classname.replace('/', '.');
				final Class<?> classObject = Class.forName(qualifiedName);
				final Class<? extends T> classT = classObject.asSubclass(baseClass);

				// Try to create an instance of the object
				ret.add(classT);
			}
			catch (ClassCastException e) {
				//noinspection UnnecessaryContinue
				continue;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public static List<Player> getObservingPlayers(Player target) {
		final List<Player> players = new ArrayList<>();

		for (Player player : target.getWorld().getPlayers()) {
			if (!player.canSee(target))
				continue;

			players.add(player);
		}

		return players;
	}

	public static String firstLetterToUppercase(String string) {
		return Character.toUpperCase(string.charAt(0))+string.substring(1);
	}

	public static int countSpaces(String phrase) {
		int spaces = 0;
		int pos = 0;
		while ((pos = phrase.indexOf(' ', pos)) != -1 ) {
			phrase = phrase.substring(pos+1);
			++spaces;
		}
		return spaces;
	}

	/**
	 * Enumerates strings in natural english.
	 *
	 * @param strings The strings to enumerate.
	 * @return the enumerated strings.
	 */
	public static StringBuilder enumerateStrings(final List<String> strings) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strings.size(); ++i) {
			final String distance = strings.get(i);
			//noinspection StatementWithEmptyBody
			if (i == 0) {
				// Do nothing
			}
			else if (i == strings.size()-1) {
				sb.append(" and ");
			}
			else {
				sb.append(", ");
			}
			sb.append(distance);
		}
		return sb;
	}

	public static void dumpDataWatcher(DataWatcher datawatcher) {
		for (Object o : datawatcher.b()) { // v1_7_R1
			WatchableObject wo = (WatchableObject) o;
			System.out.println(wo.a()); // v1_7_R1
			System.out.println(wo.b()); // v1_7_R1
		}
	}

	public static Location toLocation(Vec3D pos, World world) {
		return new Location(world, pos.c, pos.d, pos.e); // v1_7_R1
	}

	public static Vector randomCone(Location baseLocation, double maxAngle) {
		maxAngle = Math.toRadians(maxAngle);

		final double minZ = Math.cos(maxAngle);
		final double z = minZ + Math.random() * (1 - minZ);
		final double radius = Math.sqrt(1 - z * z);
		final double angle = 2 * Math.PI * Math.random();
		final Vector axis = new Vector(z, Math.sin(angle) * radius, Math.cos(angle) * radius);

		return toWorldAxis(baseLocation, axis);
	}

	public static net.minecraft.server.v1_7_R1.Entity getEntityByID(int entityId, World world) {
		return ((CraftWorld)world).getHandle().getEntity(entityId);
	}

	public static EntityFallingBlock spawnFallingBlock(Location location, int typeId, int dataValue) {
		WorldServer notchWorld = ((CraftWorld) location.getWorld()).getHandle();
		final EntityFallingBlock notchFallingBlock = new EntityFallingBlock(notchWorld, location.getX(), location.getY(), location.getZ(), getBlockById(typeId), dataValue);

		// This disables the first tick code, which takes care of removing the original block etc.
		notchFallingBlock.b = 1; // v1_7_R1

		// Do not drop an item if placing a block fails
		notchFallingBlock.dropItem = false;

		notchWorld.addEntity(notchFallingBlock);

		return notchFallingBlock;
	}

	private static Block getBlockByMaterial(Material material) {
		return CraftMagicNumbers.getBlock(material);
	}

	public static Item getItemByMaterial(Material material) {
		return CraftMagicNumbers.getItem(material);
	}

	public static Block getBlockById(int typeId) {
		return getBlockByMaterial(Material.getMaterial(typeId));
		//return (Block) Block.REGISTRY.a(typeId); // v1_7_R1
	}

	public static Item getItemById(int typeId) {
		return getItemByMaterial(Material.getMaterial(typeId));
	}

	public static DataWatcher createEmptyDataWatcher() {
		return new DataWatcher(new EntityBat(null));
	}

	private static final TObjectIntMap<String> nbtMappings = new TObjectIntHashMap<>();
	static {
		nbtMappings.put("ActiveEffects", 10);
		nbtMappings.put("AttributeModifiers", 10);
		nbtMappings.put("Attributes", 10);
		nbtMappings.put("Children", 10);
		nbtMappings.put("CustomPotionEffects", 10);
		nbtMappings.put("Doors", 10);
		nbtMappings.put("DropChances", 5);
		nbtMappings.put("EnderItems", 10);
		nbtMappings.put("Entities", 10);
		nbtMappings.put("Entrances", 11);
		nbtMappings.put("Equipment", 10);
		nbtMappings.put("Explosions", 10);
		nbtMappings.put("Inventory", 10);
		nbtMappings.put("Items", 10);
		nbtMappings.put("Lore", 8);
		nbtMappings.put("Modifiers", 10);
		nbtMappings.put("Motion", 6);
		nbtMappings.put("Objectives", 10);
		nbtMappings.put("PlayerScores", 10);
		//nbtMappings.put("Players", 10);
		//nbtMappings.put("Players", 8);
		nbtMappings.put("Pos", 6);
		nbtMappings.put("Recipes", 10);
		nbtMappings.put("Rotation", 5);
		nbtMappings.put("Sections", 10);
		nbtMappings.put("SpawnPotentials", 10);
		nbtMappings.put("Teams", 10);
		nbtMappings.put("TileEntities", 10);
		nbtMappings.put("TileTicks", 10);
		nbtMappings.put("Villages", 10);
		nbtMappings.put("direction", 6);
		nbtMappings.put("ench", 10);
		nbtMappings.put("pages", 8);
		nbtMappings.put("servers", 10);
	}

	public static int mapNBT(String tagName) {
		if (!nbtMappings.containsKey(tagName))
			throw new RuntimeException("unmapped tag name encountered.");

		return nbtMappings.get(tagName);
	}
}
