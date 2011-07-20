package de.doridian.yiffbukkit.commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("exec")
@Help("Enters all non-empty lines that dont start with # from the given file into your chat. Files are taken from scripts/<filename>.txt")
@Usage("<filename>")
@Permission("yiffbukkit.exec")
public class ExecCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (argStr.isEmpty())
			throw new YiffBukkitCommandException("Expected file name.");

		if (argStr.matches("[^-a-zA-Z0-9_ ]"))
			throw new YiffBukkitCommandException("Invalid file name. Can only contain a-z, A-Z, 0-9, underscore(_), dash(-) and space");

		List<String> list = new ArrayList<String>();

		try {
			BufferedReader stream = new BufferedReader(new FileReader("scripts/"+argStr+".txt"));
			String line;
			while((line = stream.readLine()) != null) {
				if (line.isEmpty())
					continue;

				if (line.charAt(0) == '#')
					continue;

				list.add(line);
			}
		}
		catch (IOException e) {
			throw new YiffBukkitCommandException("Error while reading file.", e);
		}

		for (String line : list) {
			ply.chat(line);
		}
	}
}
