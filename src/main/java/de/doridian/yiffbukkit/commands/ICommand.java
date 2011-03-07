package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.PlayerHelper;

public abstract class ICommand {
	protected YiffBukkit plugin;
	protected PlayerHelper playerHelper;
	
	protected ICommand(YiffBukkit plug)
	{
		plugin = plug;
		playerHelper = plugin.playerHelper;
	}
	
	public abstract int GetMinLevel();
	public abstract void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException;
	public String GetHelp() {
		return "";
	}
	public String GetUsage() {
		return "";
	}
	
	public boolean CanPlayerUseCommand(Player ply)
	{
		int plylvl = plugin.playerHelper.GetPlayerLevel(ply);
		int reqlvl = GetMinLevel();
		
		return (plylvl >= reqlvl);
	}
}
