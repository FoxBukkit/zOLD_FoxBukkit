package de.doridian.yiffbukkit;

import org.bukkit.entity.Player;

public abstract class ToolBind implements Runnable {
	public final String playerName;
	public final String name;
	public Player player;

	public ToolBind(String name, Player ply) {
		this.name = name;
		player = ply;
		playerName = ply.getName();
	}
}
