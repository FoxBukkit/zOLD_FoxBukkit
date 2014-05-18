package de.doridian.yiffbukkit.permissions;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.main.StateContainer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AbusePotentialManager extends StateContainer {
	private static final Set<UUID> abusiveAdmins = new HashSet<>();
	private static Set<String> abusivePermissions = new HashSet<>(Arrays.asList(
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
			BufferedReader reader = new BufferedReader(YiffBukkit.instance.configuration.makeReader("abusiveadmins.txt"));
			String line;

			while ((line = reader.readLine()) != null) {
				abusiveAdmins.add(UUID.fromString(line.trim()));
			}

			reader.close();
		}
		catch (FileNotFoundException e) { }
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isAbusive(UUID uuid) {
		return abusiveAdmins.contains(uuid);
	}

	public static boolean isBlocked(UUID uuid, String permission) {
		if (!isAbusive(uuid))
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
