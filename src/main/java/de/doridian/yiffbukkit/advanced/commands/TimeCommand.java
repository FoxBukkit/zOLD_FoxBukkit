package de.doridian.yiffbukkit.advanced.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.core.util.PlayerHelper.WeatherType;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.StringFlags;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Names("time")
@Help("Forces/fixes current time *clientside*.")
@Usage("[normal|night|day|morning|afternoon|<0-23>]")
@StringFlags("ws")
@Permission("yiffbukkit.time")
public class TimeCommand extends ServerTimeCommand {
	@Override
	protected void setTime(CommandSender commandSender, Long setTime, Long displayTime, WeatherType setWeather) throws YiffBukkitCommandException {
		if (booleanFlags.contains('s')) {
			if (!commandSender.hasPermission("yiffbukkit.time.set"))
				throw new PermissionDeniedException();

			if (setTime == null)
				throw new YiffBukkitCommandException("Must provide a time!");

			if (commandSender instanceof Player) {
				final World world = ((Player) commandSender).getWorld();
				world.setTime(setTime);

				PlayerHelper.sendDirectedMessage(commandSender, "You set the time on '"+world.getName()+"' to: " + displayTime + ":00");
			}
			else {
				for (World world : Bukkit.getWorlds()) {
					world.setTime(setTime);
				}

				PlayerHelper.sendDirectedMessage(commandSender, "You set the time on all worlds to: " + displayTime + ":00");
			}


			return;
		}
		if (setTime == null) {
			playerHelper.resetFrozenServerTime(asPlayer(commandSender));
			PlayerHelper.sendDirectedMessage(commandSender, "Reset your time back to normal!");
		}
		else {
			playerHelper.setFrozenServerTime(asPlayer(commandSender), setTime);
			PlayerHelper.sendDirectedMessage(commandSender, "You forced your time to be: " + displayTime + ":00");
		}

		if (setWeather == null) {
			playerHelper.frozenWeathers.remove(commandSender.getName());
			PlayerHelper.sendDirectedMessage(commandSender, "Reset your weather back to normal!");
		}
		else {
			playerHelper.frozenWeathers.put(commandSender.getName(), setWeather);
			PlayerHelper.sendDirectedMessage(commandSender, "You forced your weather to be: " + setWeather.name + ".");
		}

		playerHelper.pushWeather(asPlayer(commandSender));
	}
}
