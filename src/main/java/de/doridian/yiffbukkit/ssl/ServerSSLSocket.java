package de.doridian.yiffbukkit.ssl;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.Configuration;
import de.doridian.yiffbukkit.util.Utils;
import net.minecraft.server.*;
import org.bukkit.craftbukkit.CraftServer;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerSSLSocket extends Thread {
	private YiffBukkit plugin;
	private SSLServerSocket listenerSocket;

	private MinecraftServer server;
	
	private int connCount = 1;

	public ServerSSLSocket(YiffBukkit plug)  {
		plugin = plug;

		server = ((CraftServer)plugin.getServer()).getHandle().server;

		try {
			int sslport = Integer.valueOf(Configuration.getValue("server-ssl-port", "" + (plugin.getServer().getPort() + 1)));
			listenerSocket = (SSLServerSocket)SSLConnector.allTrustingSocketFactory.createServerSocket(sslport);
			plugin.sendConsoleMsg("Bound SSL to " + sslport);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void stopme() {
		try {
			listenerSocket.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private class NetLoginHandlerNonValidating extends NetLoginHandler {
		private boolean isValidated;

		public NetLoginHandlerNonValidating(MinecraftServer server, Socket socket, String s, boolean prevalidated) {
			super(server, socket, s);
			isValidated = prevalidated;
		}

		public void a(Packet2Handshake packet2handshake) {
			if(!isValidated)
				super.a(packet2handshake);
			else
				this.networkManager.queue(new Packet2Handshake("-"));
		}

		public void a(Packet1Login packet1login) {
			if(!isValidated)
				super.a(packet1login);
			else
				this.b(packet1login);
		}
	}

	public void run() {
		while(listenerSocket.isBound() && !listenerSocket.isClosed()) {
			try {
				final SSLSocket socket = (SSLSocket)listenerSocket.accept();
				new Thread() {
					public void run() {
						try {
							final HashMap<InetAddress, Long> networkListenThreadB = Utils.getPrivateValue(NetworkListenThread.class, server.networkListenThread, "i");

							if (socket != null) {
								synchronized (networkListenThreadB) {
									InetAddress inetaddress = socket.getInetAddress();

									if (networkListenThreadB.containsKey(inetaddress) && System.currentTimeMillis() - ((Long)networkListenThreadB.get(inetaddress)) < 5000L) {
										networkListenThreadB.put(inetaddress, (Long)System.currentTimeMillis());
										socket.close();
										return;
									}

									networkListenThreadB.put(inetaddress, (Long)System.currentTimeMillis());
								}

								NetLoginHandler netloginhandler = new NetLoginHandlerNonValidating(server, socket, "SSL Connection #" + (connCount++), false);

								final ArrayList<NetLoginHandler> networkListenThreadG = Utils.getPrivateValue(NetworkListenThread.class, server.networkListenThread, "g");
								networkListenThreadG.add(netloginhandler);
							}
						}
						catch(Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
