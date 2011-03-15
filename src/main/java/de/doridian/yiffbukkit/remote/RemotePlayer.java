package de.doridian.yiffbukkit.remote;

import org.bukkit.Server;
import org.bukkit.World;

import de.doridian.yiffbukkit.offlinebukkit.OfflinePlayer;

public class RemotePlayer extends OfflinePlayer {

	private YiffBukkitRemoteThread socket;
	public RemotePlayer(Server server, World world, YiffBukkitRemoteThread thread) {
		super(server, world, "[CONSOLE]");
		socket = thread;
	}

	public void sendMessage(String message) {
		socket.send(message);
	}
}
