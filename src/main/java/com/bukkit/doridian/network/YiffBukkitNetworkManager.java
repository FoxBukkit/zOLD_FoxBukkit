package com.bukkit.doridian.network;

import java.net.Socket;
import java.net.SocketAddress;
import java.util.Hashtable;

import org.bukkit.entity.Player;

import com.bukkit.doridian.yiffbukkit.Utils;
import com.bukkit.doridian.yiffbukkit.YiffBukkit;

import net.minecraft.server.NetHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet4UpdateTime;

public class YiffBukkitNetworkManager extends NetworkManager {
	private final NetworkManager instance;
	private YiffBukkit plugin;
	private String playerName;

	@Override
	public void a(Packet packet) {
		if (packet instanceof Packet4UpdateTime) {
			Hashtable<String, Long> frozenTimes = plugin.playerHelper.frozenTimes;
			if (frozenTimes.containsKey(playerName)) {
				Packet4UpdateTime timePacket = (Packet4UpdateTime)packet;
				timePacket.a = frozenTimes.get(playerName);
			}
			else if (plugin.playerHelper.frozenServerTime != null) {
				Packet4UpdateTime timePacket = (Packet4UpdateTime)packet;
				timePacket.a = plugin.playerHelper.frozenServerTime;
			}
		}
		
		instance.a(packet);
	}

	@SuppressWarnings("deprecation")
	public YiffBukkitNetworkManager(NetworkManager other, YiffBukkit plugin, Player ply) {
		super((Socket)new NullSocket(), (String)Utils.getPrivateValue(NetworkManager.class, other, "s"), (NetHandler)null);
		this.instance = other;
		this.plugin = plugin;
		this.playerName = ply.getName();
		
		/*for (char c = 'a'; c <= 'w'; ++c) {
			String fieldName = ""+c;
			Object value = Utils.getPrivateValue(NetworkManager.class, other, fieldName);
			Utils.setPrivateValue(NetworkManager.class, this, fieldName, value);
		}*/
		Thread thread1 = Utils.getPrivateValue(NetworkManager.class, this, "p");
		thread1.stop();
		Thread thread2 = Utils.getPrivateValue(NetworkManager.class, this, "q");
		thread2.stop();
		try {
			thread1.join();
			thread2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void a() {
		instance.a();
	}

	@Override
	public void a(NetHandler arg0) {
		instance.a(arg0);
	}

	@Override
	public void a(String arg0, Object... arg1) {
		//NetworkManager.class.getMeth
		switch (arg1.length) {
		case 0:
			instance.a(arg0);
			break;
		case 1:
			instance.a(arg0, arg1[0]);
			break;
		case 2:
			instance.a(arg0, arg1[0], arg1[1]);
			break;
		case 3:
			instance.a(arg0, arg1[0], arg1[1], arg1[2]);
			break;
		}
		
	}

	@Override
	public SocketAddress b() {
		return instance.b();
	}

	@Override
	public void c() {
		instance.c();
	}

	@Override
	public int d() {
		return instance.d();
	}

	@Override
	public boolean equals(Object obj) {
		return instance.equals(obj);
	}

	@Override
	public int hashCode() {
		return instance.hashCode();
	}

	@Override
	public String toString() {
		return instance.toString();
	}
}
/*
private java.net.Socket f;
private net.minecraft.server.NetHandler n;
*/