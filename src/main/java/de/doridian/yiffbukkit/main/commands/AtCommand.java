/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.PersistentScheduler;
import de.doridian.yiffbukkit.main.util.RunString;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Names("at")
@Help("Runs a command after the given amount of seconds.")
@Usage("<seconds> <command>[;<command>[;<command> ...]")
@Permission("yiffbukkit.at")
public class AtCommand extends ICommand {
	private static final Pattern argumentPattern = Pattern.compile("^([^ ]+) (.*)$");
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		Matcher matcher = argumentPattern.matcher(argStr);
		if (!matcher.matches())
			throw new YiffBukkitCommandException("Syntax error");

		long t = (long) (Double.parseDouble(matcher.group(1))*1000);
		String commandString = matcher.group(2);

		final RunString parsedCommands = new RunString(commandString , Collections.singleton(""));
		PersistentScheduler.schedule(t, commandSender, parsedCommands);
	}
}
