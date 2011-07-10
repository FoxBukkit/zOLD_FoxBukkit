package de.doridian.yiffbukkit.commands;

import org.bukkit.Effect;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.transmute.Transmute;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("transmute")
@Help("Transmutes you")
@Usage("[<shape>]")
@Permission("yiffbukkit.transmute")
public class TransmuteCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		final Transmute transmute = plugin.transmute;

		if (args.length == 0) {
			if (!transmute.isTransmuted(ply))
				throw new YiffBukkitCommandException("Not transmuted");

			transmute.resetShape(ply);

			playerHelper.sendDirectedMessage(ply, "Transmuted you back into your original shape.");
		}
		else {
			transmute.setShape(ply, args[0]);

			playerHelper.sendDirectedMessage(ply, "Transmuted you into "+args[0]+".");
		}
		ply.getWorld().playEffect(ply.getEyeLocation(), Effect.EXTINGUISH, 4);
		ply.getWorld().playEffect(ply.getEyeLocation(), Effect.SMOKE, 4);
		ply.getWorld().playEffect(ply.getEyeLocation(), Effect.SMOKE, 4);
		ply.getWorld().playEffect(ply.getEyeLocation(), Effect.SMOKE, 4);
	}
}
