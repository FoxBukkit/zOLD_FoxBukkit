package de.doridian.yiffbukkit.spectate.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.spectate.SpectatePlayer;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.entity.Player;

@ICommand.Names({"spectate","spec"})
@ICommand.Permission("yiffbukkit.spectate")
public class SpectateCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		SpectatePlayer currentPlayer = SpectatePlayer.wrapPlayer(ply);
		SpectatePlayer otherPlayer = SpectatePlayer.wrapPlayer(YiffBukkit.instance.playerHelper.matchPlayerSingle(argStr));
		currentPlayer.spectatePlayer(otherPlayer);
	}
}
