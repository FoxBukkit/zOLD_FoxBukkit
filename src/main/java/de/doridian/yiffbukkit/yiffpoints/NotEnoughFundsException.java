package de.doridian.yiffbukkit.yiffpoints;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;

public class NotEnoughFundsException extends YiffBukkitCommandException {
	private static final long serialVersionUID = 1L;

	public NotEnoughFundsException(double fundsMissing) {
		super("Not enough YP. Need another "+fundsMissing+" YP.");
	}

	public NotEnoughFundsException(double fundsMissing, Throwable cause) {
		super("Not enough YP. Need another "+fundsMissing+" YP.", cause);
	}
}
