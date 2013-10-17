package de.doridian.yiffbukkit.transmute;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import org.bukkit.command.CommandSender;

public interface ShapeAction {
	abstract public void run(EntityShape shape, CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException;
}
