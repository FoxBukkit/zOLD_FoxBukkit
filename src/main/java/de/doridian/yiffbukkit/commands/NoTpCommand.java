package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.commands.ICommand.Usage;

@Names("notp")
@Help("Prevents teleportation or grants/revokes exceptions.")
@Usage("[on|off|allow <name>|deny <name>]")
@Permission("yiffbukkit.teleport.noport.notp")
public class NoTpCommand extends NoPortCommand {
	public NoTpCommand() {
		summonPermissions = null;
	}

	@Override
	protected String what() {
		return "teleportation";
	}
}
