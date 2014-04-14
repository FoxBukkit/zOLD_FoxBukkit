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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        if(event.isCancelled())
            return;
		final UUID name = event.getPlayer().getUniqueId();
        synchronized(PlayerHelper.playerIPs) {
            PlayerHelper.playerIPs.remove(name);
            PlayerHelper.playerHosts.remove(name);
        }
    }
}
