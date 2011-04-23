package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.util.Utils;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("compass")
@Help("Gives you your current bearing")
@Level(0)
public class CompassCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		float yaw = ply.getLocation().getYaw();
		playerHelper.SendDirectedMessage(ply, "Direction: "+Utils.yawToDirection(yaw)+" ("+Math.round((yaw+720)%360)+")");
	}
}
