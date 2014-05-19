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
