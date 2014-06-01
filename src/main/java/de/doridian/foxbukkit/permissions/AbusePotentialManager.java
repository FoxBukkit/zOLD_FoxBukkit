/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.foxbukkit.permissions;

import de.doridian.foxbukkit.core.FoxBukkit;
import de.doridian.foxbukkit.main.StateContainer;

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
			"foxbukkit.teleport.tp.coords",
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
			BufferedReader reader = new BufferedReader(FoxBukkit.instance.configuration.makeReader("abusiveadmins.txt"));
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
