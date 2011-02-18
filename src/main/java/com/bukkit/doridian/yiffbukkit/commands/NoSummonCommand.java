package com.bukkit.doridian.yiffbukkit.commands;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class NoSummonCommand extends NoPortCommand {

	public NoSummonCommand(YiffBukkit plug) {
		super(plug);
		tpPermissions = null;
	}

	protected String what() {
		return "summoning";
	}
}
