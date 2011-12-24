package de.doridian.yiffbukkit.transmute;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.scheduler.BukkitScheduler;

import de.doridian.yiffbukkit.YiffBukkit;

public class Transmute implements Runnable {
	final YiffBukkit plugin;
	private final TransmutePacketListener transmutePacketListener;
	@SuppressWarnings("unused")
	private TransmutePlayerListener transmutePlayerListener;
	private final Map<Integer, Shape> transmuted = new HashMap<Integer, Shape>();

	public Transmute(YiffBukkit plugin) {
		this.plugin = plugin;
		transmutePacketListener = new TransmutePacketListener(this);
		transmutePlayerListener = new TransmutePlayerListener(this);

		final BukkitScheduler scheduler = plugin.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				// clean up ignored packets
				long minTimestamp = System.currentTimeMillis() - 1000;

				for (Iterator<Packet> iterator = transmutePacketListener.ignoredPackets.iterator(); iterator.hasNext(); ) {
					final Packet packet = iterator.next();

					if (((net.minecraft.server.Packet) packet).timestamp < minTimestamp)
						iterator.remove();
				}

				// clean up transmuted entities
				for (Iterator<Entry<Integer, Shape>> iterator = transmuted.entrySet().iterator(); iterator.hasNext(); ) {
					final Entry<Integer, Shape> entry = iterator.next();
					final Shape shape = entry.getValue();

					if (shape.entity.isDead())
						iterator.remove();
				}
			}
		}, 0, 200);

		scheduler.scheduleSyncRepeatingTask(plugin, this, 0, 1);
	}

	public boolean isTransmuted(int entityId) {
		return transmuted.containsKey(entityId);
	}

	public boolean isTransmuted(Entity entity) {
		return transmuted.containsKey(entity.getEntityId());
	}

	public boolean isTransmuted(net.minecraft.server.Entity entity) {
		return transmuted.containsKey(entity.id);
	}

	public Shape getShape(int entityId) {
		return transmuted.get(entityId);
	}

	public Shape getShape(Entity entity) {
		return transmuted.get(entity.getEntityId());
	}

	public Shape getShape(net.minecraft.server.Entity entity) {
		return transmuted.get(entity.id);
	}

	public void setShape(Entity entity, final Shape shape) {
		if (shape.entity != entity)
			throw new IllegalArgumentException("Assigned a shape to the wrong entity!");

		transmuted.put(entity.getEntityId(), shape);
		shape.deleteEntity();
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() {
			shape.createTransmutedEntity();
			shape.reattachPassenger();
		}}, 2);

		if (entity instanceof Player && shape instanceof EntityShape) {
			final EntityShape entityShape = (EntityShape) shape;
			int mobType = entityShape.mobType;
			try {
				String typeName = MyEntityTypes.classToTypeName(MyEntityTypes.idToClass(mobType));
				plugin.playerHelper.sendYiffcraftClientCommand((Player) entity, 't', typeName+"|"+entityShape.yawOffset+"|"+entityShape.yOffset);
			}
			catch (EntityTypeNotFoundException e) {
			}
		}
	}

	public void setShape(Player player, Entity entity, int mobType) throws EntityTypeNotFoundException {
		setShape(entity, Shape.getShape(this, player, entity, mobType));
	}

	public void setShape(Player player, Entity entity, String mobType) throws EntityTypeNotFoundException {
		setShape(entity, Shape.getShape(this, player, entity, mobType));
	}

	public Shape resetShape(Entity entity) {
		final Shape shape = removeShape(entity);
		if (shape != null) {
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() {
				shape.createOriginalEntity();
				shape.reattachPassenger();
			}}, 2);
		}

		if (entity instanceof Player)
			plugin.playerHelper.sendYiffcraftClientCommand((Player) entity, 't', "");

		return shape;
	}

	public Shape removeShape(Entity entity) {
		Shape shape = transmuted.remove(entity.getEntityId());
		if (shape != null)
			shape.deleteEntity();

		return shape;
	}

	org.bukkit.event.server.Packet ignorePacket(org.bukkit.event.server.Packet packet) {
		transmutePacketListener.ignoredPackets.add(packet);
		return packet;
	}

	@Override
	public void run() {
		for (Iterator<Entry<Integer, Shape>> iterator = transmuted.entrySet().iterator(); iterator.hasNext(); ) {
			iterator.next().getValue().tick();
		}
	}
}
