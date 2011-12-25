package de.doridian.yiffbukkit.transmute;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import org.bukkit.entity.Player;

public interface MobAction {
	abstract public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException;
}
