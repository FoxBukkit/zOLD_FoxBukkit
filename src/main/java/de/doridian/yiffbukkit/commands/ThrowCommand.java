package de.doridian.yiffbukkit.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;

public class ThrowCommand extends ICommand {
	public ThrowCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public int GetMinLevel() {
		return 4;
	}

	@Override
	public void Run(final Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		double speed = 2;

		CreatureType type;
		try {
			type = CreatureType.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new YiffBukkitCommandException("Creature type not found", e);
		}

		if (args.length >= 2) {
			try {
				speed = Double.valueOf(args[1]);
			} catch (NumberFormatException e) {
				throw new YiffBukkitCommandException("Number expected", e);
			}
		}

		Material toolType = ply.getItemInHand().getType();

		final double finalSpeed = speed;
		final CreatureType finalType = type;
		playerHelper.addToolMapping(ply, toolType, new Runnable() {
			public void run() {
				Location location = ply.getEyeLocation();
				Creature creature = ply.getWorld().spawnCreature(location, finalType);
				if (creature == null)
					playerHelper.SendDirectedMessage(ply, "Failed to spawn creature");

				creature.setVelocity(location.getDirection().multiply(finalSpeed));
			}
		});

		playerHelper.SendDirectedMessage(ply, "Bound "+type.getName()+" to your current tool ("+toolType.name()+"). Right-click to use.");
	}

	@Override
	public String GetHelp() {
		return "Binds creature spawning to your current tool. Right-click to use.";
	}

	@Override
	public String GetUsage() {
		return "<type> [speed]";
	}
}
