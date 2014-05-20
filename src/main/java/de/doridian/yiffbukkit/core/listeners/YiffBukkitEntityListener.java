/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.yiffbukkit.core.listeners;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class YiffBukkitEntityListener extends BaseListener {
	Map<String, String> lastAttacker = new HashMap<>();

	Map<EntityType, String> monsterMap = new EnumMap<>(EntityType.class);
	{
		monsterMap.put(EntityType.CREEPER, "a \u00a79creeper\u00a7f");
		monsterMap.put(EntityType.GIANT, "a \u00a79giant\u00a7f");
		monsterMap.put(EntityType.SKELETON, "a \u00a79skeleton\u00a7f");
		monsterMap.put(EntityType.SPIDER, "a \u00a79spider\u00a7f");
		monsterMap.put(EntityType.ZOMBIE, "a \u00a79zombie\u00a7f");
		monsterMap.put(EntityType.PIG_ZOMBIE, "a \u00a79pig zombie\u00a7f");
		monsterMap.put(EntityType.WOLF, "a \u00a79wolf\u00a7f");
		monsterMap.put(EntityType.SLIME, "a \u00a79slime\u00a7f");
		monsterMap.put(EntityType.GHAST, "a \u00a79ghast\u00a7f");
		monsterMap.put(EntityType.ENDERMAN, "an \u00a79enderman\u00a7f");
		monsterMap.put(EntityType.SILVERFISH, "a \u00a79silverfish\u00a7f");
		monsterMap.put(EntityType.CAVE_SPIDER, "a \u00a79cave spider\u00a7f");
		monsterMap.put(EntityType.ENDER_DRAGON, "an \u00a79ender dragon\u00a7f");
		monsterMap.put(EntityType.BLAZE, "a \u00a79blaze\u00a7f");
		monsterMap.put(EntityType.MAGMA_CUBE, "a \u00a79lava slime\u00a7f");
		monsterMap.put(EntityType.IRON_GOLEM, "a \u00a79golem\u00a7f");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		final Entity ent = event.getEntity();

		if (!(ent instanceof Player))
			return;

		final Player ply = (Player)ent;


		final String playerName = ply.getName();

		EntityDamageEvent damageEvent = ent.getLastDamageCause();
		EntityDamageEvent.DamageCause damageCause = null;
		if(damageEvent != null) damageCause = damageEvent.getCause();
		if(damageCause == null) damageCause = EntityDamageEvent.DamageCause.CUSTOM;
		final String deathMessage;
		switch (damageCause) {
		case BLOCK_EXPLOSION:
			deathMessage = "\u00a7c%s\u00a7f exploded.";
			break;

		case CONTACT:
			deathMessage = "\u00a7c%s\u00a7f is not a fakir.";
			break;

		case DROWNING:
			deathMessage = "\u00a7c%s\u00a7f can't hold breath for 10 minutes.";
			break;

		case ENTITY_ATTACK: {
			if (!(damageEvent instanceof EntityDamageByEntityEvent)) {
				deathMessage = "\u00a7c%s\u00a7f was killed.";
				break;
			}

			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent)damageEvent;
			Entity damager = edbee.getDamager();

			if (damager == null) {
				deathMessage = "\u00a7c%s\u00a7f was killed.";
				break;
			}

			String damagerName = monsterMap.get(damager.getType());
			if (damagerName == null) {
				if (damager instanceof Player)
					damagerName = ((Player) damager).getName();
				else
					damagerName = "\u00a79"+damager.toString()+"\u00a7f";
			}

			deathMessage = "\u00a7c%s\u00a7f was killed by "+damagerName+".";
			break;
		}

		case ENTITY_EXPLOSION:
			deathMessage = "\u00a7c%s\u00a7f exploded.";
			break;

		case FALL:
			deathMessage = "\u00a7c%s\u00a7f cratered.";
			break;

		case FIRE:
		case FIRE_TICK:
			deathMessage = "\u00a7c%s\u00a7f played with fire.";
			break;

		case LAVA:
			deathMessage = "\u00a7c%s\u00a7f went looking for diamonds in the wrong place.";
			break;

		case LIGHTNING:
			deathMessage = "\u00a7c%s\u00a7f angered the gods.";
			break;

		case PROJECTILE:
			deathMessage = "\u00a7c%s\u00a7f was not fast enough.";
			break;

		case SUICIDE:
			deathMessage = "\u00a7c%s\u00a7f brought shame to family.";
			break;

		case SUFFOCATION:
			deathMessage = "\u00a7c%s\u00a7f got a Mafia funeral.";
			break;

		case VOID:
			deathMessage = "\u00a7c%s\u00a7f dug too deep.";
			break;

		default:
			deathMessage = "\u00a7c%s\u00a7f died.";
		}

		lastAttacker.remove(playerName);
		((PlayerDeathEvent) event).setDeathMessage(String.format(deathMessage, playerName));
	}
}
