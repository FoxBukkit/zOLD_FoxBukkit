package de.doridian.yiffbukkit.spectate.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.spectate.SpectatePlayer;
import org.bukkit.entity.Player;

@ICommand.Names({"spectateoff","specoff"})
@ICommand.Permission("yiffbukkit.spectate")
public class SpectateOffCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		SpectatePlayer currentPlayer = SpectatePlayer.wrapPlayer(ply);
		currentPlayer.unspectate();
	}
}
