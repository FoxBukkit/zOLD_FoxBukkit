package de.doridian.yiffbukkit.ssl;

import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class SSLUtils {
	private static HashSet<String> sslPlayers = new HashSet<String>();
	public static void setSSLState(Player ply, boolean hasSSL) {
		setSSLState(ply.getName(), hasSSL);
	}

	public static void setSSLState(String plyName, boolean hasSSL) {
		plyName = plyName.toLowerCase();
		if(hasSSL) {
			if(!sslPlayers.contains(plyName)) {
				sslPlayers.add(plyName);
			}
		} else {
			if(sslPlayers.contains(plyName)) {
				sslPlayers.remove(plyName);
			}
		}
	}

	public static boolean hasSSL(Player ply) {
		return hasSSL(ply.getName());
	}

	public static boolean hasSSL(String plyName) {
		return sslPlayers.contains(plyName.toLowerCase());
	}
	
	public static void nagIfNoSSL(PlayerHelper plyHelper, Player ply) {
		if(!hasSSL(ply)) {
			PlayerHelper.sendDirectedMessage(ply, "Please connect using +mc.doridian.de for SSL!");
			PlayerHelper.sendDirectedMessage(ply, "Please note that this is *required* for /setrank and /rcon!");
		}
	}
}
