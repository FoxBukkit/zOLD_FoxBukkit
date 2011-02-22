package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkit;

public class NoSummonCommand extends NoPortCommand {

	public NoSummonCommand(YiffBukkit plug) {
		super(plug);
		tpPermissions = null;
	}

	protected String what() {
		return "summoning";
	}
}
