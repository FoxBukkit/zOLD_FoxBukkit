package de.doridian.yiffbukkit.mcbans;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;
import org.getspout.spout.packet.CustomPacket;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.packet.PacketFullVersion;
import org.getspout.spoutapi.packet.SpoutPacket;
import org.getspout.spoutapi.packet.listener.Listener;
import org.getspout.spoutapi.packet.listener.Listeners;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ClientBlacklist extends PacketListener {
	YiffBukkit plugin;
	ArrayList<Pattern> disallowedClients;
	
	public ClientBlacklist(YiffBukkit plug) {
		plugin = plug;
		PacketListener.addPacketListener(false, 195, this, plugin);
		loadBlacklist();
	}

	public void loadBlacklist() {
		disallowedClients = new ArrayList<Pattern>();
		addDisallowedClient("^ZC \\(.*\\)$");
		addDisallowedClient("^RainCraft");
	}

	private void addDisallowedClient(String pat) {
		disallowedClients.add(Pattern.compile(pat, Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE));
	}

	@Override
	public boolean onIncomingPacket(Player ply, int packetID, Packet opacket) {
		if(!(opacket instanceof CustomPacket)) return true;
		CustomPacket cpacket = (CustomPacket)opacket;
		if(!(cpacket.packet instanceof PacketFullVersion)) return true;
		PacketFullVersion vpacket = (PacketFullVersion)cpacket.packet;
		String version = (String)Utils.getPrivateValue(PacketFullVersion.class, vpacket, "versionString");

		for(Pattern pattern : disallowedClients) {
			if(pattern.matcher(version).matches()) {
				ply.kickPlayer("[YB] \u00a7kFAGGOTFAGGOTFAGGOTFAGGOTFAGGOT");
				break;
			}
		}

		return true;
	}
}
