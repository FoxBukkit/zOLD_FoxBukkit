package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkitPlayerListener;

public class NoSummonCommand extends NoPortCommand {

	public NoSummonCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
		tpPermissions = null;
	}

	protected String what() {
		return "summoning";
	}
}
