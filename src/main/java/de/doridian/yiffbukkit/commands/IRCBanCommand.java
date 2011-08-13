package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.irc.Ircbot;

@Names({"ircban", "ircb"})
@Help("Bans specified user from IRC Chat")
@Usage("<full name>")
@Permission("yiffbukkit.irc.ban")
public class IRCBanCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		plugin.ircbot.sendToPublicChannel(commandSender.getName() + " banned " + args[0]);
		playerHelper.sendServerMessage(commandSender.getName() + " banned " + args[0] + "@IRC");
		plugin.ircbot.setMode(Ircbot.PUBLICCHANNEL, "+b " + args[0] + "!*@*");
		plugin.ircbot.kick(Ircbot.PUBLICCHANNEL, args[0]);
	}
}