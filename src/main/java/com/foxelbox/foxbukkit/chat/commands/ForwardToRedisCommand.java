/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.chat.commands;

import com.foxelbox.foxbukkit.chat.RedisHandler;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import org.bukkit.entity.Player;

@ICommand.Names({"pm", "conv", "emote", "me", "tell", "msg", "list"})
@ICommand.Help("Well, it's /me, durp")
@ICommand.Usage("<stuff here>")
@ICommand.Permission("foxbukkit.redisforwardcommand")
public class ForwardToRedisCommand extends ICommand {
	@Override
	public void Run(Player player, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		RedisHandler.sendMessage(player, "/" + commandName + " " + argStr);
	}
}
