package de.doridian.yiffbukkit.commands;

import net.minecraft.server.EntityWolf;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Level;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Usage;


@Names("butcher")
@Help(
		"Kills living entities around yourself or a specified target.\n"+
		"The default radius is 20. To kill everything, use a radius\n"+
		"of -1. Players and tamed wolves are never butchered."
)
@Usage("[<target>] [<radius>]")
@Level(3)
public class ButcherCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		int radius;
		Player target;
		switch (args.length) {
		case 0:
			//butcher <name> - butcher everything
			radius = 20;
			target = asPlayer(commandSender);

			break;

		case 1:
			try {
				//butcher <radius> - butcher around yourself in the given radius
				radius = args[0].equalsIgnoreCase("all") ? -1 : Integer.parseInt(args[0]);
				target = asPlayer(commandSender);
			}
			catch (NumberFormatException e) {
				//butcher <name> - butcher someone fully
				radius = 20;
				target = playerHelper.MatchPlayerSingle(args[0]);
			}
			break;

		default:
			try {
				//butcher <radius> <name> - butcher around someone in the given radius
				radius = Integer.parseInt(args[0]);
				target = playerHelper.MatchPlayerSingle(args[1]);
			}
			catch (NumberFormatException e) {
				//butcher <name> <...> - not sure yet
				target = playerHelper.MatchPlayerSingle(args[0]);

				try {
					//butcher <name> <radius> - butcher around someone in the given radius
					radius = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException e2) {
					throw new YiffBukkitCommandException("Syntax error", e2);
				}
			}
			break;
		}

		int removed = 0;

		final World world;
		if (commandSender instanceof Player)
			world = ((Player)commandSender).getWorld();
		else
			world = plugin.GetOrCreateWorld("world", Environment.NORMAL);

		if (radius < 0) {
			for (LivingEntity livingEntity : world.getLivingEntities()) {
				if (isSpared(livingEntity))
					continue;

				livingEntity.remove();
				++removed;
			}

			playerHelper.SendServerMessage(commandSender.getName() + " killed all mobs.", commandSender);
			playerHelper.SendDirectedMessage(commandSender, "Killed "+removed+" mobs.");
			return;
		}

		final Vector targetPos = target.getLocation().toVector();
		final double radiusSquared = radius*radius;
		for (LivingEntity livingEntity : world.getLivingEntities()) {
			if (isSpared(livingEntity))
				continue;

			final Vector currentPos = livingEntity.getLocation().toVector();
			final double distanceSquared = currentPos.distanceSquared(targetPos);

			if (distanceSquared > radiusSquared)
				continue;

			livingEntity.remove();
			++removed;
		}

		if (target == commandSender) {
			playerHelper.SendServerMessage(commandSender.getName() + " killed all mobs in a radius of "+radius+" around themselves.", commandSender);
			playerHelper.SendDirectedMessage(commandSender, "Killed "+removed+" mobs in a radius of "+radius+" around yourself.");
		}
		else {
			playerHelper.SendServerMessage(commandSender.getName() + " killed all mobs in a radius of "+radius+" around "+target.getName()+".");
			playerHelper.SendDirectedMessage(commandSender, "Killed "+removed+" mobs in a radius of "+radius+" around "+target.getName()+".");
		}
	}

	private boolean isSpared(LivingEntity livingEntity) {
		if (livingEntity instanceof Player)
			return true;

		if (livingEntity instanceof CraftWolf) {
			CraftWolf wolf = (CraftWolf) livingEntity;
			if (wolf.isAngry())
				return false;

			EntityWolf eWolf = wolf.getHandle();
			if (eWolf.n_())
				return true;
		}

		return false;
	}
}
