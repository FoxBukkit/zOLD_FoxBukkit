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
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftOfflinePlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
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
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public ItemStack getItemInHand() {
		return getInventory().getItemInHand();
	}
	@Override
	public void setItemInHand(ItemStack item) {
		getInventory().setItemInHand(item);
	}
	@Override
	public int getHealth() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public void setHealth(int health) {
		throw new UnsupportedOperationException("Not yet implemented!");
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
		throw new UnsupportedOperationException("Player is offline");
	}
	@Override
	public Snowball throwSnowball() {
		throw new UnsupportedOperationException("Player is offline");
	}
	@Override
	public Arrow shootArrow() {
		throw new UnsupportedOperationException("Player is offline");
	}
	@Override
	public boolean isInsideVehicle() {
		return false;
	}
	@Override
	public boolean leaveVehicle() {
		throw new UnsupportedOperationException("Player is offline");
	}
	@Override
	public Vehicle getVehicle() {
		return null;
	}
	@Override
	public int getRemainingAir() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public void setRemainingAir(int ticks) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public int getMaximumAir() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public void setMaximumAir(int ticks) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public Location getLocation() {
		return location;
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
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public int getMaxFireTicks() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public void setFireTicks(int ticks) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public void remove() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public void sendMessage(String message) {
		throw new UnsupportedOperationException("Player is offline");
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
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public InetSocketAddress getAddress() {
		throw new UnsupportedOperationException("Player is offline");
	}
	@Override
	public void kickPlayer(String message) {
		//throw new UnsupportedOperationException("Player is offline");
	}
	@Override
	public void chat(String msg) {
		throw new UnsupportedOperationException("Player is offline");
	}
	@Override
	public boolean performCommand(String command) {
		throw new UnsupportedOperationException("Player is offline");
	}
	@Override
	public boolean isSneaking() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public void setSneaking(boolean sneak) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public void updateInventory() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public Location getEyeLocation() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void damage(int amount) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void damage(int amount, Entity source) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void setVelocity(Vector velocity) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public Vector getVelocity() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public int getMaximumNoDamageTicks() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void setMaximumNoDamageTicks(int ticks) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public int getLastDamage() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void setLastDamage(int damage) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public int getNoDamageTicks() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void setNoDamageTicks(int ticks) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public Player getKiller() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public boolean teleport(Location location) {
		//throw new UnsupportedOperationException("Not yet implemented!");
		return false;
	}

	@Override
	public boolean teleport(Entity destination) {
		return teleport(destination.getLocation());
	}

	@Override
	public Entity getPassenger() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public boolean setPassenger(Entity passenger) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public boolean eject() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public Location getCompassTarget() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void sendRawMessage(String message) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public boolean isSleeping() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public int getSleepTicks() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public boolean isDead() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public float getFallDistance() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void setFallDistance(float distance) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void saveData() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void loadData() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void setSleepingIgnored(boolean isSleeping) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public boolean isSleepingIgnored() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void awardAchievement(Achievement achievement) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void incrementStatistic(Statistic statistic) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void incrementStatistic(Statistic statistic, int amount) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material, int amount) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent event) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public EntityDamageEvent getLastDamageCause() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void playNote(Location loc, byte instrument, byte note) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void sendBlockChange(Location loc, Material material, byte data) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void sendBlockChange(Location loc, int material, byte data) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public UUID getUniqueId() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public long getPlayerTime() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public long getPlayerTimeOffset() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public boolean isPlayerTimeRelative() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void playEffect(Location arg0, Effect arg1, int arg2) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void playNote(Location arg0, Instrument arg1, Note arg2) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void resetPlayerTime() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public boolean sendChunkChange(Location arg0, int arg1, int arg2, int arg3,
			byte[] arg4) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void setPlayerTime(long arg0, boolean arg1) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public boolean isPermissionSet(String name) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public boolean hasPermission(String name) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public boolean hasPermission(Permission perm) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void recalculatePermissions() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void sendMap(MapView map) {
		throw new UnsupportedOperationException("Not yet implemented!");
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
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public float getExhaustion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getExperience() {
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
	public void setExperience(int arg0) {
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

	@Override
	public int getMaxHealth() {
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean teleport(Entity destination, TeleportCause cause) {
		// TODO Auto-generated method stub
		return false;
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
}
