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
package de.doridian.foxbukkit.core.util;

import de.doridian.foxbukkit.main.listeners.BaseListener;
import gnu.trove.TDecorators;
import gnu.trove.set.TIntSet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class AutoCleanup extends BaseListener {
	private static final Set<Set<Player>> registeredPlayerSets = Collections.newSetFromMap(new IdentityHashMap<Set<Player>, Boolean>());
	private static final Set<Set<Entity>> registeredEntitySets = Collections.newSetFromMap(new IdentityHashMap<Set<Entity>, Boolean>());
	private static final Set<Set<Integer>> registeredEntityIdSets = Collections.newSetFromMap(new IdentityHashMap<Set<Integer>, Boolean>());


	public static void registerPlayerSet(Set<Player> set) {
		registeredPlayerSets.add(set);
	}

	public static void registerPlayerMap(Map<Player, ?> map) {
		registerPlayerSet(map.keySet());
	}


	public static void registerEntitySet(Set<Entity> set) {
		registeredEntitySets.add(set);
	}

	public static void registerEntityMap(Map<Entity,?> map) {
		registerEntitySet(map.keySet());
	}


	public static void registerEntityIdSet(Set<Integer> set) {
		registeredEntityIdSets.add(set);
	}

	public static void registerEntityIdMap(Map<Integer,?> map) {
		registerEntityIdSet(map.keySet());
	}

	public static void registerEntityIdSet(TIntSet set) {
		registerEntityIdSet(TDecorators.wrap(set));
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {
		handleEntityEvent(event);
	}

	/* TODO:
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityRemoved(EntityRemovedEvent event) {
		handleEntityEvent(event);
	}
	*/


	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		handlePlayerEvent(event);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) {
		handlePlayerEvent(event);
	}


	private void handleEntityEvent(EntityDeathEvent event) {
		final Entity entity = event.getEntity();

		for (Set<Player> set : registeredPlayerSets) {
			//noinspection SuspiciousMethodCalls
			set.remove(entity);
		}

		for (Set<Entity> set : registeredEntitySets) {
			set.remove(entity);
		}

		for (Set<Integer> set : registeredEntityIdSets) {
			set.remove(entity.getEntityId());
		}
	}

	private void handlePlayerEvent(PlayerEvent event) {
		final Player player = event.getPlayer();

		for (Set<Player> set : registeredPlayerSets) {
			set.remove(player);
		}

		for (Set<Entity> set : registeredEntitySets) {
			set.remove(player);
		}

		for (Set<Integer> set : registeredEntityIdSets) {
			set.remove(player.getEntityId());
		}
	}
}
