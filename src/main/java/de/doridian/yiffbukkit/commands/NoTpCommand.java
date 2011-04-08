package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkitPlayerListener;

public class NoTpCommand extends NoPortCommand {

	public NoTpCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
		summonPermissions = null;
	}

	protected String what() {
		return "teleportation";
	}
}
