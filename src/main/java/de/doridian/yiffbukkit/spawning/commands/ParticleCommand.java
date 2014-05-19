package de.doridian.yiffbukkit.spawning.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.spawning.SpawnUtils;

@Names("particle")
@Help("Spawns a particle.")
@Permission("yiffbukkit.particle")
public class ParticleCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		final Location commandSenderLocation = getCommandSenderLocation(commandSender, true);
		SpawnUtils.makeParticles(commandSenderLocation, new Vector(.1, .1, .1), 0, 10, argStr);
	}
}
