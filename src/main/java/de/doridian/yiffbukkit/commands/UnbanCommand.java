package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;

import com.firestar.mcbans.mcbans;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({"unban", "pardon"})
@Help("Unbans specified user")
@Usage("<full name>")
@Level(3)
public class UnbanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		String otherply = args[0];
		if(!playerHelper.getPlayerRank(otherply).equals("banned")) {
			playerHelper.sendDirectedMessage(commandSender, "Player is not banned!");
			return;
		}

		playerHelper.setPlayerRank(otherply, "guest");
		
		mcbans mcbansPlugin = (mcbans) plugin.getServer().getPluginManager().getPlugin("mcbans");		
		mcbansPlugin.mcb_handler.unban(otherply, commandSender.getName());
		playerHelper.sendServerMessage(commandSender.getName() + " unbanned " + otherply + "!");
	}
}
