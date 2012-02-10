package de.doridian.yiffbukkit.remote;

import de.doridian.yiffbukkit.main.util.Configuration;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class YiffBukkitRemoteThread extends Thread {
	public final YiffBukkit plugin;
	private final Socket socket;
	private PrintWriter out;

	private final String PASSWORD = Configuration.getValue("rcon-password", null);

	public YiffBukkitRemoteThread(YiffBukkit plugin, Socket socket) {
		this.plugin = plugin;
		this.socket = socket;
	}

	public void run() {
		final String command;
		final Server server;
		final CommandSender commandSender;

		try {
			final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);

			final String str = in.readLine();
			if(PASSWORD == null || !str.equals(PASSWORD)) throw new Exception("Invalid password");

			send("OK");
			flush();

			command = in.readLine();
			server = plugin.getServer();
			commandSender = new RemotePlayer(this);
		}
		catch(Exception e) {
			e.printStackTrace();
			send(e.getMessage());
			closeThis();
			return;
		}
		server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				try {
					YiffBukkitRemote.currentCommandSender = commandSender;
					boolean ret = plugin.commandSystem.runCommand(commandSender, command);
					if(!ret) throw new Exception("Invalid command");
				}
				catch(Exception e) {
					e.printStackTrace();
					send(e.getMessage());
				}
				server.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						closeThis();
					}
				}, 2);
			}
		});
	}

	private void closeThis() {
		YiffBukkitRemote.currentCommandSender = null;
		flush();
		try {
			socket.close();
		}
		catch(Exception e) { }
	}
	
	public void flush() {
		try {
			out.flush();
		}
		catch(Exception e) { }
	}
	
	public void send(String txt) {
		try {
			out.write(txt + "\n");
		}
		catch(Exception e) { }
	}
}
