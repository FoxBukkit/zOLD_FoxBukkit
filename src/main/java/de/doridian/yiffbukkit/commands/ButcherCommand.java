package de.doridian.yiffbukkit.commands;

import net.minecraft.server.EntityWolf;

import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.YiffBukkitPlayerListener;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Level;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Usage;


@Names("butcher")
@Help("Kills all living entities except for players and tamed wolves around yourself or a specified target.")
@Usage("[<target>] [<radius>]")
@Level(3)
public class ButcherCommand extends ICommand {

	public ButcherCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		int radius;
		Player target;
		switch (args.length) {
		case 0:
			//butcher <name> - butcher everything
			radius = 20;
			target = ply;

			break;

		case 1:
			try {
				//butcher <radius> - butcher around yourself in the given radius
				radius = Integer.parseInt(args[0]);
				target = ply;
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

		if (radius < 0) {
			for (LivingEntity livingEntity : ply.getWorld().getLivingEntities()) {
				if (spare(livingEntity))
					continue;

				livingEntity.remove();
				++removed;
			}

			playerHelper.SendServerMessage(ply.getName() + " killed all mobs.", ply);
			playerHelper.SendDirectedMessage(ply, "Killed "+removed+" mobs.");
			return;
		}

		final Vector targetPos = target.getLocation().toVector();
		final double radiusSquared = radius*radius;
		for (LivingEntity livingEntity : ply.getWorld().getLivingEntities()) {
			if (spare(livingEntity))
				continue;

			final Vector currentPos = livingEntity.getLocation().toVector();
			final double distanceSquared = currentPos.distanceSquared(targetPos);

			if (distanceSquared > radiusSquared)
				continue;

			livingEntity.remove();
			++removed;
		}

		if (target == ply) {
			playerHelper.SendServerMessage(ply.getName() + " killed all mobs in a radius of "+radius+" around themselves.", ply);
			playerHelper.SendDirectedMessage(ply, "Killed "+removed+" mobs in a radius of "+radius+" around yourself.");
		}
		else {
			playerHelper.SendServerMessage(ply.getName() + " killed all mobs in a radius of "+radius+" around "+target.getName()+".");
			playerHelper.SendDirectedMessage(ply, "Killed "+removed+" mobs in a radius of "+radius+" around "+target.getName()+".");
		}
	}

	private boolean spare(LivingEntity livingEntity) {
		if (livingEntity instanceof Player)
			return true;
		
		if (livingEntity instanceof CraftWolf) {
			CraftWolf wolf = (CraftWolf) livingEntity;
			EntityWolf eWolf = wolf.getHandle();
			if (eWolf.y())
				return true;
		}
		
		return false;
	}
}
