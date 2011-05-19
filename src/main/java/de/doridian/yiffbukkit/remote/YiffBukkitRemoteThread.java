package de.doridian.yiffbukkit.remote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.bukkit.Server;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.listeners.YiffBukkitPlayerListener;

public class YiffBukkitRemoteThread extends Thread {
	private YiffBukkitPlayerListener listen;
	private YiffBukkit plugin;
	private Socket socket;
	private PrintWriter out;

	private final String PASSWORD = "SECRET";

	public YiffBukkitRemoteThread(YiffBukkit plug, YiffBukkitPlayerListener listener, Socket sock) {
		plugin = plug;
		listen = listener;
		socket =  sock;
	}

	public void run() {
		final String command;
		final Server server;
		final Player ply;

		try {
			final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);

			final String str = in.readLine();
			if(!str.equals(PASSWORD)) throw new Exception("Invalid password");

			send("OK");
			out.flush();

			command = in.readLine();
			server = plugin.getServer();
			ply = new RemotePlayer(server, plugin.GetOrCreateWorld("world", Environment.NORMAL), this);
		}
		catch(Exception e) {
			e.printStackTrace();
			send(e.getMessage());
			return;
		}

		server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				try {
					YiffBukkitRemote.currentPlayer = ply;
					boolean ret = listen.runCommand(ply, command);

					if(!ret) throw new Exception("Invalid command");
				}
				catch(Exception e) {
					e.printStackTrace();
					send(e.getMessage());
				}
				finally {
					YiffBukkitRemote.currentPlayer = null;

					try {
						out.flush();
					}
					catch(Exception e) { }
					try {
						socket.close();
					}
					catch(Exception e) { }
				}
			}
		});
	}

	public void send(String txt) {
		try {
			out.write(txt + "\n");
		}
		catch(Exception e) { }
	}
}
