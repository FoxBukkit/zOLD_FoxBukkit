package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("test")
@Permission("yiffbukkit.useless.test")
public class TestCommand extends ICommand {
	@Override
	public void Run(final Player ply, String[] args, String argStr) {
		plugin.mcbans.evidence(ply, "doridian", null);
	}
}
