package com.bukkit.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class GoNetherCommand extends ICommand {
	public int GetMinLevel() {
		return 3;
	}
	
	public GoNetherCommand(YiffBukkit plug) {
		plugin = plug;
	}

	public void Run(Player ply, String[] args, String argStr) {
		ply.teleportTo(plugin.TogglePlayerWorlds(ply, ply.getLocation()));
	}
	
	public String GetHelp() {
		return "Toggles between nether and normal";
	}

	public String GetUsage() {
		return "";
	}
}
