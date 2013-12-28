package de.doridian.yiffbukkit.core.util;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
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
