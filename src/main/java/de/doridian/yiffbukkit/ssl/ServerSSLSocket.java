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
import java.net.Socket;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerSSLSocket extends Thread {
	private YiffBukkit plugin;
	private SSLServerSocket listenerSocket;

	private MinecraftServer server;

	private int connCount = 1;
	private File pubKeyDir;

	public ServerSSLSocket(YiffBukkit plug) throws IOException  {
		plugin = plug;

		pubKeyDir = new File("pubkeys");
		pubKeyDir.mkdirs();

		server = ((CraftServer)plugin.getServer()).getHandle().server;

		int sslport = Integer.valueOf(Configuration.getValue("server-ssl-port", "" + (plugin.getServer().getPort() + 1)));
		listenerSocket = (SSLServerSocket)SSLConnector.allTrustingSocketFactory.createServerSocket(sslport);
		listenerSocket.setUseClientMode(true);
		plugin.sendConsoleMsg("Bound SSL to " + sslport);
	}

	public void stopme() {
		try {
			listenerSocket.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private class NetLoginHandlerCertificate extends NetLoginHandler {
		private Certificate cert;
		private String isValidatedFor = null;

		public NetLoginHandlerCertificate(MinecraftServer server, Socket socket, String s, Certificate certUsed) {
			super(server, socket, s);
			cert = certUsed;
		}

		@Override
		public void a(Packet2Handshake packet2handshake) {
			String name = packet2handshake.a;
			if(cert != null && cert.getPublicKey().equals(readKey(name))) {
				isValidatedFor = name;
				this.networkManager.queue(new Packet2Handshake("-"));
				return;
			}
			super.a(packet2handshake);
		}

		@Override
		public void a(Packet1Login packet1login) {
			if(isValidatedFor == null || !isValidatedFor.equalsIgnoreCase(packet1login.name)) {
				isValidatedFor = null;
				super.a(packet1login);
			} else {
				plugin.sendConsoleMsg("Validated player " + isValidatedFor + " using their public key!");
				this.b(packet1login);
			}
		}

		@Override
		public void b(Packet1Login packet1login) {
			if(cert != null && server.onlineMode && isValidatedFor == null) {
				writeKey(packet1login.name, cert.getPublicKey());
			}

			super.b(packet1login);
		}
	}


	public File getPubKeyFile(String ply) {
		return new File(pubKeyDir, ply.toLowerCase() + ".key");
	}

	public void writeKey(String ply, PublicKey key) {
		try {
			FileOutputStream stream = new FileOutputStream(getPubKeyFile(ply));
			ObjectOutputStream writer = new ObjectOutputStream(stream);
			try {
				writer.writeObject(key);
			} catch(Exception e) {
				e.printStackTrace();
			}
			writer.close();
			stream.close();
		} catch(Exception e) { }
	}

   	public PublicKey readKey(String ply) {
		PublicKey retKey = null;
		try {
			FileInputStream stream = new FileInputStream(getPubKeyFile(ply));
			ObjectInputStream reader = new ObjectInputStream(stream);
			try {
				retKey = (PublicKey)reader.readObject();
			} catch(Exception e) {
				e.printStackTrace();
				retKey = null;
			}
			reader.close();
			stream.close();
		} catch(Exception e) { }
		return retKey;
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

								NetLoginHandler netloginhandler = new NetLoginHandlerCertificate(server, socket, "SSL Connection #" + (connCount++), cert);

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
