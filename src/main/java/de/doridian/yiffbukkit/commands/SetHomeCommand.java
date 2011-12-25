package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import org.bukkit.entity.Player;

@Names("sethome")
@Help("Sets your home position (see /home)")
@Permission("yiffbukkit.teleport.basic.sethome")
public class SetHomeCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		playerHelper.setPlayerHomePosition(ply, ply.getLocation());
		playerHelper.sendDirectedMessage(ply, "Home location saved!");
	}
}