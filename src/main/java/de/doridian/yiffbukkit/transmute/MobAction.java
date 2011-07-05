package de.doridian.yiffbukkit.transmute;

import de.doridian.yiffbukkit.YiffBukkitCommandException;

public interface MobAction {
	abstract public void run(MobShape shape, String[] args, String argStr) throws YiffBukkitCommandException;
}
