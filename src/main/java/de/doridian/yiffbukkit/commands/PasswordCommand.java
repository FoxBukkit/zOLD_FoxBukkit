package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.login.DoriLogin;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("setpass")
@Help("Sets your web login password.")
@Usage("<password>")
@Level(2)
@Permission("yiffbukkit.useless.password")
public class PasswordCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		DoriLogin.setPassword(ply.getName(), argStr);
	}
}
