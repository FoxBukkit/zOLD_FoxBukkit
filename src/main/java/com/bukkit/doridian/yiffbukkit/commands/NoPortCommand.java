package com.bukkit.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class NoPortCommand extends ICommand {
	TpCommand tpCommand;
	SummonCommand summonCommand;
	
	public int GetMinLevel() {
		return 1;
	}
	
	public NoPortCommand(YiffBukkit plug, TpCommand tpCommand, SummonCommand summonCommand) {
		plugin = plug;
		this.tpCommand = tpCommand;
		this.summonCommand = summonCommand;
	}

	public void Run(Player ply, String[] args, String argStr) {
		boolean newState;
		String playerName = ply.getName();
		
		if (argStr.equals("on")) {
			newState = true;
		}
		else if (argStr.equals("off")) {
			newState = false;
		}
		else {
			if (tpCommand == null) {
				newState = !summonCommand.playerForbidsPort.contains(playerName);
			}
			else if (summonCommand == null || tpCommand.playerForbidsPort.contains(playerName) == summonCommand.playerForbidsPort.contains(playerName)) {
				newState = !tpCommand.playerForbidsPort.contains(playerName);
			}
			else {
				plugin.playerHelper.SendDirectedMessage(ply, "The states of notp and nosummon differ. Please use !noport on/off explicitly.");
				return;
			}
		}
		
		if (tpCommand != null) {
			if (newState)
				tpCommand.playerForbidsPort.add(playerName);
			else
				tpCommand.playerForbidsPort.remove(playerName);
		}
		
		if (summonCommand != null) {
			if (newState)
				summonCommand.playerForbidsPort.add(playerName);
			else
				summonCommand.playerForbidsPort.remove(playerName);
		}
		
		plugin.playerHelper.SendDirectedMessage(ply, (newState ? "Disallowed" : "Allowed")+" "+what()+".");
	}

	private String what() {
		String what = null;
		if (tpCommand != null) {
			what = "teleportation";
		}
		
		if (summonCommand != null) {
			if (what == null)
				what = "summoning";
			else
				what += " and summoning";
		}
		return what;
	}
	
	public String GetHelp() {
		return "Prevents "+what();
	}

	public String GetUsage() {
		return "[<on|off>]";
	}
}
