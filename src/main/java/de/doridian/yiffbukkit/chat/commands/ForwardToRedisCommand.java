package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.chat.RedisHandler;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import org.bukkit.entity.Player;

@ICommand.Names({"pm", "conv", "emote", "me", "tell", "msg"})
@ICommand.Help("Well, it's /me, durp")
@ICommand.Usage("<stuff here>")
@ICommand.Permission("yiffbukkit.redisforwardcommand")
public class ForwardToRedisCommand extends ICommand {
	@Override
	public void Run(Player player, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		RedisHandler.sendMessage(player, "/" + commandName + " " + argStr);
	}
}
