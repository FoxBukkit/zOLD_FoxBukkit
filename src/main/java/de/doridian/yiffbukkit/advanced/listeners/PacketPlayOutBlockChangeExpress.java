package de.doridian.yiffbukkit.advanced.listeners;

import java.util.Map;

import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutBlockChange;
import de.doridian.yiffbukkit.main.util.Utils;

public class PacketPlayOutBlockChangeExpress extends PacketPlayOutBlockChange {
	static {
		Map<Class<? extends Packet>, Integer> classToId = Utils.getPrivateValue(Packet.class, null, "a");
		classToId.put(PacketPlayOutBlockChangeExpress.class, 53);
	}

	public PacketPlayOutBlockChangeExpress() {
		lowPriority = false;
	}

	public PacketPlayOutBlockChangeExpress(int x, int y, int z, net.minecraft.server.v1_7_R1.World world) {
		super(x, y, z, world);
		lowPriority = false;
	}
}
