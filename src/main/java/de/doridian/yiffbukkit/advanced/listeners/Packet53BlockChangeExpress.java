package de.doridian.yiffbukkit.advanced.listeners;

import java.util.Map;

import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.Packet53BlockChange;
import de.doridian.yiffbukkit.main.util.Utils;

public class Packet53BlockChangeExpress extends Packet53BlockChange {
	static {
		Map<Class<? extends Packet>, Integer> classToId = Utils.getPrivateValue(Packet.class, null, "a");
		classToId.put(Packet53BlockChangeExpress.class, 53);
	}

	public Packet53BlockChangeExpress() {
		lowPriority = false;
	}

	public Packet53BlockChangeExpress(int x, int y, int z, net.minecraft.server.v1_6_R2.World world) {
		super(x, y, z, world);
		lowPriority = false;
	}
}
