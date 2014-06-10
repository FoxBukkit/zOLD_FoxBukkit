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
package com.foxelbox.foxbukkit.chat.listeners;

import com.foxelbox.foxbukkit.chat.ChatReplacer;
import com.foxelbox.foxbukkit.chat.ChatSounds;
import com.foxelbox.foxbukkit.chat.RedisHandler;
import com.foxelbox.foxbukkit.chat.commands.ChatReplacementCommand;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.listeners.BaseListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener extends BaseListener {
	public ChatListener() {
		new RedisHandler();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		String msg = event.getMessage();
		if (msg.charAt(0) == '/')
			return;

		ChatSounds.processMessage(event.getPlayer(), msg);

		try {
			final Player ply = event.getPlayer();

			if(!ply.hasPermission("foxbukkit.chatreplace.override")) {
				String tmp;
				for(ChatReplacer replacer : ChatReplacementCommand.chatReplacers) {
					tmp = replacer.replace(msg);
					if(tmp != null) msg = tmp;
				}
			}
			plugin.sendConsoleMsg("<" + ply.getName() + "> " + msg, false);
            if(msg.charAt(0) == '\u0123')
                msg = msg.substring(1);
			RedisHandler.sendMessage(ply, msg);
		}
		catch (Exception e) {
			e.printStackTrace();
			PlayerHelper.sendDirectedMessage(event.getPlayer(), e.getMessage());
		}

		event.setCancelled(true);
	}
}
