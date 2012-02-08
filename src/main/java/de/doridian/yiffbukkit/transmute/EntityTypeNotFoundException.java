package de.doridian.yiffbukkit.transmute;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;

public class EntityTypeNotFoundException extends YiffBukkitCommandException {
	private static final long serialVersionUID = 1L;

	public EntityTypeNotFoundException() {
		super("Entity type not found.");
	}

	public EntityTypeNotFoundException(Throwable cause) {
		super("Entity type not found.", cause);
	}
}
