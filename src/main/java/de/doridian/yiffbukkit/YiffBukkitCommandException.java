package de.doridian.yiffbukkit;

public class YiffBukkitCommandException extends Exception {
	private static final long serialVersionUID = 1L;

	public YiffBukkitCommandException(String message) {
		super(message);
	}

	public YiffBukkitCommandException(Throwable cause) {
		super(cause);
	}

	public YiffBukkitCommandException(String message, Throwable cause) {
		super(message, cause);
	}
}
