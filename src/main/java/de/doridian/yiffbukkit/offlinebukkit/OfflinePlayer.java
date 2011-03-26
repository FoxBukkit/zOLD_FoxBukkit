package de.doridian.yiffbukkit.offlinebukkit;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Vehicle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class OfflinePlayer implements Player {
	private Location location;
	private World world;
	private int entId = -1;
	String name;
	private String displayName;
	
	public OfflinePlayer(Server server, World world, String name) {
		displayName = this.name = name;
		this.world = world; // TEMP!
		location = world.getSpawnLocation(); // TEMP!
		//ServerConfigurationManager confmgr = ((CraftServer)server).getHandle();
		//File worldFile = ((CraftWorld)world).getHandle().u;
		//PlayerNBTManager pnm = confmgr.
		//PlayerNBTManager pnm = new PlayerNBTManager(worldFile , name, false);
		//new File(worldFile, "_tmp_.dat");
	}

	@Override
	public String getName() {
		return name;
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
		if(ignoreSneaking) {
			return 1.62D;
		} else {
			if (isSneaking()) {
				return 1.42D;
			} else {
				return 1.62D;
			}
		}
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
	public void teleportTo(Location location) {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public void teleportTo(Entity destination) {
		teleportTo(destination.getLocation());
	}
	@Override
	public int getEntityId() {
		return entId ;
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
	public Server getServer() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public void sendMessage(String message) {
		throw new UnsupportedOperationException("Player is offline");
	}
	@Override
	public boolean isOp() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}
	@Override
	public boolean isPlayer() {
		return true;
	}
	@Override
	public boolean isOnline() {
		return false;
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
		throw new UnsupportedOperationException("Player is offline");
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
	public Vector getMomentum() {
		throw new UnsupportedOperationException("Not yet implemented!");
	}

	@Override
	public void setMomentum(Vector vector) {
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
}
