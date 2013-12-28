package de.doridian.yiffbukkit.remote;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.main.util.Configuration;
import org.bukkit.command.CommandSender;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class YiffBukkitRemote extends Thread {
	public static CommandSender currentCommandSender;

	private YiffBukkit plugin;
	private ServerSocket socket;

	public YiffBukkitRemote(YiffBukkit plugin) {
		this.plugin = plugin;
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
				YiffBukkitRemoteThread thread = new YiffBukkitRemoteThread(plugin, socketX);
				thread.start();
			}
			catch(SocketException e) {
				if (socket.isClosed())
					break;

				e.printStackTrace();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
