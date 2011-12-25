package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import de.doridian.yiffbukkit.irc.Ircbot;
import org.bukkit.command.CommandSender;

@Names({"ircunmuteall"})
@Help("Unmutes the IRC Chat")
@Permission("yiffbukkit.irc.muteall")
public class IRCUnmuteAllCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		plugin.ircbot.sendToPublicChannel(commandSender.getName() + " unmuted IRC Chat");
		playerHelper.sendServerMessage(commandSender.getName() + " unmuted IRC Chat");
		plugin.ircbot.setMode(Ircbot.PUBLICCHANNEL, "-m");
	}
}