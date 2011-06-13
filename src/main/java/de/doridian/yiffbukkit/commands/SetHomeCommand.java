package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("sethome")
@Help("Sets your home position (see /home)")
@Level(0)
public class SetHomeCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		playerHelper.setPlayerHomePosition(ply, ply.getLocation());
		playerHelper.sendDirectedMessage(ply, "Home location saved!");
	}
}