package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitPlayerListener;

public class ConsoleCommand extends ICommand {
	public int GetMinLevel() {
		return 5;
	}

	public ConsoleCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	public void Run(Player ply, String[] args, String argStr) {
		//String[] argsX = new String[args.length - 1];
		//System.arraycopy(args, 1, argsX, 0, argsX.length);
		//plugin.getServer().
		//plugin.getServer().getPluginCommand(args[0]).execute(ply, args[0], argsX);
		plugin.sendServerCmd(argStr,ply);
	}

	public String GetUsage() {
		return "<command>";
	}
	
	public String GetHelp() {
		return "Pushes a command to console";
	}
}
