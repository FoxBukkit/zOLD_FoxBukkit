package de.doridian.yiffbukkit.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.jail.JailException;
import de.doridian.yiffbukkit.util.PlayerFindException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("jail")
@Help("Sends someone to a previously defined jail cell.")
@Usage("<name> [release]")
@Level(3)
@Permission("yiffbukkit.jail.jail")
public class JailCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws PlayerFindException, JailException {
		if (args.length == 0) {
			playerHelper.sendDirectedMessage(ply, "Not enough arguments.");
			return;
		}

		Player otherply = playerHelper.matchPlayerSingle(args[0]);

		if (args.length == 1) {
			plugin.jailEngine.jailPlayer(otherply, true);
			playerHelper.sendServerMessage(ply.getName()+" sent "+otherply.getName()+" to jail.");
		}
		else if (args[1].equals("release") || args[1].equals("rel") || args[1].equals("r")) {
			plugin.jailEngine.jailPlayer(otherply, false);
			playerHelper.sendServerMessage(ply.getName()+" released "+otherply.getName()+" from jail.");
		}
		else {
			playerHelper.sendDirectedMessage(ply, "Invalid argument.");
		}
	}
}
