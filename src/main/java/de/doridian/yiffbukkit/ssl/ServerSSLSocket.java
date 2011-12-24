package de.doridian.yiffbukkit.ssl;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.Configuration;
import de.doridian.yiffbukkit.util.Utils;
import net.minecraft.server.*;
import org.bukkit.craftbukkit.CraftServer;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import java.io.*;
import java.net.InetAddress;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerSSLSocket extends Thread {
	private YiffBukkit plugin;
	private SSLServerSocket listenerSocket;

	private MinecraftServer server;
	private SSLPlayerListener sslPlayerListener;

	private int connCount = 1;

	public ServerSSLSocket(YiffBukkit plug) throws IOException  {
		plugin = plug;

		server = ((CraftServer)plugin.getServer()).getHandle().server;

		int sslport = Integer.valueOf(Configuration.getValue("server-ssl-port", "" + (plugin.getServer().getPort() + 1)));
		listenerSocket = (SSLServerSocket)SSLConnector.allTrustingSocketFactory.createServerSocket(sslport);
		listenerSocket.setUseClientMode(true);
		plugin.sendConsoleMsg("Bound SSL to " + sslport);
		sslPlayerListener = new SSLPlayerListener(plugin);
	}

	public void stopme() {
		try {
			listenerSocket.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while(listenerSocket.isBound() && !listenerSocket.isClosed()) {
			try {
				final SSLSocket socket = (SSLSocket)listenerSocket.accept();
				new Thread() {
					public void run() {
						try {
							Certificate cert = null;
							try {
								cert = socket.getSession().getPeerCertificates()[0];
								cert.verify(cert.getPublicKey());
							} catch(Exception e) { cert = null; e.printStackTrace(); }

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

								NetLoginHandler netloginhandler = new NetLoginHandlerSSL(plugin, server, socket, "SSL Connection #" + (connCount++), cert);

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
