package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.irc.Ircbot;
import de.doridian.yiffbukkit.util.Utils;

@Names({"irckick", "irck"})
@Help("Kicks specified user from IRC Chat")
@Usage("<full name>")
@Permission("yiffbukkit.irc.kick")
public class IRCKickCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		String reason = commandSender.getName() + ": " + Utils.concatArray(args, 1, "Kicked");
		plugin.ircbot.kick(Ircbot.PUBLICCHANNEL, args[0], reason);
	}
}