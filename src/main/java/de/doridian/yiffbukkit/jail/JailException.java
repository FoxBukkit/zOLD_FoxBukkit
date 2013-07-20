package de.doridian.yiffbukkit.jail;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;

public class JailException extends YiffBukkitCommandException {
	private static final long serialVersionUID = 1L;

	public JailException(String message) {
		super(message);
	}

	public JailException(Throwable cause) {
		super(cause);
	}

	public JailException(String message, Throwable cause) {
		super(message, cause);
	}
}
