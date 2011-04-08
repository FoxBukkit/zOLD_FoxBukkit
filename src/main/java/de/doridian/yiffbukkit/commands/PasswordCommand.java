package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitPlayerListener;
import de.doridian.yiffbukkit.login.DoriLogin;

public class PasswordCommand extends ICommand {
	public int GetMinLevel() {
		return 2;
	}

	public PasswordCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	public void Run(Player ply, String[] args, String argStr) {
		DoriLogin.setPassword(ply.getName(), argStr);
	}

	public String GetHelp() {
		return "Sets your web login password.";
	}

	public String GetUsage() {
		return "<password>";
	}
}
