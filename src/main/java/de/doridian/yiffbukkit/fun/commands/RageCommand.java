package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.spawning.effects.system.YBEffect;

import org.bukkit.entity.Player;

@Names("rage")
@Permission("yiffbukkit.experimental.rage")
public class RageCommand extends ICommand {
	@Override
	public void Run(final Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		YBEffect.create("rage", ply).start();
	}
}
