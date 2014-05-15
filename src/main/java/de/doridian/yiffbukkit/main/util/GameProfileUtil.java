package de.doridian.yiffbukkit.main.util;

import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;

import java.util.Iterator;
import java.util.UUID;

public class GameProfileUtil {
	public static GameProfile getFilledGameProfile(UUID uuid, String name) {
		GameProfile gameprofile = MinecraftServer.getServer().getUserCache().a(uuid);

		if (gameprofile == null)
			gameprofile = new GameProfile(uuid, name);

		gameprofile = MinecraftServer.getServer().av().fillProfileProperties(gameprofile, true);

		Iterator iterator = gameprofile.getProperties().values().iterator();

		return gameprofile;
	}
}
