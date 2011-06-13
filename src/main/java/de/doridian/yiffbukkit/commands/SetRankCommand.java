package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;

import com.firestar.mcbans.mcbans;

import de.doridian.yiffbukkit.PermissionDeniedException;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("setrank")
@Help("Sets rank of specified user")
@Usage("<full name> <rank>")
@Level(3)
public class SetRankCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		String otherName = args[0];
		String newRank = args[1];
		String oldRank = playerHelper.getPlayerRank(otherName);

		if (newRank.equals(oldRank))
			throw new YiffBukkitCommandException("Player already has that rank!");

		if(!playerHelper.ranklevels.containsKey(newRank)) {
			throw new YiffBukkitCommandException("Rank does not exist!");
		}

		int selflvl = playerHelper.getPlayerLevel(commandSender);

		if(selflvl <= playerHelper.getPlayerLevel(otherName))
			throw new PermissionDeniedException();

		if(selflvl <= playerHelper.getRankLevel(newRank))
			throw new PermissionDeniedException();

		playerHelper.setPlayerRank(otherName, newRank);

		if (newRank.equals("banned")) {
			mcbans mcbansPlugin = (mcbans) plugin.getServer().getPluginManager().getPlugin("mcbans");		
			mcbansPlugin.mcb_handler.ban(otherName, commandSender.getName(), "Kickbanned by " + commandSender.getName(), "l");
		}
		else if (oldRank.equals("banned")) {
			mcbans mcbansPlugin = (mcbans) plugin.getServer().getPluginManager().getPlugin("mcbans");		
			mcbansPlugin.mcb_handler.unban(otherName, commandSender.getName());
		}

		playerHelper.sendServerMessage(commandSender.getName() + " set rank of " + otherName + " to " + newRank);
	}
}
