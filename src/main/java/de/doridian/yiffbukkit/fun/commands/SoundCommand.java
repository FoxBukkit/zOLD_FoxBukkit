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
package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Names("sound")
@Help("Makes noise")
@Usage("<volume> <pitch> <sound name>")
@Permission("yiffbukkit.sound")
public class SoundCommand extends ICommand {
	final Pattern argumentPattern = Pattern.compile("^([^ ]+) ([^ ]+) (.+)$");

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		args = parseFlags(args);

		final Matcher matcher = argumentPattern.matcher(argStr);
		if (!matcher.matches())
			throw new YiffBukkitCommandException("Invalid arguments.");

		final float volume = Float.parseFloat(matcher.group(1));
		final float pitch = Float.parseFloat(matcher.group(2));
		final String soundName = matcher.group(3);

		Utils.makeSound(getCommandSenderLocation(commandSender, true), soundName, volume, pitch);

		PlayerHelper.sendDirectedMessage(commandSender, "Played sound "+soundName+" at volume "+volume+" and pitch "+pitch+".");
	}
}
