package de.doridian.yiffbukkit.main;

public class YiffBukkitCommandException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private char color = '5';

	public YiffBukkitCommandException(String message) {
		super(message);
	}

	public YiffBukkitCommandException(Throwable cause) {
		super(cause);
	}

	public YiffBukkitCommandException(String message, Throwable cause) {
		super(message, cause);
	}

	public YiffBukkitCommandException setColor(char color) {
		this.color = color;
		return this;
	}

	public char getColor() {
		return color;
	}
}
