package de.doridian.yiffbukkit.vanish;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.craftbukkit.entity.CraftPlayer;
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

	Set<String> vanishedPlayers = new HashSet<String>();
	Set<Integer> vanishedEntityIds = new HashSet<Integer>();

	public Vanish(YiffBukkit plugin) {
		this.plugin = plugin;
		permissionHandler = plugin.permissionHandler;
		vanishPacketListener = new VanishPacketListener(this);
		vanishPlayerListener = new VanishPlayerListener(this);
	}

	boolean canSeeEveryone(Player ply) {
		return permissionHandler.has(ply, "yiffbukkit.vanish.see");
	}

	public boolean isVanished(Player ply) {
		return vanishedPlayers.contains(ply.getName());
	}

	public boolean isVanished(String playerName) {
		return vanishedPlayers.contains(playerName);
	}

	public void vanish(Player ply) {
		plugin.playerHelper.sendPacketToPlayersAround(ply.getLocation(), 1024, new Packet29DestroyEntity(ply.getEntityId()), ply, 3);
		vanishedPlayers.add(ply.getName());
		vanishedEntityIds.add(ply.getEntityId());
	}

	public void unVanish(Player ply) {
		vanishedPlayers.remove(ply.getName());
		vanishedEntityIds.remove(ply.getEntityId());
		plugin.playerHelper.sendPacketToPlayersAround(ply.getLocation(), 1024, new Packet29DestroyEntity(ply.getEntityId()), ply, 3);
		plugin.playerHelper.sendPacketToPlayersAround(ply.getLocation(), 1024, new Packet20NamedEntitySpawn(((CraftPlayer)ply).getHandle()), ply, 3);
	}

	public void vanishId(int entityId) {
		vanishedEntityIds.add(entityId);
	}

	public void unVanishId(int entityId) {
		vanishedEntityIds.remove(entityId);
	}
}
