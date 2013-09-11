package de.doridian.yiffbukkit.main.offlinebukkit;

import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.craftbukkit.v1_6_R2.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
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

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class OfflinePlayer extends CraftOfflinePlayer implements Player {
	private Location location;
	private int entId = -1;
	private String displayName;
	private World world;

	@Override
	public void setPlayerWeather(WeatherType weatherType) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isHealthScaled() {
		return false;
	}

	@Override
	public void setHealthScaled(boolean b) {

	}

	@Override
	public void setHealthScale(double v) throws IllegalArgumentException {

	}

	@Override
	public double getHealthScale() {
		return 1;
	}

	@Override
	public Spigot spigot() {
		return null;
	}

	public void setLastDamage(double v) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void damage(double v) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void damage(double v, Entity entity) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void _INVALID_damage(int v) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void _INVALID_damage(int v, Entity entity) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void setHealth(double v) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void setMaxHealth(double v) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void _INVALID_setMaxHealth(int v) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Scoreboard getScoreboard() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setScoreboard(Scoreboard scoreboard) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public WeatherType getPlayerWeather() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void resetPlayerWeather() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isOnGround() {
		return true;
	}

    @Override
    public void resetMaxHealth() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public OfflinePlayer(Server server, String name) {
		super((CraftServer) server, name);
		final File playerFile = PlayerHelper.getPlayerFile(name, "world");
		if (playerFile != null) {
			final String playerFileName = playerFile.getName();
			name = playerFileName.substring(0, playerFileName.length() - 4);
		}

		displayName = name;
		world = server.getWorld("world"); // TEMP!
		location = world.getSpawnLocation(); // TEMP!
		//ServerConfigurationManager confmgr = ((CraftServer)server).getHandle();
		//File worldFile = ((CraftWorld)world).getHandle().u;
		//PlayerNBTManager pnm = confmgr.
		//PlayerNBTManager pnm = new PlayerNBTManager(worldFile , name, false);
		//new File(worldFile, "_tmp_.dat");
	}

	@Override
	public void playEffect(EntityEffect effect) {
		//TODO: Maybe implement?
	}

	@Override
	public PlayerInventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Inventory getEnderChest() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ItemStack getItemInHand() {
		return getInventory().getItemInHand();
	}
	@Override
	public void setItemInHand(ItemStack item) {
		getInventory().setItemInHand(item);
	}


	public double getHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int _INVALID_getHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void _INVALID_setHealth(int health) {
		// TODO Auto-generated method stub
	}

	@Override
	public double getEyeHeight() {
		return getEyeHeight(false);
	}
	@Override
	public double getEyeHeight(boolean ignoreSneaking) {
		if (ignoreSneaking || !isSneaking())
			return 1.62D;

		return 1.42D;
	}
	@Override
	public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
		return getLineOfSight(transparent, maxDistance, 0);
	}
	private List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance, int maxLength) {
		if (maxDistance > 120) {
			maxDistance = 120;
		}
		ArrayList<Block> blocks = new ArrayList<Block>();
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
	public Egg throwEgg() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Snowball throwSnowball() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Arrow shootArrow() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isInsideVehicle() {
		return false;
	}
	@Override
	public boolean leaveVehicle() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Vehicle getVehicle() {
		return null;
	}
	@Override
	public int getRemainingAir() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void setRemainingAir(int ticks) {
		// TODO Auto-generated method stub
	}
	@Override
	public int getMaximumAir() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void setMaximumAir(int ticks) {
		// TODO Auto-generated method stub
	}
	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public Location getLocation(Location location) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public World getWorld() {
		return world;
	}
	@Override
	public int getEntityId() {
		return entId;
	}
	@Override
	public int getFireTicks() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getMaxFireTicks() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void setFireTicks(int ticks) {
		// TODO Auto-generated method stub
	}
	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}
	@Override
	public void sendMessage(String message) {
		// TODO Auto-generated method stub
	}
	@Override
	public String getDisplayName() {
		return displayName;
	}
	@Override
	public void setDisplayName(String name) {
		displayName = name;
	}
	@Override
	public void setCompassTarget(Location loc) {
		// TODO Auto-generated method stub
	}
	@Override
	public InetSocketAddress getAddress() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void kickPlayer(String message) {
		// TODO Auto-generated method stub
	}
	@Override
	public void chat(String msg) {
		// TODO Auto-generated method stub
	}
	@Override
	public boolean performCommand(String command) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isSneaking() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setSneaking(boolean sneak) {
		// TODO Auto-generated method stub
	}
	@Override
	public void updateInventory() {
		// TODO Auto-generated method stub
	}

	@Override
	public Location getEyeLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVelocity(Vector velocity) {
		// TODO Auto-generated method stub
	}

	@Override
	public Vector getVelocity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaximumNoDamageTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMaximumNoDamageTicks(int ticks) {
		// TODO Auto-generated method stub
	}

	public int _INVALID_getLastDamage() {
		return 0;
	}

	public double getLastDamage() {
		return 0;
	}

	public void _INVALID_setLastDamage(int damage) {
		// TODO Auto-generated method stub
	}


	@Override
	public int getNoDamageTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setNoDamageTicks(int ticks) {
		// TODO Auto-generated method stub
	}

	@Override
	public Player getKiller() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean teleport(Location location) {
		//// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean teleport(Entity destination) {
		return teleport(destination.getLocation());
	}

	@Override
	public Entity getPassenger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setPassenger(Entity passenger) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eject() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Location getCompassTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendRawMessage(String message) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isSleeping() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSleepTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDead() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getFallDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFallDistance(float distance) {
		// TODO Auto-generated method stub
	}

	@Override
	public void saveData() {
		// TODO Auto-generated method stub
	}

	@Override
	public void loadData() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setSleepingIgnored(boolean isSleeping) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isSleepingIgnored() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void awardAchievement(Achievement achievement) {
		// TODO Auto-generated method stub
	}

	@Override
	public void incrementStatistic(Statistic statistic) {
		// TODO Auto-generated method stub
	}

	@Override
	public void incrementStatistic(Statistic statistic, int amount) {
		// TODO Auto-generated method stub
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material) {
		// TODO Auto-generated method stub
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material, int amount) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public EntityDamageEvent getLastDamageCause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void playNote(Location loc, byte instrument, byte note) {
		// TODO Auto-generated method stub
	}

	@Override
	public void sendBlockChange(Location loc, Material material, byte data) {
		// TODO Auto-generated method stub
	}

	@Override
	public void sendBlockChange(Location loc, int material, byte data) {
		// TODO Auto-generated method stub
	}

	@Override
	public UUID getUniqueId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getPlayerTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPlayerTimeOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isPlayerTimeRelative() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void playEffect(Location arg0, Effect arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void playNote(Location arg0, Instrument arg1, Note arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void playSound(Location location, Sound sound, float v, float v1) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void resetPlayerTime() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean sendChunkChange(Location arg0, int arg1, int arg2, int arg3, byte[] arg4) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPlayerTime(long arg0, boolean arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isPermissionSet(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPermission(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPermission(Permission perm) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		// TODO Auto-generated method stub
	}

	@Override
	public void recalculatePermissions() {
		// TODO Auto-generated method stub
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendMap(MapView map) {
		// TODO Auto-generated method stub
	}

	@Override
	public GameMode getGameMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGameMode(GameMode arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public Location getBedSpawnLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBedSpawnLocation(Location location) {
		// TODO Auto-generated method stub
	}

	@Override
	public float getExhaustion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFoodLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getSaturation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalExperience() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setExhaustion(float arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setFoodLevel(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setLevel(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setSaturation(float arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setTotalExperience(int arg0) {
		// TODO Auto-generated method stub
	}
	
	public void setSprinting(boolean arg0) {
		// TODO Auto-generated method stub
	}
	
	public boolean isSprinting() {
		return false;
	}

	@Override
	public int getTicksLived() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTicksLived(int value) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getPlayerListName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPlayerListName(String name) {
		// TODO Auto-generated method stub
	}

	public double getMaxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int _INVALID_getMaxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void giveExp(int amount) {
		// TODO Auto-generated method stub
	}

	@Override
	public float getExp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setExp(float exp) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean teleport(Location location, TeleportCause cause) {
		return teleport(location);
	}

	@Override
	public boolean teleport(Entity destination, TeleportCause cause) {
		return teleport(destination);
	}

	@Override
	public void sendPluginMessage(Plugin plugin, String s, byte[] bytes) {
		// TODO Auto-generated method stub
	}

	@Override
	public Set<String> getListeningPluginChannels() {
		return new HashSet<String>();
	}

	@Override
	public void setAllowFlight(boolean b) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean getAllowFlight() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void hidePlayer(Player player) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showPlayer(Player player) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean canSee(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFlying() {
		return false;
	}

	@Override
	public void setFlying(boolean b) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setFlySpeed(float v) throws IllegalArgumentException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setWalkSpeed(float v) throws IllegalArgumentException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public float getFlySpeed() {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public float getWalkSpeed() {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

    @Override
    public void setTexturePack(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
	public boolean addPotionEffect(PotionEffect effect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addPotionEffect(PotionEffect effect, boolean force) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addPotionEffects(Collection<PotionEffect> effects) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removePotionEffect(PotionEffectType type) {
		// TODO Auto-generated method stub
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasLineOfSight(Entity entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getRemoveWhenFarAway() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setRemoveWhenFarAway(boolean b) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public EntityEquipment getEquipment() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setCanPickupItems(boolean b) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean getCanPickupItems() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setCustomName(String s) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public String getCustomName() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setCustomNameVisible(boolean b) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isCustomNameVisible() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isLeashed() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Entity getLeashHolder() throws IllegalStateException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean setLeashHolder(Entity entity) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
	}

	@Override
	public ItemStack getItemOnCursor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InventoryView getOpenInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InventoryView openEnchanting(Location arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InventoryView openInventory(Inventory arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void openInventory(InventoryView arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public InventoryView openWorkbench(Location arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setItemOnCursor(ItemStack arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean setWindowProperty(Property arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void abandonConversation(Conversation arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void acceptConversationInput(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean beginConversation(Conversation arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConversing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void sendMessage(String[] arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public <T> void playEffect(Location arg0, Effect arg1, T arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isBlocking() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void abandonConversation(Conversation arg0, ConversationAbandonedEvent arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getExpToLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isValid() {
		return !isDead();
	}

	@Override
	public void giveExpLevels(int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBedSpawnLocation(Location location, boolean force) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playSound(Location arg0, String arg1, float arg2, float arg3) {
		// TODO Auto-generated method stub
		
	}
}
