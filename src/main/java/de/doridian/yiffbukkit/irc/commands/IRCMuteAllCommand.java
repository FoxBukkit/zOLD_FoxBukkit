package de.doridian.yiffbukkit.irc.commands;

import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.irc.Ircbot;
import org.bukkit.command.CommandSender;

@Names({"ircmuteall", "ircmall"})
@Help("Mutes the IRC Chat")
@Permission("yiffbukkit.irc.muteall")
public class IRCMuteAllCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		plugin.ircbot.sendToPublicChannel(commandSender.getName() + " muted IRC Chat");
		playerHelper.sendServerMessage(commandSender.getName() + " muted IRC Chat");
		plugin.ircbot.setMode(Ircbot.PUBLICCHANNEL, "+m");
	}
}