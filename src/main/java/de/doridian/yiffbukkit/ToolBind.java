package de.doridian.yiffbukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemEvent;

public abstract class ToolBind {
	public final String playerName;
	public final String name;

	public ToolBind(String name, Player ply) {
		this.name = name;
		playerName = ply.getName();
	}
	
	public abstract void run(PlayerItemEvent event);
}
