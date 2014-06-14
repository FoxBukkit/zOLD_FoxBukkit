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
package com.foxelbox.foxbukkit.chat;

import com.google.gson.Gson;
import com.foxelbox.dependencies.redis.AbstractRedisHandler;
import com.foxelbox.foxbukkit.chat.json.ChatMessage;
import com.foxelbox.foxbukkit.core.FoxBukkit;
import com.foxelbox.foxbukkit.main.chat.Parser;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R3.command.CraftConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RedisHandler extends AbstractRedisHandler {
	public RedisHandler() {
		super(FoxBukkit.instance.redisManager, "foxbukkit:to_server");
	}

	public static void sendMessage(final CommandSender player, final String  message) {
		if(player == null || message == null)
			throw new NullPointerException();
        if(player instanceof Player)
		    FoxBukkit.instance.redisManager.publish("foxbukkit:from_server", FoxBukkit.instance.configuration.getValue("server-name", "Main") + "|" + player.getUniqueId() + "|" + player.getName() + "|" + message);
        else
            FoxBukkit.instance.redisManager.publish("foxbukkit:from_server", FoxBukkit.instance.configuration.getValue("server-name", "Main") + "|" + CraftConsoleCommandSender.CONSOLE_UUID + "|" + player.getName() + "|" + message);
	}

	private final Gson gson = new Gson();

	@Override
	public void onMessage(final String c_message) {
		try {
			final ChatMessage chatMessage;
			synchronized (gson) {
				chatMessage = gson.fromJson(c_message, ChatMessage.class);
			}

			if (!chatMessage.server.equals(FoxBukkit.instance.configuration.getValue("server-name", "Main"))) {
				chatMessage.contents.plain = "\u00a72[" + chatMessage.server + "]\u00a7f " + chatMessage.contents.plain;
				if(chatMessage.contents.xml_format != null)
					chatMessage.contents.xml_format = "<color name=\"dark_green\">[" + chatMessage.server + "]</color> " + chatMessage.contents.xml_format;
			}

			List<Player> allPlayers = Arrays.asList(FoxBukkit.instance.getServer().getOnlinePlayers());
			List<Player> targetPlayers = new ArrayList<>();
			switch(chatMessage.to.type) {
				case "all":
					targetPlayers = allPlayers;
					break;
				case "permission":
					for(String permission : chatMessage.to.filter)
						for (Player player : allPlayers)
							if (player.hasPermission(permission) && !targetPlayers.contains(player))
								targetPlayers.add(player);
					break;
				case "player":
					for(String playerUUID : chatMessage.to.filter)
						for (Player player : allPlayers)
							if (player.getUniqueId().equals(UUID.fromString(playerUUID)) && !targetPlayers.contains(player))
								targetPlayers.add(player);
					break;
			}

			if(chatMessage.contents.xml_format != null)
				Parser.sendToPlayers(targetPlayers, chatMessage.contents.xml_format, chatMessage.contents.xml_format_args);
			else
				for(Player plyTarget : targetPlayers)
					plyTarget.sendMessage(chatMessage.contents.plain);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
