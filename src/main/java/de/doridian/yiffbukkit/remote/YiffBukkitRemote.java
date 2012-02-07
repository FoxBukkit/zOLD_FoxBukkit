package de.doridian.yiffbukkit.remote;

import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.listeners.YiffBukkitPlayerListener;
import de.doridian.yiffbukkit.main.util.Configuration;
import org.bukkit.command.CommandSender;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class YiffBukkitRemote extends Thread {
	public static CommandSender currentCommandSender;

	private YiffBukkitPlayerListener listen;
	private YiffBukkit plugin;
	private ServerSocket socket;

	public YiffBukkitRemote(YiffBukkit plug, YiffBukkitPlayerListener listener) {
		plugin = plug;
		listen = listener;
		try {
			socket = new ServerSocket(Integer.valueOf(Configuration.getValue("rcon-port", "13388")), 0, InetAddress.getByName(Configuration.getValue("rcon-host", "localhost")));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void stopme() {
		try {
			socket.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while(socket.isBound() && !socket.isClosed()) {
			try {
				Socket socketX = socket.accept();
				YiffBukkitRemoteThread thread = new YiffBukkitRemoteThread(plugin, listen, socketX);
				thread.start();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
