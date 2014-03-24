package de.doridian.yiffbukkit.spawning;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import org.bukkit.entity.Entity;

public interface Spawnable<V extends Entity> {
	V getEntity() throws YiffBukkitCommandException;
	net.minecraft.server.v1_7_R2.Entity getInternalEntity() throws YiffBukkitCommandException;
}
