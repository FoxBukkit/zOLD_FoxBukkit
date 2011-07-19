package de.doridian.yiffbukkit.remote;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.bukkit.command.CommandSender;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.listeners.YiffBukkitPlayerListener;



public class YiffBukkitRemote extends Thread {
	public static CommandSender currentCommandSender;

	private YiffBukkitPlayerListener listen;
	private YiffBukkit plugin;
	private ServerSocket socket;

	public YiffBukkitRemote(YiffBukkit plug, YiffBukkitPlayerListener listener) {
		plugin = plug;
		listen = listener;
		try {
			socket = new ServerSocket(13388,0,InetAddress.getByName("localhost"));
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
