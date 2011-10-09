package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names({"channel", "c"})
@Help("YiffBukkit chat system :3")
@Usage("Read help...")
public class ChannelCommand extends ICommand {
	private enum SubCommand {
		JOIN, LIST, INFO, //everyone [or yiffbukkit.channels.force.user for forcing join allowance]
		CREATE, //yiffbukkit.channels.create
		PASSWORD, MODERATOR, DROP, MODE, RANGE, //channel owner or yiffbukkit.channels.force.owner
		KICK, USER, //moderator or yiffbukkit.channels.force.moderator
		SWITCH, SAY, MUTE, SINGLE, LEAVE; //users in channel or yiffbukkit.channels.force.user
	}
	
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		 SubCommand cmd = SubCommand.valueOf(args[0].toUpperCase());
		 
		 switch(cmd) {
		 	case JOIN:
		 		break;
		 }
	}
}
