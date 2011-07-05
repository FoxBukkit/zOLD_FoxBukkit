package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.transmute.Shape;

@Names({"shapeaction", "sac"})
@Permission("yiffbukkit.transmute.shapeaction")
public class ShapeActionCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		final Shape shape = plugin.transmute.getShape(ply);
		if (shape == null)
			throw new YiffBukkitCommandException("You are not currently transmuted.");

		shape.runAction(argStr);
	}
}
