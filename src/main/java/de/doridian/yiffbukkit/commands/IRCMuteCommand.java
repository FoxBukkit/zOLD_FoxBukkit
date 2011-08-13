package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.irc.Ircbot;

@Names({"ircmute", "ircm"})
@Help("Mutes specified user from IRC Chat")
@Usage("<full name>")
@Permission("yiffbukkit.irc.mute")
public class IRCMuteCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		plugin.ircbot.sendToPublicChannel(commandSender.getName() + " muted " + args[0]);
		playerHelper.sendServerMessage(commandSender.getName() + " muted " + args[0] + "@IRC");
		plugin.ircbot.setMode(Ircbot.PUBLICCHANNEL, "+q " + args[0] + "!*@*");
	}
}