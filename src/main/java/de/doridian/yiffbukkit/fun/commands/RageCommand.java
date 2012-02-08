package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import org.bukkit.entity.Player;

@Names("rage")
@Permission("yiffbukkitsplit.experimental.rage")
public class RageCommand extends ICommand {
	@Override
	public void Run(final Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		if (!playerHelper.rage(ply, 100)) {
			throw new YiffBukkitCommandException("Already raging!");
		}
	}
}
