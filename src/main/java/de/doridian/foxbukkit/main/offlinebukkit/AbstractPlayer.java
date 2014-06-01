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
package de.doridian.foxbukkit.main.offlinebukkit;

import de.doridian.foxbukkit.bans.FishBansResolver;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.craftbukkit.v1_7_R3.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractPlayer extends CraftOfflinePlayer implements Player {
	public AbstractPlayer(CraftServer server, UUID uuid, String name) {
		super(server, new GameProfile((uuid == null) ? FishBansResolver.getUUID(name) : uuid, name));
	}

	public AbstractPlayer(CraftServer server, String name) {
		this(server, null, name);
	}

	public AbstractPlayer(CraftServer server, UUID uuid) {
		this(server, uuid, null);
	}

	@Override public ItemStack getItemInHand() {
		return getInventory().getItemInHand();
	}
	@Override public void setItemInHand(ItemStack item) {
		getInventory().setItemInHand(item);
	}
	@Override public double getEyeHeight() {
		return getEyeHeight(false);
	}
	@Override public double getEyeHeight(boolean ignoreSneaking) {
		if (ignoreSneaking || !isSneaking())
			return 1.62D;

		return 1.42D;
	}
	@Override public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
		return getLineOfSight(transparent, maxDistance, 0);
	}

	private List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance, int maxLength) {
		if (maxDistance > 120) {
			maxDistance = 120;
		}
		ArrayList<Block> blocks = new ArrayList<>();
		Iterator<Block> itr = new BlockIterator(this, maxDistance);
		while (itr.hasNext()) {
			Block block = itr.next();
			blocks.add(block);
			if (maxLength != 0 && blocks.size() > maxLength) {
				blocks.remove(0);
			}
			int id = block.getTypeId();
			if (transparent == null) {
				if (id != 0) {
					break;
				}
			} else {
				if (!transparent.contains((byte)id)) {
					break;
				}
			}
		}
		return blocks;
	}

	@Override
	public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
		List<Block> blocks = getLineOfSight(transparent, maxDistance, 1);
		return blocks.get(0);
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
		return getLineOfSight(transparent, maxDistance, 2);
	}

	@Override
	public Location getLocation(Location location) {
		location.subtract(location);
		location.add(getLocation());
		return null;
	}

	@Override
	public boolean teleport(Entity destination) {
		return teleport(destination.getLocation());
	}

	@Override
	public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
		return teleport(location);
	}

	@Override
	public boolean teleport(Entity destination, PlayerTeleportEvent.TeleportCause cause) {
		return teleport(destination);
	}

	@Override
	public Set<String> getListeningPluginChannels() {
		return new HashSet<>();
	}

	@Override
	public boolean isValid() {
		return !isDead();
	}

	@Override public void setPlayerWeather(WeatherType weatherType) { }
	@Override public boolean isHealthScaled() { return false; }
	@Override public void setHealthScaled(boolean b) { }
	@Override public void setHealthScale(double v) throws IllegalArgumentException { }
	@Override public Spigot spigot() { return null; }
	@Override public void setLastDamage(double v) { }
	@Override public void damage(double v) { }
	@Override public void damage(double v, Entity entity) { }
	@Override public void _INVALID_damage(int v) { }
	@Override public void _INVALID_damage(int v, Entity entity) { }
	@Override public void setHealth(double v) { }
	@Override public void setMaxHealth(double v) { }
	@Override public void _INVALID_setMaxHealth(int v) { }
	@Override public Scoreboard getScoreboard() { return null; }
	@Override public void setScoreboard(Scoreboard scoreboard) { }
	@Override public WeatherType getPlayerWeather() { return null; }
	@Override public void resetPlayerWeather() { }
	@Override public boolean isOnGround() { return true; }
	@Override public void resetMaxHealth() { }
	@Override public void playEffect(EntityEffect effect) { }
	@Override public PlayerInventory getInventory() { return null; }
	@Override public Inventory getEnderChest() { return null; }
	@Override public double getHealth() { return 0; }
	@Override public int _INVALID_getHealth() { return 0; }
	@Override public void _INVALID_setHealth(int health) { }
	@Override public Egg throwEgg() { return null; }
	@Override public Snowball throwSnowball() { return null; }
	@Override public Arrow shootArrow() { return null; }
	@Override public boolean isInsideVehicle() { return false; }
	@Override public boolean leaveVehicle() { return false; }
	@Override public Vehicle getVehicle() { return null; }
	@Override public int getRemainingAir() { return 0; }
	@Override public void setRemainingAir(int ticks) { }
	@Override public int getMaximumAir() { return 0; }
	@Override public void setMaximumAir(int ticks) { }
	@Override public int getFireTicks() { return 0; }
	@Override public int getMaxFireTicks() { return 0; }
	@Override public void setFireTicks(int ticks) { }
	@Override public void remove() { }
	@Override public void sendMessage(String message) { }
	@Override public void setCompassTarget(Location loc) { }
	@Override public InetSocketAddress getAddress() { return null; }
	@Override public void kickPlayer(String message) { }
	@Override public void chat(String msg) { }
	@Override public boolean performCommand(String command) { return false; }
	@Override public boolean isSneaking() { return false; }
	@Override public void setSneaking(boolean sneak) { }
	@Override public void updateInventory() { }
	@Override public Location getEyeLocation() { return null; }
	@Override public void setVelocity(Vector velocity) { }
	@Override public Vector getVelocity() { return null; }
	@Override public int getMaximumNoDamageTicks() { return 0; }
	@Override public void setMaximumNoDamageTicks(int ticks) { }
	@Override public int _INVALID_getLastDamage() { return 0; }
	@Override public double getLastDamage() { return 0; }
	@Override public void _INVALID_setLastDamage(int damage) { }
	@Override public int getNoDamageTicks() { return 0; }
	@Override public void setNoDamageTicks(int ticks) { }
	@Override public Player getKiller() { return null; }
	@Override public boolean teleport(Location location) { return false; }
	@Override public Entity getPassenger() { return null; }
	@Override public boolean setPassenger(Entity passenger) { return false; }
	@Override public boolean isEmpty() { return false; }
	@Override public boolean eject() { return false; }
	@Override public Location getCompassTarget() { return null; }
	@Override public void sendRawMessage(String message) { }
	@Override public boolean isSleeping() { return false; }
	@Override public int getSleepTicks() { return 0; }
	@Override public List<Entity> getNearbyEntities(double x, double y, double z) { return null; }
	@Override public boolean isDead() { return false; }
	@Override public float getFallDistance() { return 0; }
	@Override public void setFallDistance(float distance) { }
	@Override public void saveData() { }
	@Override public void loadData() { }
	@Override public void setSleepingIgnored(boolean isSleeping) { }
	@Override public boolean isSleepingIgnored() { return false; }
	@Override public void awardAchievement(Achievement achievement) { }
	@Override public void incrementStatistic(Statistic statistic) { }
	@Override public void incrementStatistic(Statistic statistic, int amount) { }
	@Override public void incrementStatistic(Statistic statistic, Material material) { }
	@Override public void incrementStatistic(Statistic statistic, Material material, int amount) { }
	@Override public void setLastDamageCause(EntityDamageEvent event) { }
	@Override public EntityDamageEvent getLastDamageCause() { return null; }
	@Override public void playNote(Location loc, byte instrument, byte note) { }
	@Override public void sendBlockChange(Location loc, Material material, byte data) { }
	@Override public void sendBlockChange(Location loc, int material, byte data) { }
	@Override public long getPlayerTime() { return 0; }
	@Override public long getPlayerTimeOffset() { return 0; }
	@Override public boolean isPlayerTimeRelative() { return false; }
	@Override public void playEffect(Location arg0, Effect arg1, int arg2) { }
	@Override public void playNote(Location arg0, Instrument arg1, Note arg2) { }
	@Override public void playSound(Location location, Sound sound, float v, float v1) { }
	@Override public void resetPlayerTime() { }
	@Override public boolean sendChunkChange(Location arg0, int arg1, int arg2, int arg3, byte[] arg4) { return false; }
	@Override public void setPlayerTime(long arg0, boolean arg1) { }
	@Override public boolean isPermissionSet(String name) { return false; }
	@Override public boolean isPermissionSet(Permission perm) { return false; }
	@Override public boolean hasPermission(String name) { return false; }
	@Override public boolean hasPermission(Permission perm) { return false; }
	@Override public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) { return null; }
	@Override public PermissionAttachment addAttachment(Plugin plugin) { return null; }
	@Override public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) { return null; }
	@Override public PermissionAttachment addAttachment(Plugin plugin, int ticks) { return null; }
	@Override public void removeAttachment(PermissionAttachment attachment) { }
	@Override public void recalculatePermissions() { }
	@Override public Set<PermissionAttachmentInfo> getEffectivePermissions() { return null; }
	@Override public void sendMap(MapView map) { }
	@Override public GameMode getGameMode() { return null; }
	@Override public void setGameMode(GameMode arg0) { }
	@Override public Location getBedSpawnLocation() { return null; }
	@Override public void setBedSpawnLocation(Location location) { }
	@Override public float getExhaustion() { return 0; }
	@Override public int getFoodLevel() { return 0; }
	@Override public int getLevel() { return 0; }
	@Override public float getSaturation() { return 0; }
	@Override public int getTotalExperience() { return 0; }
	@Override public void setExhaustion(float arg0) { }
	@Override public void setFoodLevel(int arg0) { }
	@Override public void setLevel(int arg0) { }
	@Override public void setSaturation(float arg0) { }
	@Override public void setTotalExperience(int arg0) { }
	@Override public void setSprinting(boolean arg0) { }
	@Override public boolean isSprinting() { return false; }
	@Override public int getTicksLived() { return 0; }
	@Override public void setTicksLived(int value) { }
	@Override public String getPlayerListName() { return null; }
	@Override public void setPlayerListName(String name) { }
	@Override public double getMaxHealth() { return 0; }
	@Override public int _INVALID_getMaxHealth() { return 0; }
	@Override public void giveExp(int amount) { }
	@Override public float getExp() { return 0; }
	@Override public void setExp(float exp) { }
	@Override public void sendPluginMessage(Plugin plugin, String s, byte[] bytes) { }
	@Override public void setAllowFlight(boolean b) { }
	@Override public boolean getAllowFlight() { return true; }
	@Override public void hidePlayer(Player player) { }
	@Override public void showPlayer(Player player) { }
	@Override public boolean canSee(Player player) { return false; }
	@Override public boolean isFlying() { return false; }
	@Override public void setFlying(boolean b) { }
	@Override public void setFlySpeed(float v) throws IllegalArgumentException { }
	@Override public void setWalkSpeed(float v) throws IllegalArgumentException { }
	@Override public float getFlySpeed() { return 0; }
	@Override public float getWalkSpeed() { return 0; }
	@Override public void setTexturePack(String s) { }
	@Override public boolean addPotionEffect(PotionEffect effect) { return false; }
	@Override public boolean addPotionEffect(PotionEffect effect, boolean force) { return false; }
	@Override public boolean addPotionEffects(Collection<PotionEffect> effects) { return false; }
	@Override public boolean hasPotionEffect(PotionEffectType type) { return false; }
	@Override public void removePotionEffect(PotionEffectType type) { }
	@Override public Collection<PotionEffect> getActivePotionEffects() { return null; }
	@Override public boolean hasLineOfSight(Entity entity) { return false; }
	@Override public boolean getRemoveWhenFarAway() { return false; }
	@Override public void setRemoveWhenFarAway(boolean b) { }
	@Override public EntityEquipment getEquipment() { return null; }
	@Override public void setCanPickupItems(boolean b) { }
	@Override public boolean getCanPickupItems() { return false; }
	@Override public void setCustomName(String s) { }
	@Override public String getCustomName() { return null; }
	@Override public void setCustomNameVisible(boolean b) { }
	@Override public boolean isCustomNameVisible() { return false; }
	@Override public boolean isLeashed() { return false; }
	@Override public Entity getLeashHolder() throws IllegalStateException { return null; }
	@Override public boolean setLeashHolder(Entity entity) { return false; }
	@Override public void closeInventory() { }
	@Override public ItemStack getItemOnCursor() { return null; }
	@Override public InventoryView getOpenInventory() { return null; }
	@Override public InventoryView openEnchanting(Location arg0, boolean arg1) { return null; }
	@Override public InventoryView openInventory(Inventory arg0) { return null; }
	@Override public void openInventory(InventoryView arg0) { }
	@Override public InventoryView openWorkbench(Location arg0, boolean arg1) { return null; }
	@Override public void setItemOnCursor(ItemStack arg0) { }
	@Override public boolean setWindowProperty(InventoryView.Property arg0, int arg1) { return false; }
	@Override public <T extends Projectile> T launchProjectile(Class<? extends T> arg0) { return null; }
	@Override public EntityType getType() { return null; }
	@Override public void abandonConversation(Conversation arg0) { }
	@Override public void acceptConversationInput(String arg0) { }
	@Override public boolean beginConversation(Conversation arg0) { return false; }
	@Override public boolean isConversing() { return false; }
	@Override public void sendMessage(String[] arg0) { }
	@Override public <T> void playEffect(Location arg0, Effect arg1, T arg2) { }
	@Override public boolean isBlocking() { return false; }
	@Override public void abandonConversation(Conversation arg0, ConversationAbandonedEvent arg1) { }
	@Override public int getExpToLevel() { return 0; }
	@Override public void giveExpLevels(int amount) { }
	@Override public void setBedSpawnLocation(Location location, boolean force) { }
	@Override public void playSound(Location arg0, String arg1, float arg2, float arg3) { }
	@Override public void setResourcePack(String s) { }
}
