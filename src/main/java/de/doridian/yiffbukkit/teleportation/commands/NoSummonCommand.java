package de.doridian.yiffbukkit.teleportation.commands;

import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;

@Names("nosummon")
@Help("Prevents summoning or grants/revokes exceptions.")
@Usage("[on|off|allow <name>|deny <name>]")
@Permission("yiffbukkit.teleport.noport.nosummon")
public class NoSummonCommand extends NoPortCommand {
	public NoSummonCommand() {
		tpPermissions = null;
	}

	@Override
	protected String what() {
		return "summoning";
	}
}
