package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitPlayerListener;

public class SetHomeCommand extends ICommand {
	public int GetMinLevel() {
		return 0;
	}

	public SetHomeCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	public void Run(Player ply, String[] args, String argStr) {
		playerHelper.SetPlayerHomePosition(ply, ply.getLocation());
		playerHelper.SendDirectedMessage(ply, "Home location saved!");
	}

	public String GetHelp() {
		return "Sets your home position (see /home)";
	}
}