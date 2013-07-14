package de.doridian.yiffbukkitsplit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.doridian.yiffbukkit.main.StateContainer;
import de.doridian.yiffbukkit.main.config.ConfigFileReader;

public class AbusePotentialManager extends StateContainer {
	private static final Set<String> abusiveAdmins = new HashSet<String>();
	private static Set<String> abusivePermissions = new HashSet<String>(Arrays.asList(
			"bukkit.commandblock.set",
			"worldedit.butcher",
			"worldedit.remove",
			"worldedit.limit",
			"yiffbukkit.teleport.tp.coords",
			"worldedit.regen"
	));

	private static String[] abusivePermissionPrefixes = new String[] {
			"worldguard.",
			"worldedit.clipboard.",
			"worldedit.schematic.",
			"worldedit.snapshots.",
	};

	@Loader("abuse")
	public static void load() {
		abusiveAdmins.clear();

		try {
			BufferedReader reader = new BufferedReader(new ConfigFileReader("abusiveadmins.txt"));
			String line;

			while ((line = reader.readLine()) != null) {
				abusiveAdmins.add(line.trim().toLowerCase());
			}

			reader.close();
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isAbusive(String name) {
		return abusiveAdmins.contains(name.toLowerCase());
	}

	public static boolean isBlocked(String name, String permission) {
		if (!isAbusive(name))
			return false;

		if (abusivePermissions.contains(permission))
			return true;

		for (String prefix: abusivePermissionPrefixes) {
			if (permission.startsWith(prefix))
				return true;
		}

		return false;
	}
}
