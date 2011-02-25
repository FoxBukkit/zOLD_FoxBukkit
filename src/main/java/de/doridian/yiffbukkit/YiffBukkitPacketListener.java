package de.doridian.yiffbukkit;

import org.bukkit.entity.Player;

import net.minecraft.server.IPacketListener;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet4UpdateTime;

public class YiffBukkitPacketListener implements IPacketListener {
	private final YiffBukkit plugin;
	private PlayerHelper playerHelper;
	
	public YiffBukkitPacketListener(YiffBukkit instance) {
		plugin = instance;
		playerHelper = plugin.playerHelper;
	}
	

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		Packet4UpdateTime p4 = (Packet4UpdateTime)packet;
		Long frozenTime = playerHelper.frozenTimes.get(ply.getName());

		if (frozenTime != null) {
			p4.a = frozenTime;
		}
		else if (playerHelper.frozenServerTime != null) {
			p4.a = playerHelper.frozenServerTime;
		}

		return true;
	}

}
