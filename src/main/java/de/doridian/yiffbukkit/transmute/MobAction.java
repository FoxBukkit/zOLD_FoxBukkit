package de.doridian.yiffbukkit.transmute;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;

public interface MobAction {
	abstract public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException;
}
