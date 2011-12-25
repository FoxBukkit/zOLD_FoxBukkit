package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import org.bukkit.entity.Player;

@Names("rage")
@Permission("yiffbukkit.experimental.rage")
public class RageCommand extends ICommand {
	@Override
	public void Run(final Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (!playerHelper.rage(ply, 100)) {
			throw new YiffBukkitCommandException("Already raging!");
		}
	}
}
