package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.PlayerHelper;
import de.doridian.yiffbukkit.YiffBukkit;

public abstract class ICommand {
	protected YiffBukkit plugin;
	protected PlayerHelper playerHelper;
	
	protected ICommand(YiffBukkit plug)
	{
		plugin = plug;
		playerHelper = plugin.playerHelper;
	}
	public abstract int GetMinLevel();
	public abstract void Run(Player ply, String[] args, String argStr);
	public abstract String GetHelp();
	public abstract String GetUsage();
}
