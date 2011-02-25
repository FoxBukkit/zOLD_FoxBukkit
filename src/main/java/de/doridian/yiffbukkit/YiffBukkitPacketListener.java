package de.doridian.yiffbukkit;

import org.bukkit.entity.Player;

import net.minecraft.server.IPacketListener;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet4UpdateTime;

public class YiffBukkitPacketListener implements IPacketListener {
	private final YiffBukkit plugin;
	
	public YiffBukkitPacketListener(YiffBukkit instance) {
		plugin = instance;
	}
	

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		Packet4UpdateTime p4 = (Packet4UpdateTime)packet;
		Long frozenTime = plugin.playerHelper.frozenTimes.get(ply.getName());

		if (frozenTime != null) {
			p4.a = frozenTime;
		}
		else if (plugin.playerHelper.frozenServerTime != null) {
			p4.a = plugin.playerHelper.frozenServerTime;
		}

		return true;
	}

}
