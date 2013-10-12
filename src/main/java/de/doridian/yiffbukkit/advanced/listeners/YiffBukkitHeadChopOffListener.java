package de.doridian.yiffbukkit.advanced.listeners;

import de.doridian.yiffbukkit.advanced.packetlistener.YBPacketListener;
import de.doridian.yiffbukkit.componentsystem.YBListener;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.server.v1_6_R2.Packet;
import net.minecraft.server.v1_6_R2.Packet20NamedEntitySpawn;
import net.minecraft.server.v1_6_R2.Packet24MobSpawn;
import net.minecraft.server.v1_6_R2.Packet30Entity;
import net.minecraft.server.v1_6_R2.Packet34EntityTeleport;
import net.minecraft.server.v1_6_R2.Packet35EntityHeadRotation;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class YiffBukkitHeadChopOffListener extends YBPacketListener implements Listener, YBListener {
	private final static byte CHOPPED_PITCH = (byte)128;

	public static YiffBukkitHeadChopOffListener instance;

	public YiffBukkitHeadChopOffListener() {
		instance = this;
		Bukkit.getServer().getPluginManager().registerEvents(this, YiffBukkit.instance);

		register(PacketDirection.OUTGOING, 20);
		register(PacketDirection.OUTGOING, 24);
		register(PacketDirection.OUTGOING, 32);
		register(PacketDirection.OUTGOING, 33);
		register(PacketDirection.OUTGOING, 34);
		register(PacketDirection.OUTGOING, 35);
	}

	private TIntHashSet choppedEntities = new TIntHashSet();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {
		int eid = event.getEntity().getEntityId();
		choppedEntities.remove(eid);
	}

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

		final Player damagerPly = (Player) damager;
		final Material item = damagerPly.getItemInHand().getType();

		switch (item) {
		case DIAMOND_SPADE:
		case GOLD_SPADE:
		case IRON_SPADE:
		case WOOD_SPADE:
		case STONE_SPADE:
			addChoppedEntity(entityId);
			break;
		}
	}

	private net.minecraft.server.v1_6_R2.Entity getEntityByID(int eid, World world) {
		return ((CraftWorld)world).getHandle().getEntity(eid);
	}

	@Override
	public boolean onOutgoingPacket(Player ply, int packetID, Packet packetRaw) {
		switch (packetID) {
		case 20:
			final Packet20NamedEntitySpawn packet20 = (Packet20NamedEntitySpawn) packetRaw;
			if(!choppedEntities.contains(packet20.a)) // v1_6_R2
				break;

			packet20.g = CHOPPED_PITCH; // v1_6_R2

			break;

		case 24:
			final Packet24MobSpawn packet24 = (Packet24MobSpawn) packetRaw;
			if(!choppedEntities.contains(packet24.a)) // v1_6_R2
				break;

			packet24.j = CHOPPED_PITCH; // v1_6_R2

			break;

		case 34:
			final Packet34EntityTeleport packet34 = (Packet34EntityTeleport) packetRaw;
			if (!choppedEntities.contains(packet34.a)) // v1_6_R2
				break;

			packet34.f = CHOPPED_PITCH; // v1_6_R2

			break;

		case 32:
		case 33:
			final Packet30Entity packet30 = (Packet30Entity) packetRaw;
			if(!choppedEntities.contains(packet30.a)) // v1_6_R2
				break;

			packet30.f = CHOPPED_PITCH; // v1_6_R2

			break;

		case 35:
			final Packet35EntityHeadRotation packet35 = (Packet35EntityHeadRotation) packetRaw;
			if (!choppedEntities.contains(packet35.a)) // v1_6_R2
				break;

			final float yaw = getEntityByID(packet35.a, ply.getWorld()).yaw; // v1_6_R2
			packet35.b = (byte)(((yaw % 360) / 360) * 255); // v1_6_R2

			break;
		}
		return true;
	}

	public void addChoppedEntity(int entityId) {
		choppedEntities.add(entityId);
	}
}
