package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public abstract class ICommand {
	protected YiffBukkit plugin;
	public ICommand()
	{
	}
	public ICommand(YiffBukkit plug)
	{
		plugin = plug;
	}
	public abstract int GetMinLevel();
	public abstract void Run(Player ply, String[] args, String argStr);
	public abstract String GetHelp();
	public abstract String GetUsage();
}
