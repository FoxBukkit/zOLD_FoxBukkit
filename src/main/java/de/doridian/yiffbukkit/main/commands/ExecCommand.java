package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import de.doridian.yiffbukkit.main.config.ConfigFileReader;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Names("exec")
@Help("Enters all non-empty lines that dont start with # from the given file into your chat. Files are taken from scripts/<filename>.txt")
@Usage("<filename>")
@Permission("yiffbukkit.exec")
public class ExecCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		if (argStr.isEmpty())
			throw new YiffBukkitCommandException("Expected file name.");

		if (argStr.matches("[^-a-zA-Z0-9_ ]"))
			throw new YiffBukkitCommandException("Invalid file name. Can only contain a-z, A-Z, 0-9, underscore(_), dash(-) and space");

		List<String> list = new ArrayList<String>();

		try {
			BufferedReader stream = new BufferedReader(new ConfigFileReader("scripts/"+argStr+".txt"));
			String line;
			while((line = stream.readLine()) != null) {
				if (line.isEmpty())
					continue;

				if (line.charAt(0) == '#')
					continue;

				if (line.charAt(0) == '\\')
					line = line.substring(1);

				list.add(line);
			}
		}
		catch (IOException e) {
			throw new YiffBukkitCommandException("Error while reading file.", e);
		}

		for (String line : list) {
			chat(commandSender, line);
		}
	}

	private void chat(CommandSender commandSender, String line) {
		if (commandSender instanceof Player) {
			((Player) commandSender).chat(line);
			return;
		}

		if (line.startsWith("/")) {
			Bukkit.getServer().dispatchCommand(commandSender, line.substring(1));
			return;
		}

		Bukkit.getServer().dispatchCommand(commandSender, "say "+line);
	}
}
