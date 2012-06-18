package de.doridian.yiffbukkit.ssl;

import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.Packet1Login;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

class ThreadSSLPrelogin extends Thread {
	final Packet1Login loginPacket;
	final NetLoginHandler netLoginHandler;

	YiffBukkit plugin;

	ThreadSSLPrelogin(NetLoginHandler netloginhandler, Packet1Login packet1login, YiffBukkit plug) {
		this.plugin = plug;

		this.netLoginHandler = netloginhandler;
		this.loginPacket = packet1login;
	}

	public void run() {
		try {
			// CraftBukkit start
			if (this.netLoginHandler.getSocket() == null) {
				return;
			}

			AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(this.loginPacket.name, this.netLoginHandler.getSocket().getInetAddress());
			plugin.getServer().getPluginManager().callEvent(asyncEvent);

			PlayerPreLoginEvent event = new PlayerPreLoginEvent(this.loginPacket.name, this.netLoginHandler.getSocket().getInetAddress());
			if (asyncEvent.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
				event.disallow(asyncEvent.getResult(), asyncEvent.getKickMessage());
			}
			plugin.getServer().getPluginManager().callEvent(event);

			if (event.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
				this.netLoginHandler.disconnect(event.getKickMessage());
				return;
			}
			// CraftBukkit end

			Utils.setPrivateValue(NetLoginHandler.class, netLoginHandler, "h", this.loginPacket);
		} catch(Exception e) {
			this.netLoginHandler.disconnect("SSL login error: " + e);
		}
	}
}
