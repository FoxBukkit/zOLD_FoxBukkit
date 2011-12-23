package de.doridian.yiffbukkit.transmute;

import de.doridian.yiffbukkit.YiffBukkitCommandException;

public interface MobAction {
	abstract public void run(EntityShape shape, String[] args, String argStr) throws YiffBukkitCommandException;
}
