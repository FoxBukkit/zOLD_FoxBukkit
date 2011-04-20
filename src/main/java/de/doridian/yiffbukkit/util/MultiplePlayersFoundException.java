package de.doridian.yiffbukkit.util;

import java.util.List;

import org.bukkit.entity.Player;

public class MultiplePlayersFoundException extends PlayerFindException {
	private static final long serialVersionUID = 1L;
	private List<Player> players;

	public MultiplePlayersFoundException(List<Player> players) {
		super("Sorry, multiple players found!");
		this.players = players;
	}

	public List<Player> getPlayers() {
		return players;
	}
}
