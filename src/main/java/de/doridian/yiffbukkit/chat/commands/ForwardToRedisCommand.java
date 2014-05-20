/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbukkit.chat.commands;

import de.doridian.yiffbukkit.chat.RedisHandler;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import org.bukkit.entity.Player;

@ICommand.Names({"pm", "conv", "emote", "me", "tell", "msg", "list"})
@ICommand.Help("Well, it's /me, durp")
@ICommand.Usage("<stuff here>")
@ICommand.Permission("yiffbukkit.redisforwardcommand")
public class ForwardToRedisCommand extends ICommand {
	@Override
	public void Run(Player player, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		RedisHandler.sendMessage(player, "/" + commandName + " " + argStr);
	}
}
