package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("setrank")
@Help("Sets rank of specified user")
@Usage("<full name> <rank>")
@Level(3)
public class SetRankCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws PermissionDeniedException {
		String otherply = args[0];
		String newrank = args[1];
		int selflvl = playerHelper.GetPlayerLevel(commandSender);

		if(!playerHelper.ranklevels.containsKey(newrank)) {
			playerHelper.SendDirectedMessage(commandSender, "Rank does not exist!");
			return;
		}

		if(selflvl <= playerHelper.GetPlayerLevel(otherply) || selflvl <= playerHelper.GetRankLevel(newrank))
			throw new PermissionDeniedException();

		playerHelper.SetPlayerRank(otherply, newrank);
		playerHelper.SendServerMessage(commandSender.getName() + " set rank of " + otherply + " to " + newrank);
	}
}
