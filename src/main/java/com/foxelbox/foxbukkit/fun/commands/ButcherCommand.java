/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.fun.commands;

import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.AbusePotential;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.BooleanFlags;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Help;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Names;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Permission;
import com.foxelbox.foxbukkit.main.commands.system.ICommand.Usage;
import com.foxelbox.foxbukkit.main.util.MultiplePlayersFoundException;
import com.foxelbox.foxbukkit.main.util.PlayerNotFoundException;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Wolf;
import org.bukkit.util.Vector;

import java.util.List;

@Names("fbutcher")
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
@Permission("foxbukkit.butcher")
@AbusePotential
public class ButcherCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		args = parseFlags(args);
		int radius;
		Location target;
		switch (args.length) {
		case 0:
			//butcher - butcher around yourself in a radius of 20
			radius = 20;
			target = getCommandSenderLocation(commandSender, false);

			break;

		case 1:
			try {
				//butcher <radius> - butcher around yourself in the given radius
				radius = args[0].equalsIgnoreCase("all") ? -1 : Integer.parseInt(args[0]);
				target = getCommandSenderLocation(commandSender, false);
			}
			catch (NumberFormatException e) {
				//butcher <name> -  butcher around someone else in a radius of 20
				radius = 20;
				target = getMatchedPlayerLocation(args[0]);
			}
			break;

		default:
			try {
				//butcher <radius> <name> - butcher around someone in the given radius
				radius = Integer.parseInt(args[0]);
				target = getMatchedPlayerLocation(args[1]);
			}
			catch (NumberFormatException e) {
				//butcher <name> <...> - not sure yet
				target = getMatchedPlayerLocation(args[0]);

				try {
					//butcher <name> <radius> - butcher around someone in the given radius
					radius = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException e2) {
					throw new FoxBukkitCommandException("Syntax error", e2);
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

			PlayerHelper.sendServerMessage(commandSender.getName() + " killed all mobs.", commandSender);
			PlayerHelper.sendDirectedMessage(commandSender, "Killed "+removed+" mobs.");
			return;
		}

		final Vector targetPos = target.toVector();
		final double radiusSquared = radius*radius;

		final List<? extends Entity> entities;
		if (booleanFlags.contains('v'))
			entities = world.getEntities();
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

		//if (target == commandSender) {
			PlayerHelper.sendServerMessage(commandSender.getName() + " killed all mobs in a radius of " + radius + " around themselves.", commandSender);
			PlayerHelper.sendDirectedMessage(commandSender, "Killed "+removed+" mobs in a radius of "+radius+" around yourself.");
		/*}
		else {
			playerHelper.sendServerMessage(commandSender.getName() + " killed all mobs in a radius of "+radius+" around "+target.getName()+".");
			PlayerHelper.sendDirectedMessage(commandSender, "Killed "+removed+" mobs in a radius of "+radius+" around "+target.getName()+".");
		}*/
	}

	public Location getMatchedPlayerLocation(final String arg) throws PlayerNotFoundException, MultiplePlayersFoundException {
		return playerHelper.matchPlayerSingle(arg).getLocation();
	}

	private boolean isSpared(Entity entity, boolean spareNPCs) {
		if (entity instanceof LivingEntity) {
			if (entity instanceof Player) {
				if (spareNPCs)
					return true;

				final EntityPlayer eply = ((CraftPlayer) entity).getHandle();

				return eply.world.players.contains(eply);
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
