package de.doridian.yiffbukkit.irc.commands;

import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import de.doridian.yiffbukkit.irc.Ircbot;
import org.bukkit.command.CommandSender;

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