package de.doridian.yiffbukkitsplit.listeners;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.logging.Level;

public class YiffBukkitBungeeLink extends BaseListener {
    public YiffBukkitBungeeLink() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "yiffbukkitbungee");
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "yiffbukkitbungee", new PluginMessageListener() {
            @Override
            public void onPluginMessageReceived(String s, Player ply, byte[] bytes) {
                final String[] ret = new String(bytes).split("\\|");
                if(ret[0].equals("ip")) {
                    final String name = ply.getName().toLowerCase();
                    PlayerHelper.playerIPs.put(name, ret[1]);
                    PlayerHelper.playerHosts.put(name, ret[2]);

                    plugin.getLogger().log(Level.INFO, "Player " + ply.getName() + " join from [" + ret[1] + " / " + ret[2] + "]");
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch(Exception e) { }
                player.sendPluginMessage(plugin, "yiffbukkitbungee", "getip".getBytes());
            }
        }.start();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        final String name = event.getPlayer().getName().toLowerCase();
        PlayerHelper.playerIPs.remove(name);
        PlayerHelper.playerHosts.remove(name);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        if(event.isCancelled())
            return;
        final String name = event.getPlayer().getName().toLowerCase();
        PlayerHelper.playerIPs.remove(name);
        PlayerHelper.playerHosts.remove(name);
    }
}
