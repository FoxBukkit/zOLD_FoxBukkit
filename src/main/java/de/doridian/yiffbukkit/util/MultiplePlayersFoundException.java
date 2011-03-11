package de.doridian.yiffbukkit.util;

public class MultiplePlayersFoundException extends PlayerFindException {
	private static final long serialVersionUID = 1L;

	public MultiplePlayersFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public MultiplePlayersFoundException(String message) {
		super(message);
	}

	public MultiplePlayersFoundException(Throwable cause) {
		super(cause);
	}
}
