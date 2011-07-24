package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.mcbans.MCBansBlockLoggerLogBlock;

@Names("test")
@Permission("yiffbukkit.useless.test")
public class TestCommand extends ICommand {
	@Override
	public void Run(final Player ply, String[] args, String argStr) {
		MCBansBlockLoggerLogBlock logger = new MCBansBlockLoggerLogBlock(plugin);
		String str = logger.getFormattedBlockChangesBy(args[0], ply.getWorld(), true);
		System.out.println(str);
		ply.sendMessage(str);
	}
}
