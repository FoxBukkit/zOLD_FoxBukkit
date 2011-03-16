package de.doridian.yiffbukkit.warp;

import de.doridian.yiffbukkit.YiffBukkitCommandException;

public class WarpException extends YiffBukkitCommandException {
	private static final long serialVersionUID = 1L;

	public WarpException(String message) {
		super(message);
	}

	public WarpException(Throwable cause) {
		super(cause);
	}

	public WarpException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public WarpException setColor(char color) {
		super.setColor(color);
		return this;
	}
}
