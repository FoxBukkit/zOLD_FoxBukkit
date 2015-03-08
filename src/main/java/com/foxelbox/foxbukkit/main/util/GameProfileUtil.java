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
package com.foxelbox.foxbukkit.main.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R2.MinecraftServer;

import java.util.Iterator;
import java.util.UUID;

public class GameProfileUtil {
	public static GameProfile getFilledGameProfile(UUID uuid, String name) {
		GameProfile gameprofile = MinecraftServer.getServer().getUserCache().a(uuid);

		if (gameprofile == null)
			gameprofile = new GameProfile(uuid, name);

		gameprofile = MinecraftServer.getServer().aC().fillProfileProperties(gameprofile, true);

		Iterator iterator = gameprofile.getProperties().values().iterator();

		return gameprofile;
	}
}
