package de.doridian.yiffbukkit.console;

import de.doridian.yiffbukkit.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RawListExecutor implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if(commandSender instanceof Player) return true;
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		String str = "Connected players: ";
		if(players.length > 0) {
			str += players[0].getName();
			for(int i=1;i<players.length;i++) {
				str += ", " + players[i].getName();
			}
		}
		commandSender.sendMessage(str);
		return true;
	}
}
