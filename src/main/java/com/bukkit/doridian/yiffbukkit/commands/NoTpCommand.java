package com.bukkit.doridian.yiffbukkit.commands;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class NoTpCommand extends NoPortCommand {

	public NoTpCommand(YiffBukkit plug) {
		super(plug);
		summonPermissions = null;
	}

	protected String what() {
		return "teleportation";
	}
}
