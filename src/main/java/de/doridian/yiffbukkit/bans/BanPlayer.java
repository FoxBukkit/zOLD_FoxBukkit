package de.doridian.yiffbukkit.bans;

import java.util.UUID;

public class BanPlayer {
	public final UUID uuid;
	public final String name;

	BanPlayer(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}
}
