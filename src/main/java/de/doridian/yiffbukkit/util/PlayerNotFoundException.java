package de.doridian.yiffbukkit.util;

public class PlayerNotFoundException extends PlayerFindException {
	private static final long serialVersionUID = 1L;

	public PlayerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlayerNotFoundException(String message) {
		super(message);
	}

	public PlayerNotFoundException(Throwable cause) {
		super(cause);
	}
}
