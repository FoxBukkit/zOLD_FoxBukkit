package de.doridian.yiffbukkit.remote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitPlayerListener;

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
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		
			String str = in.readLine();
			if(!str.equals(PASSWORD)) throw new Exception("Invalid password");
			
			send("OK");
			out.flush();
			
			str = in.readLine();
			Player ply = new RemotePlayer(plugin.getServer(), plugin.GetOrCreateWorld("world", Environment.NORMAL), this);
			YiffBukkitRemote.currentPlayer = ply; 
			boolean ret = listen.runCommand(ply, str);
			YiffBukkitRemote.currentPlayer = null;
			
			if(!ret) throw new Exception("Invalid command");
		}
		catch(Exception e) {
			e.printStackTrace();
			send(e.getMessage());
		}
		try {
			out.flush();
		}
		catch(Exception e) { }
		try {
			socket.close();
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
