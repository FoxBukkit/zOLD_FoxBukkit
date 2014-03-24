package de.doridian.yiffbukkit.advanced.listeners;

import de.doridian.yiffbukkit.advanced.packetlistener.YBPacketListener;
import de.doridian.yiffbukkit.componentsystem.YBListener;
import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.AutoCleanup;
import de.doridian.yiffbukkit.main.util.Utils;
import gnu.trove.TDecorators;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.server.v1_7_R2.Packet;
import net.minecraft.server.v1_7_R2.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R2.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R2.PacketPlayOutEntity;
import net.minecraft.server.v1_7_R2.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R2.PacketPlayOutEntityHeadRotation;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class YiffBukkitHeadChopOffListener extends YBPacketListener implements Listener, YBListener {
	private final static byte CHOPPED_PITCH = (byte) 128;

	public static YiffBukkitHeadChopOffListener instance;

	public YiffBukkitHeadChopOffListener() {
		instance = this;
		Bukkit.getServer().getPluginManager().registerEvents(this, YiffBukkit.instance);

		AutoCleanup.registerEntityIdSet(TDecorators.wrap(choppedEntities));

		register(PacketDirection.OUTGOING, 20);
		register(PacketDirection.OUTGOING, 24);
		register(PacketDirection.OUTGOING, 32);
		register(PacketDirection.OUTGOING, 33);
		register(PacketDirection.OUTGOING, 34);
		register(PacketDirection.OUTGOING, 35);
	}

	private final TIntHashSet choppedEntities = new TIntHashSet();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK)
			return;

		if (!(event instanceof EntityDamageByEntityEvent))
			return;

		final Entity damagedEntity = event.getEntity();
		if (damagedEntity instanceof Player)
			return;

		if (!(damagedEntity instanceof LivingEntity))
			return;

		final int entityId = damagedEntity.getEntityId();
		if (choppedEntities.contains(entityId))
			return;

		final Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
		if (!(damager instanceof Player))
			return;

		final Material materialInHand = ((Player) damager).getItemInHand().getType();

		switch (materialInHand) {
		case DIAMOND_SPADE:
		case GOLD_SPADE:
		case IRON_SPADE:
		case WOOD_SPADE:
		case STONE_SPADE:
			addChoppedEntity(entityId);
			break;
		}
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
		switch (packetID) {
		case 20:
			final PacketPlayOutNamedEntitySpawn p20 = (PacketPlayOutNamedEntitySpawn) packet;
			if(!choppedEntities.contains(p20.a)) // v1_7_R1
				break;

			p20.g = CHOPPED_PITCH; // v1_7_R1

			break;

		case 24:
			final PacketPlayOutSpawnEntityLiving p24 = (PacketPlayOutSpawnEntityLiving) packet;
			if(!choppedEntities.contains(p24.a)) // v1_7_R1
				break;

			p24.j = CHOPPED_PITCH; // v1_7_R1

			break;

		case 34:
			final PacketPlayOutEntityTeleport p34 = (PacketPlayOutEntityTeleport) packet;
			if (!choppedEntities.contains(p34.a)) // v1_7_R1
				break;

			p34.f = CHOPPED_PITCH; // v1_7_R1

			break;

		case 32:
		case 33:
			final PacketPlayOutEntity p30 = (PacketPlayOutEntity) packet;
			if(!choppedEntities.contains(p30.a)) // v1_7_R1
				break;

			p30.f = CHOPPED_PITCH; // v1_7_R1

			break;

		case 35:
			final PacketPlayOutEntityHeadRotation p35 = (PacketPlayOutEntityHeadRotation) packet;
			if (!choppedEntities.contains(p35.a)) // v1_7_R1
				break;

			final float yaw = Utils.getEntityByID(p35.a, ply.getWorld()).yaw; // v1_7_R1
			p35.b = (byte) (((yaw % 360) / 360) * 255); // v1_7_R1

			break;
		}
		return true;
	}

	public void addChoppedEntity(int entityId) {
		choppedEntities.add(entityId);
	}
}
