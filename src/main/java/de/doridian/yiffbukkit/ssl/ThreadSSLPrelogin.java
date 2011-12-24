package de.doridian.yiffbukkit.ssl;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.Utils;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.Packet1Login;
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
			if (this.netLoginHandler.getSocket() == null) {
				return;
			}

			PlayerPreLoginEvent event = new PlayerPreLoginEvent(this.loginPacket.name, this.netLoginHandler.getSocket().getInetAddress());
			plugin.getServer().getPluginManager().callEvent(event);

			if (event.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
				this.netLoginHandler.disconnect(event.getKickMessage());
				return;
			}

			Utils.setPrivateValue(NetLoginHandler.class, netLoginHandler, "h", this.loginPacket);
		} catch(Exception e) {
			this.netLoginHandler.disconnect("SSL login error: " + e);
		}
	}
}
