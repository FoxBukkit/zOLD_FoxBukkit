package de.doridian.yiffbukkit.vanish;

import java.util.Set;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.PlayerHelper;

public class Vanish {
	YiffBukkit plugin;
	private final PlayerHelper playerHelper;
	@SuppressWarnings("unused")
	private final VanishPacketListener vanishPacketListener;
	@SuppressWarnings("unused")
	private final VanishPlayerListener vanishPlayerListener;

	Set<String> vanishedPlayers;

	public Vanish(YiffBukkit plugin) {
		this.plugin = plugin;
		playerHelper = plugin.playerHelper;
		vanishPacketListener = new VanishPacketListener(this);
		vanishPlayerListener = new VanishPlayerListener(this);
	}

	boolean canSeeEveryone(Player ply) {
		return playerHelper.getPlayerLevel(ply) >= 3;
	}
}
