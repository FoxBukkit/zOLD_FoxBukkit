package de.doridian.yiffbukkit.ssl;

import de.doridian.yiffbukkit.YiffBukkit;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.Packet1Login;
import net.minecraft.server.Packet2Handshake;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.security.cert.Certificate;

class NetLoginHandlerSSL extends NetLoginHandler {
	private Certificate cert;
	private String isValidatedFor = null;
	private MinecraftServer mcserver;
	private YiffBukkit plugin;
	
	private static File pubKeyDir;
	static {
		try {
			pubKeyDir = new File("pubkeys");
			pubKeyDir.mkdirs();
		}
		catch(Exception e) { e.printStackTrace(); }
	}

	NetLoginHandlerSSL(YiffBukkit plug, MinecraftServer server, Socket socket, String s, Certificate certUsed) {
		super(server, socket, s);
		mcserver = server;
		cert = certUsed;
		plugin = plug;
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
			(new ThreadSSLPrelogin(this, packet1login, plugin)).start();
		}
	}

	@Override
	public void b(Packet1Login packet1login) {
		if(cert != null && mcserver.onlineMode && isValidatedFor == null) {
			writeKey(packet1login.name, cert.getPublicKey());
		}

		SSLUtils.setSSLState(packet1login.name, true);

		super.b(packet1login);
	}

	private static File getPubKeyFile(String ply) {
		return new File(pubKeyDir, ply.toLowerCase() + ".key");
	}

	private static void writeKey(String ply, PublicKey key) {
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

	private static PublicKey readKey(String ply) {
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
}

