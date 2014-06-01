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
package de.doridian.foxbukkit.spawning.fakeentity;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public class AbstractEntity implements Entity {

	@Override
	public Server getServer() {
		return Bukkit.getServer();
	}

	@Override
	public boolean isEmpty() {
		return getPassenger() == null;
	}

	@Override
	public boolean eject() {
		return setPassenger(null);
	}

	@Override
	public boolean isValid() {
		return !isDead();
	}

	@Override
	public Spigot spigot() {
		return new Spigot();
	}

	@Override
	public boolean teleport(Entity destination) {
		return teleport(destination.getLocation());
	}

	@Override
	public Vector getVelocity() {
		return new Vector();
	}

	public void playEffect(EntityEffect effect) {
		//TODO: Implement?
	}

	@Override
	public Location getLocation(Location location) {
		location.subtract(location).add(getLocation());
		return location;
	}


	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public void setVelocity(Vector vector) { }

	@Override
	public boolean isOnGround() {
		return false;
	}

	@Override
	public World getWorld() {
		return null;
	}

	@Override
	public boolean teleport(Location location) {
		return false;
	}

	@Override
	public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause) {
		return false;
	}

	@Override
	public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause) {
		return false;
	}

	@Override
	public List<Entity> getNearbyEntities(double v, double v2, double v3) {
		return null;
	}

	@Override
	public int getEntityId() {
		return 0;
	}

	@Override
	public int getFireTicks() {
		return 0;
	}

	@Override
	public int getMaxFireTicks() {
		return 0;
	}

	@Override
	public void setFireTicks(int i) { }

	@Override
	public void remove() { }

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public Entity getPassenger() {
		return null;
	}

	@Override
	public boolean setPassenger(Entity entity) {
		return false;
	}

	@Override
	public float getFallDistance() {
		return 0;
	}

	@Override
	public void setFallDistance(float v) { }

	@Override
	public void setLastDamageCause(EntityDamageEvent entityDamageEvent) { }

	@Override
	public EntityDamageEvent getLastDamageCause() {
		return null;
	}

	@Override
	public UUID getUniqueId() {
		return null;
	}

	@Override
	public int getTicksLived() {
		return 0;
	}

	@Override
	public void setTicksLived(int i) { }

	@Override
	public EntityType getType() {
		return null;
	}

	@Override
	public boolean isInsideVehicle() {
		return false;
	}

	@Override
	public boolean leaveVehicle() {
		return false;
	}

	@Override
	public Entity getVehicle() {
		return null;
	}

	@Override
	public void setMetadata(String s, MetadataValue metadataValue) { }

	@Override
	public List<MetadataValue> getMetadata(String s) {
		return null;
	}

	@Override
	public boolean hasMetadata(String s) {
		return false;
	}

	@Override
	public void removeMetadata(String s, Plugin plugin) { }
}
