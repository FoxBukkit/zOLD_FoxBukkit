package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.commands.ICommand.Usage;
import de.doridian.yiffbukkit.login.DoriLogin;
import org.bukkit.entity.Player;

@Names("setpass")
@Help("Sets your web login password.")
@Usage("<password>")
@Permission("yiffbukkit.useless.password")
public class PasswordCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		DoriLogin.setPassword(ply.getName(), argStr);
	}
}
