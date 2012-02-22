package de.doridian.yiffbukkit.teleportation.commands;

import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;

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
