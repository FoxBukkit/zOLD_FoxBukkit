package de.doridian.yiffbukkit.transmute;

import de.doridian.yiffbukkitsplit.YiffBukkitCommandException;
import org.bukkit.entity.Player;

public interface ShapeAction {
	abstract public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException;
}
