package de.doridian.yiffbukkit.teleportation.commands;

import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;

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
