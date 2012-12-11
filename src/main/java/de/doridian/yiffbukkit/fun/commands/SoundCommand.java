package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Names("sound")
@Help("Makes noise")
@Usage("<volume> <pitch> <sound name>")
@Permission("yiffbukkit.sound")
public class SoundCommand extends ICommand {
	final Pattern argumentPattern = Pattern.compile("^([^ ]+) ([^ ]+) (.+)$");

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		final Matcher matcher = argumentPattern.matcher(argStr);
		if (!matcher.matches())
			throw new YiffBukkitCommandException("Invalid arguments.");

		final float volume = Float.parseFloat(matcher.group(1));
		final float pitch = Float.parseFloat(matcher.group(2));
		final String soundName = matcher.group(3);

		Utils.makeSound(ply.getLocation(), soundName, volume, pitch);

		PlayerHelper.sendDirectedMessage(ply, "Played sound "+soundName+" at volume "+volume+" and pitch "+pitch+".");
	}
}
