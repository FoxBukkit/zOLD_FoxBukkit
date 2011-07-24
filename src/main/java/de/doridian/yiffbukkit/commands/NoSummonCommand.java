package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.commands.ICommand.*;

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
