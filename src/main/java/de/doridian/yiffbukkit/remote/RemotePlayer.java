package de.doridian.yiffbukkit.remote;

import org.bukkit.craftbukkit.v1_7_R3.command.CraftConsoleCommandSender;

public class RemotePlayer extends CraftConsoleCommandSender {
	private YiffBukkitRemoteThread yiffBukkitRemoteThread;

	public RemotePlayer(YiffBukkitRemoteThread yiffBukkitRemoteThread) {
		this.yiffBukkitRemoteThread = yiffBukkitRemoteThread;
	}

	public void sendMessage(String message) {
		yiffBukkitRemoteThread.send(message);
		yiffBukkitRemoteThread.plugin.sendConsoleMsg("YiffBukkitRemote: "+message);
	}
}
