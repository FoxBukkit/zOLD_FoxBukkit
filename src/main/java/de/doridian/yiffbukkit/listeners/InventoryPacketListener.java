package de.doridian.yiffbukkit.listeners;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.server.Container;
import net.minecraft.server.ContainerChest;
import net.minecraft.server.ContainerDispenser;
import net.minecraft.server.ContainerFurnace;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import net.minecraft.server.Packet102WindowClick;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityFurnace;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import de.diddiz.LogBlock.Consumer;
import de.diddiz.util.BukkitUtils;
import de.doridian.yiffbukkit.YiffBukkit;

public class InventoryPacketListener extends PacketListener {
	@SuppressWarnings("unused")
	private final YiffBukkit plugin;
	private final Consumer consumer;

	private final Map<Player, Location> activeLocations = new HashMap<Player, Location>();
	private final Set<Player> activeInventories = new HashSet<Player>();

	public InventoryPacketListener(YiffBukkit plugin) {
		this.plugin = plugin;
		this.consumer = plugin.logBlockConsumer;

		plugin.playerHelper.registerMap(activeLocations);
		plugin.playerHelper.registerSet(activeInventories);

		addPacketListener(false, 101, this, plugin);
		addPacketListener(false, 102, this, plugin);

		PlayerListener playerListener = new PlayerListener() {
			@Override
			public void onPlayerInteract(PlayerInteractEvent event) {
				if (event.isCancelled()) {
					return;
				}
				if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
					return;
				}

				final Player player = event.getPlayer();
				final Block clickedBlock = event.getClickedBlock();
				if (clickedBlock != null) {
					Material type = clickedBlock.getType();
					if (type == Material.CHEST || type == Material.DISPENSER || type == Material.WORKBENCH || type == Material.FURNACE) {
						activeLocations.put(player, clickedBlock.getLocation());
					}
				}
			}
		};

		final PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Lowest, plugin);
	}
	@Override
	public boolean onIncomingPacket(Player ply, int packetID, Packet packet) {
		Location activeLocation = activeLocations.get(ply);
		boolean activeInventory = activeInventories.contains(ply);
		EntityPlayer eply = ((CraftPlayer)ply).getHandle();
		CraftInventory inventory = getActiveInventory(eply);
		boolean cancelled = false;
		switch (packetID) {
		case 101:
			cancelled = inventoryCloseEvent(ply, inventory, getInventoryFromContainer(eply, eply.defaultContainer), activeLocation);
			activeLocations.remove(ply);
			break;

		case 102:
			Packet102WindowClick p102 = (Packet102WindowClick) packet;
			if (eply.activeContainer.windowId != p102.a || !eply.activeContainer.c(eply))
				break;

			//alert of a newly opened inventory
			if (!activeInventory) {
				activeInventory = true;
				cancelled = inventoryOpenEvent(ply, inventory, getDefaultInventory(eply), activeLocation);
				if (cancelled) {
					eply.y();
					activeInventories.remove(ply);
					activeLocations.remove(ply);
					break;
				}
			}

			/*
			// Fire InventoryChange or InventoryCraft event
			if (p102.b != -999) {
				if (inventory instanceof CraftingInventory) {
					CraftingInventory crafting = (CraftingInventory) inventory;
					InventoryCrafting recipe = (InventoryCrafting) crafting.getMatrixHandle();

					ContribCraftItemStack craftResult = ContribCraftItemStack.fromItemStack(CraftingManager.getInstance().craft(recipe));
					ContribCraftItemStack[] recipeContents = new ContribCraftItemStack[recipe.getSize()];
					for (int i = 0; i < recipe.getSize(); i++) {
						org.bukkit.inventory.ItemStack temp = crafting.getMatrix()[i];
						recipeContents[i] = temp == null ? null : new ContribCraftItemStack(temp.getTypeId(), temp.getAmount(), temp.getDurability());
					}

					ContribCraftItemStack[][] matrix = null;
					if (recipe.getSize() == 4) {
						matrix = new ContribCraftItemStack[][] {
								Arrays.copyOfRange(recipeContents, 0, 2),
								Arrays.copyOfRange(recipeContents, 2, 4)
						};
					}
					else if (recipe.getSize() == 9) {
						matrix = new ContribCraftItemStack[][] {
								Arrays.copyOfRange(recipeContents, 0, 3),
								Arrays.copyOfRange(recipeContents, 3, 6),
								Arrays.copyOfRange(recipeContents, 6, 9)
						};
					}
					//Clicking to grab the crafting result
					if (type == InventorySlotType.RESULT) {
						InventoryCraftEvent craftEvent = new InventoryCraftEvent(this.getPlayer(), crafting, this.activeLocation, type, p102.b, matrix, craftResult, cursor, p102.c == 0, p102.f);
						Bukkit.getServer().getPluginManager().callEvent(craftEvent);
						craftEvent.getInventory().setResult(craftEvent.getResult());
						cursor = craftEvent.getCursor() == null ? null : new ContribCraftItemStack(craftEvent.getCursor().getTypeId(), craftEvent.getCursor().getAmount(), craftEvent.getCursor().getDurability());
						if (craftEvent.isCancelled()) {
							craftEvent.getInventory().setMatrix(recipeContents);
							setCursorSlot(cursor != null ? cursor.getHandle() : null);
							clickSuccessful = false;
						}
					}
				}
			}

			if (clickSuccessful) {
				clickSuccessful = handleInventoryClick(p102, type, slot, cursor, inventory);
			}

			if (clickSuccessful) {
				eply.netServerHandler.sendPacket(new Packet106Transaction(windowId, p102.d, true));
				eply.h = true;
				eply.activeContainer.a();
				eply.z();
				eply.h = false;
			}
			else {
				this.n.put(Integer.valueOf(eply.activeContainer.windowId), Short.valueOf(p102.d));
				eply.netServerHandler.sendPacket(new Packet106Transaction(windowId, p102.d, false));
				eply.activeContainer.a(eply, false);
				ArrayList<ItemStack> arraylist = new ArrayList<ItemStack>();

				for (int i = 0; i < eply.activeContainer.e.size(); ++i) {
					arraylist.add(((Slot) eply.activeContainer.e.get(i)).getItem());
				}

				eply.a(eply.activeContainer, arraylist);
			}
			 */
			break;
		}

		return !cancelled;
	}

	private static final CraftInventory getActiveInventory(EntityPlayer eply) {
		return getInventoryFromContainer(eply, eply.activeContainer);
	}

	private static final CraftInventory getDefaultInventory(EntityPlayer eply) {
		return getInventoryFromContainer(eply, eply.defaultContainer);
	}

	private static final CraftInventory getInventoryFromContainer(EntityPlayer eply, Container container) {
		try {
			if (container instanceof ContainerChest) {
				Field a = ContainerChest.class.getDeclaredField("a");
				a.setAccessible(true);
				return new CraftInventory((IInventory) a.get((ContainerChest)container));
			}
			/*if (container instanceof ContainerPlayer) {
				return new CraftInventoryPlayer(eply.inventory, new CraftingInventory(((ContainerPlayer)container).craftInventory, ((ContainerPlayer)container).resultInventory));
			}*/
			if (container instanceof ContainerFurnace) {
				Field a = ContainerFurnace.class.getDeclaredField("a");
				a.setAccessible(true);
				return new CraftInventory((TileEntityFurnace)a.get((ContainerFurnace)container));
			}
			if (container instanceof ContainerDispenser) {
				Field a = ContainerDispenser.class.getDeclaredField("a");
				a.setAccessible(true);
				return new CraftInventory((TileEntityDispenser)a.get((ContainerDispenser)container));
			}
			/*if (container instanceof ContainerWorkbench) {
				return new CraftingInventory(((ContainerWorkbench)container).craftInventory, ((ContainerWorkbench)container).resultInventory);
			}*/
		}
		catch (Exception e) {
			e.printStackTrace();
			return new CraftInventory(eply.inventory);
		}
		return null;
	}

	private final Map<Integer, ItemStack[]> containers = new HashMap<Integer, ItemStack[]>();
	private boolean inventoryCloseEvent(Player player, CraftInventory inventory, CraftInventory other, Location location) {
		if (location == null)
			return false;

		if (!containers.containsKey(player.getName().hashCode()))
			return false;

		String playerName = player.getName();
		ItemStack[] before = (ItemStack[])this.containers.get(playerName.hashCode());
		ItemStack[] after = BukkitUtils.compressInventory(inventory.getContents());
		ItemStack[] diff = BukkitUtils.compareInventories(before, after);

		for (final ItemStack item : diff) {
			this.consumer.queueChestAccess(playerName, location, location.getWorld().getBlockTypeIdAt(location), (short)item.getTypeId(), (short)item.getAmount(), BukkitUtils.rawData(item));
		}

		containers.remove(playerName.hashCode());

		return false;
	}

	private boolean inventoryOpenEvent(Player player, CraftInventory inventory, CraftInventory other, Location location) {
		if (location == null)
			return false;

		containers.put(player.getName().hashCode(), BukkitUtils.compressInventory(inventory.getContents()));

		return false;
	}
}
