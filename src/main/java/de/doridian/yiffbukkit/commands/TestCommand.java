package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import org.bukkit.entity.Player;

@Names("test")
@Permission("yiffbukkit.useless.test")
public class TestCommand extends ICommand {
	@Override
	public void Run(final Player ply, String[] args, String argStr) {
		plugin.playerHelper.sendYiffcraftClientCommand(ply, 't', "Creeper");
	}
}
