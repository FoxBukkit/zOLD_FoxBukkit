package de.doridian.yiffbukkit.remote;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;

public class RemotePlayer extends ConsoleCommandSender {
	private YiffBukkitRemoteThread yiffBukkitRemoteThread;

	public RemotePlayer(Server server, YiffBukkitRemoteThread yiffBukkitRemoteThread) {
		super(server);
		this.yiffBukkitRemoteThread = yiffBukkitRemoteThread;
	}

	public void sendMessage(String message) {
		yiffBukkitRemoteThread.send(message);
	}
}
