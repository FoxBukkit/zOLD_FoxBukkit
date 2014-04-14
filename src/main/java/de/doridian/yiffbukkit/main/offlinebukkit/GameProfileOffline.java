package de.doridian.yiffbukkit.main.offlinebukkit;

import net.minecraft.util.com.mojang.authlib.GameProfile;

import java.util.UUID;

public class GameProfileOffline extends GameProfile {
	private final String name;
	private final UUID id;

	public GameProfileOffline(UUID id, String name) {
		super(id, name);
		this.id = id;
		this.name = name;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}
}
