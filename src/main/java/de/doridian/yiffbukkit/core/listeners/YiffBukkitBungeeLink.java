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
package de.doridian.yiffbukkit.core.listeners;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.listeners.BaseListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.InetSocketAddress;
import java.util.UUID;

public class YiffBukkitBungeeLink extends BaseListener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
		final UUID name = event.getPlayer().getUniqueId();
		final InetSocketAddress inet = event.getPlayer().getAddress();
		final String ip = inet.getAddress().getHostAddress();
		final String host = inet.getAddress().getCanonicalHostName();
		synchronized (PlayerHelper.playerIPs) {
			PlayerHelper.playerIPs.put(name, ip);
			PlayerHelper.playerHosts.put(name, host);
		}
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDisconnect(PlayerQuitEvent event) {
		final UUID name = event.getPlayer().getUniqueId();
        synchronized(PlayerHelper.playerIPs) {
            PlayerHelper.playerIPs.remove(name);
            PlayerHelper.playerHosts.remove(name);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
		final UUID name = event.getPlayer().getUniqueId();
        synchronized(PlayerHelper.playerIPs) {
            PlayerHelper.playerIPs.remove(name);
            PlayerHelper.playerHosts.remove(name);
        }
    }
}
