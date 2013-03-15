package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.BooleanFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_5_R1.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_5_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Wolf;
import org.bukkit.util.Vector;

import java.util.List;

@Names("ybutcher")
@Help(
		"Kills living entities around yourself or a specified target.\n"+
		"The default radius is 20. To kill everything, use a radius\n"+
		"of -1. Players and tamed wolves are never butchered.\n"+
		"Flags:\n"+
		"  -n butcher NPCs too\n"+
		"  -v remove vehicles too"
)
@Usage("[<target>] [<radius>]")
@BooleanFlags("nvl")
@Permission("yiffbukkit.butcher")
public class ButcherCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);
		int radius;
		Player target;
		switch (args.length) {
		case 0:
			//butcher - butcher around yourself in a radius of 20
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
				//butcher <name> -  butcher around someone else in a radius of 20
				radius = 20;
				target = playerHelper.matchPlayerSingle(args[0]);
			}
			break;

		default:
			try {
				//butcher <radius> <name> - butcher around someone in the given radius
				radius = Integer.parseInt(args[0]);
				target = playerHelper.matchPlayerSingle(args[1]);
			}
			catch (NumberFormatException e) {
				//butcher <name> <...> - not sure yet
				target = playerHelper.matchPlayerSingle(args[0]);

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
			world = plugin.getOrCreateWorld("world", Environment.NORMAL);

		final boolean spareNPCs = !booleanFlags.contains('n');
		if (radius < 0) {
			for (LivingEntity livingEntity : world.getLivingEntities()) {
				if (isSpared(livingEntity, spareNPCs))
					continue;

				livingEntity.remove();
				++removed;
			}

			playerHelper.sendServerMessage(commandSender.getName() + " killed all mobs.", commandSender);
			PlayerHelper.sendDirectedMessage(commandSender, "Killed "+removed+" mobs.");
			return;
		}

		final Vector targetPos = target.getLocation().toVector();
		final double radiusSquared = radius*radius;

		final List<? extends Entity> entities;
		if (booleanFlags.contains('v'))
			entities = target.getNearbyEntities(radius*2, radius*2, radius*2);
		else
			entities = world.getLivingEntities();

		boolean doLightning = booleanFlags.contains('l');
		for (Entity entity : entities) {
			if (isSpared(entity, spareNPCs))
				continue;

			final Location location = entity.getLocation();
			final Vector currentPos = location.toVector();
			final double distanceSquared = currentPos.distanceSquared(targetPos);

			if (distanceSquared > radiusSquared)
				continue;

			if (doLightning) {
				entity.getWorld().strikeLightningEffect(location);
			}
			entity.remove();
			++removed;
		}

		if (target == commandSender) {
			playerHelper.sendServerMessage(commandSender.getName() + " killed all mobs in a radius of "+radius+" around themselves.", commandSender);
			PlayerHelper.sendDirectedMessage(commandSender, "Killed "+removed+" mobs in a radius of "+radius+" around yourself.");
		}
		else {
			playerHelper.sendServerMessage(commandSender.getName() + " killed all mobs in a radius of "+radius+" around "+target.getName()+".");
			PlayerHelper.sendDirectedMessage(commandSender, "Killed "+removed+" mobs in a radius of "+radius+" around "+target.getName()+".");
		}
	}

	private boolean isSpared(Entity entity, boolean spareNPCs) {
		if (entity instanceof LivingEntity) {
			if (entity instanceof Player) {
				if (spareNPCs)
					return true;

				final EntityPlayer eply = ((CraftPlayer)entity).getHandle();
				if (eply.world.players.contains(eply))
					return true;

				return false;
			}

			if (entity instanceof Wolf) {
				Wolf wolf = (Wolf) entity;
				if (wolf.isAngry())
					return false;

				if (wolf.isTamed())
					return true;
			}
			return false;
		}
		else if (entity instanceof Vehicle) {
			return false;
		}

		return true;
	}
}
