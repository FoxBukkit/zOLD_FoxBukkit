/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.foxbukkit.transmute;

import de.doridian.foxbukkit.core.FoxBukkit;
import de.doridian.foxbukkit.core.util.AutoCleanup;
import de.doridian.foxbukkit.transmute.listeners.TransmutePacketListener;
import de.doridian.foxbukkit.transmute.listeners.TransmutePlayerListener;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import net.minecraft.server.v1_7_R3.Packet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Transmute implements Runnable {
	final FoxBukkit plugin;
	private final TransmutePacketListener transmutePacketListener;
	private final TIntObjectMap<Shape> transmuted = new TIntObjectHashMap<>();
	private Map<Player, Entity> lastEntities = new HashMap<>();

	public Transmute(FoxBukkit plugin) {
		this.plugin = plugin;
		transmutePacketListener = new TransmutePacketListener(this);
		new TransmutePlayerListener(this);

		final BukkitScheduler scheduler = plugin.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				// clean up ignored packets
				long minTimestamp = System.currentTimeMillis() - 1000;

				for (Iterator<Packet> iterator = transmutePacketListener.ignoredPackets.iterator(); iterator.hasNext(); ) {
					final Packet packet = iterator.next();

					if (packet.timestamp < minTimestamp)
						iterator.remove();
				}

				// clean up transmuted entities
				try {
					transmuted.forEachEntry(new TIntObjectProcedure<Shape>() {
						@Override
						public boolean execute(int i, Shape shape) {
							if (shape.entity.isDead())
								transmuted.remove(i);
							return true;
						}
					});
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}, 0, 200);

		scheduler.scheduleSyncRepeatingTask(plugin, this, 0, 1);

		AutoCleanup.registerPlayerMap(lastEntities);
	}

	public boolean isTransmuted(int entityId) {
		return transmuted.containsKey(entityId);
	}

	public boolean isTransmuted(Entity entity) {
		return transmuted.containsKey(entity.getEntityId());
	}

	public boolean isTransmuted(net.minecraft.server.v1_7_R3.Entity entity) {
		return transmuted.containsKey(entity.id);
	}

	public Shape getShape(int entityId) {
		return transmuted.get(entityId);
	}

	public Shape getShape(Entity entity) {
		return transmuted.get(entity.getEntityId());
	}

	public Shape getShape(net.minecraft.server.v1_7_R3.Entity entity) {
		return transmuted.get(entity.id);
	}

	public Shape setShape(Player player, Entity entity, final Shape shape) {
		if (shape.entity != entity)
			throw new IllegalArgumentException("Assigned a shape to the wrong entity!");

		transmuted.put(entity.getEntityId(), shape);
		shape.deleteEntity();
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() {
			shape.createTransmutedEntity();
			shape.reattachPassenger();
		}}, 2);

		lastEntities.put(player, entity);

		return shape;
	}

	public Shape setShape(Player player, Entity entity, int mobType) throws EntityTypeNotFoundException {
		return setShape(player, entity, Shape.getShape(this, entity, mobType));
	}

	public Shape setShape(Player player, Entity entity, String mobType) throws EntityTypeNotFoundException {
		return setShape(player, entity, Shape.getShape(this, entity, mobType));
	}

	public Shape resetShape(Player player, Entity entity) {
		final Shape shape = removeShape(entity);
		if (shape != null) {
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() {
				shape.createOriginalEntity();
				shape.reattachPassenger();
			}}, 2);
		}

		lastEntities.put(player, entity);

		return shape;
	}

	public Shape removeShape(Entity entity) {
		Shape shape = transmuted.remove(entity.getEntityId());
		if (shape != null)
			shape.deleteEntity();

		return shape;
	}

	Packet ignorePacket(Packet packet) {
		if(packet == null)
			throw new NullPointerException();
		transmutePacketListener.ignoredPackets.add(packet);
		return packet;
	}

	public Entity getLastTransmutedEntity(Player ply) {
		return lastEntities.get(ply);
	}

	@Override
	public void run() {
		transmuted.forEachValue(new TObjectProcedure<Shape>() {
			@Override
			public boolean execute(Shape shape) {
				shape.tick();
				return true;
			}
		});
	}
}
