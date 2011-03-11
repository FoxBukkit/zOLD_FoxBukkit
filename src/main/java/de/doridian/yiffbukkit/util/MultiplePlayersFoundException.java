package de.doridian.yiffbukkit.util;

public class MultiplePlayersFoundException extends PlayerFindException {
	private static final long serialVersionUID = 1L;

	public MultiplePlayersFoundException() {
		super("Sorry, multiple players found!");
	}

	public MultiplePlayersFoundException(Throwable cause) {
		super("Sorry, multiple players found!", cause);
	}
}
