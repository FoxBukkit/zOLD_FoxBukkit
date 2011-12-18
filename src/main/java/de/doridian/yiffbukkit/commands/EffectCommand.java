package de.doridian.yiffbukkit.commands;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("effect")
@Help("Plays the specified effect.")
@Usage("<effect>")
@Permission("yiffbukkit.test.effect")
public class EffectCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		if (args.length < 1)
			throw new YiffBukkitCommandException("Not enough arguments.");

		Effect effect;
		try {
			effect = Effect.valueOf(args[0]);
		} catch (IllegalArgumentException  e1) {
			try {
				final int effectId = Integer.valueOf(args[0]);
				effect = null;
				for (Effect effect2 : Effect.values()) {
					if (effect2.getId() == effectId) {
						effect = effect2;
						break;
					}
				}
				if (effect == null)
					throw new YiffBukkitCommandException("Unknown effect id " + effectId);
			}
			catch (NumberFormatException e) {
				throw new YiffBukkitCommandException("Unknown effect " + args[0], e);
			}
		}

		final int data;
		if (args.length < 2) {
			data = 0;
		}
		else {
			try {
				data = Integer.valueOf(args[1]);
			}
			catch (NumberFormatException e) {
				throw new YiffBukkitCommandException("Unable to parse data value " + args[1], e);
			}
		}

		Location location = ply.getLocation();

		if (effect == Effect.STEP_SOUND && Material.getMaterial(data) == null) {
			throw new YiffBukkitCommandException("Invalid block ID passed for STEP_SOUND effect.");
		}

		ply.getWorld().playEffect(location, effect, data);
		playerHelper.sendDirectedMessage(ply, "location="+location+" effect="+effect+" data="+data);
	}
}
