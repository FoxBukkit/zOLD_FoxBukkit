package de.doridian.yiffbukkit.yiffpoints;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;

public class ItemHasNoPriceException extends YiffBukkitCommandException {
	private static final long serialVersionUID = 1L;

	public ItemHasNoPriceException() {
		super("This item does not have a price.");
	}

	public ItemHasNoPriceException(Throwable cause) {
		super("This item does not have a price.", cause);
	}
}
