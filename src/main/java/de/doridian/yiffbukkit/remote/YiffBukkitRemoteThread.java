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
	
	public YiffBukkitRemoteThread(YiffBukkit plug, YiffBukkitPlayerListener listener, Socket sock) {
		plugin = plug;
		listen = listener;
		socket =  sock;
	}
	
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		
			String cmd = in.readLine();
			runCommand(cmd);
			out.flush();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		try {
			socket.close();
		}
		catch(Exception e) {
			
		}
	}
	
	public void send(String txt) {
		out.write(txt + "\n");
	}
	
	public void runCommand(String xcmd) {
		Player ply = new RemotePlayer(plugin.getServer(), plugin.GetOrCreateWorld("world", Environment.NORMAL), this);
		YiffBukkitRemote.currentPlayer = ply; 
		listen.runCommand(ply, xcmd);
		YiffBukkitRemote.currentPlayer = null; 
	}
}
