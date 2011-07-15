package de.doridian.yiffbukkit.transmute;

import net.minecraft.server.Packet17;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet20NamedEntitySpawn;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;
import org.bukkit.plugin.Plugin;

public class TransmutePacketListener extends PacketListener {
	private final Transmute transmute;

	public TransmutePacketListener(Transmute transmute) {
		this.transmute = transmute;
		Plugin plugin = transmute.plugin;

		PacketListener.addPacketListener(true, 17, this, plugin);
		PacketListener.addPacketListener(true, 18, this, plugin);
		PacketListener.addPacketListener(true, 20, this, plugin);
		//PacketListener.addPacketListener(true, 24, this, plugin);
	}

	@Override
	public boolean onOutgoingPacket(final Player ply, int packetID, final Packet packet) {
		switch (packetID) {
		case 17:
			final Packet17 p17 = (Packet17) packet;

			return !transmute.isTransmuted(p17.a);

		case 18:
			final Packet18ArmAnimation p18 = (Packet18ArmAnimation) packet;

			if (p18.b == 2)
				return true;

			return !transmute.isTransmuted(p18.a);

		case 20: {
			final Packet20NamedEntitySpawn p20 = (Packet20NamedEntitySpawn) packet;
			if (p20.b.equals("Herobrine"))
				return true;

			final int entityID = p20.a;
			//final Entity entity = ((CraftWorld)ply.getWorld()).getHandle().getEntity(entityID);

			Shape shape = transmute.getShape(entityID);
			if (shape == null)
				return true;

			shape.createTransmutedEntity(ply);

			return false;
		}

		/*case 24: {
			//if (true) return true;
			final Packet24MobSpawn p24 = (Packet24MobSpawn) packet;
			final int entityID = p24.a;
			final Entity entity = getEntityFromID(ply, entityID);
			if (entity == null) {
				transmute.plugin.getServer().getScheduler().scheduleSyncDelayedTask(transmute.plugin, new Runnable() {
					public void run() {
						transmute.plugin.playerHelper.sendPacketToPlayer(ply, (net.minecraft.server.Packet) packet);
					}
				});
				return false;
			}
			if (!(entity instanceof EntityZombie))
				return true;

			final Packet20NamedEntitySpawn newp20 = new Packet20NamedEntitySpawn();
			newp20.a = entityID;
			newp20.b = "Herobrine";
			newp20.c = p24.c;
			newp20.d = p24.d;
			newp20.e = p24.e;
			newp20.f = p24.f;
			newp20.g = p24.g;
			transmute.plugin.playerHelper.sendPacketToPlayer(ply, newp20);

			return false;
		}*/

		default:
			return true;
		}
	}

	/*private Entity getEntityFromID(final Player ply, final int entityID) {
		return ((CraftWorld)ply.getWorld()).getHandle().getEntity(entityID);
	}*/
}
