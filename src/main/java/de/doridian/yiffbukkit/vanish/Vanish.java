package de.doridian.yiffbukkit.vanish;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissionHandler;

public class Vanish {
	YiffBukkit plugin;
	private YiffBukkitPermissionHandler permissionHandler;
	@SuppressWarnings("unused")
	private final VanishPacketListener vanishPacketListener;
	@SuppressWarnings("unused")
	private final VanishPlayerListener vanishPlayerListener;

	public Set<String> vanishedPlayers = new HashSet<String>();

	public Vanish(YiffBukkit plugin) {
		this.plugin = plugin;
		permissionHandler = plugin.permissionHandler;
		vanishPacketListener = new VanishPacketListener(this);
		vanishPlayerListener = new VanishPlayerListener(this);
	}

	boolean canSeeEveryone(Player ply) {
		return permissionHandler.has(ply, "yiffbukkit.vanish.see");
	}
}
