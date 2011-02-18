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
		
		String arg0 = args.length >= 1 ? args[0] : "";
		
		if (argStr.equals("on") || argStr.equals("1")) {
			newState = true;
		}
		else if (argStr.equals("off") || argStr.equals("0")) {
			newState = false;
		}
		else if (arg0.equals("allow") || arg0.equals("accept")) {
			if (args.length < 2) {
				plugin.playerHelper.SendDirectedMessage(ply, "Usage: " + GetUsage());
				return;
			}
			
			String otherName = plugin.playerHelper.CompletePlayerName(args[1]);
			if (otherName == null) {
				plugin.playerHelper.SendDirectedMessage(ply, "Sorry, multiple players found!");
			}
			else {
				setException(playerName, otherName, true);
				plugin.playerHelper.SendDirectedMessage(ply, "Allowed "+what()+" for "+otherName+".");
			}
			return;
		}
		else if (arg0.equals("deny") || arg0.equals("reject") || arg0.equals("revoke") || arg0.equals("forbid")) {
			if (args.length < 2) {
				plugin.playerHelper.SendDirectedMessage(ply, "Usage: " + GetUsage());
				return;
			}
			
			String otherName = plugin.playerHelper.CompletePlayerName(args[1]);
			if (otherName == null) {
				plugin.playerHelper.SendDirectedMessage(ply, "Sorry, multiple players found!");
			}
			else {
				setException(playerName, otherName, false);
				plugin.playerHelper.SendDirectedMessage(ply, "Disallowed "+what()+" for "+otherName+".");
			}
			return;
		}
		else if (argStr.isEmpty()) {
			// toggle
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
		else {
			plugin.playerHelper.SendDirectedMessage(ply, "Usage: " + GetUsage());
			return;
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

	private void setException(String playerName, String otherName, boolean newState) {
		String pair = playerName+" "+otherName;
		
		if (tpCommand != null) {
			if (newState)
				tpCommand.playerPortExceptions.add(pair);
			else
				tpCommand.playerPortExceptions.remove(pair);
		}
		if (summonCommand != null) {
			if (newState)
				summonCommand.playerPortExceptions.add(pair);
			else
				summonCommand.playerPortExceptions.remove(pair);
		}
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
		return "[on|off|allow <name>|deny <name>]";
	}
}
