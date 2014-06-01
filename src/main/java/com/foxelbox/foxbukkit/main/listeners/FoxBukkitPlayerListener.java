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
package com.foxelbox.foxbukkit.main.listeners;

import com.foxelbox.foxbukkit.chat.RedisHandler;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.ToolBind;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.spawning.SpawnUtils;
import com.foxelbox.foxbukkit.spawning.effects.system.FBEffect;
import net.minecraft.server.v1_7_R3.AxisAlignedBB;
import net.minecraft.server.v1_7_R3.EntityAnimal;
import net.minecraft.server.v1_7_R3.EntityInsentient;
import net.minecraft.server.v1_7_R3.EntityLiving;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.EntityTameableAnimal;
import net.minecraft.server.v1_7_R3.EntityWaterAnimal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SpawnEgg;
import org.bukkit.material.Wool;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

/**
 * Handle events for all Player related events
 * @author Doridian
 */
public class FoxBukkitPlayerListener extends BaseListener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		playerHelper.refreshUUID(player);

		playerHelper.applyTime(player);

		playerHelper.pushPlayerLocationOntoTeleportStack(player);

		playerHelper.setPlayerDisplayName(player);

		playerHelper.setPlayerListName(player);
		playerHelper.setPlayerScoreboardTeam(player);

		final File playerFile = PlayerHelper.getPlayerFile(player.getUniqueId(), "world");
		if (playerFile == null || !playerFile.exists()) {
			Location location = playerHelper.getPlayerSpawnPosition(player);
			player.teleport(location);
		}

		event.setJoinMessage(null);
		RedisHandler.sendMessage(player, "\u0123join");

		ToolBind.updateToolMappings(player);
		playerHelper.pushWeather(player);

		playerHelper.refreshPlayerListRedis(null);
	}

	public Hashtable<String,String> offlinePlayers = new Hashtable<>();
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final String playerName = player.getName();

		playerHelper.teleportHistory.remove(player.getUniqueId());

		event.setQuitMessage(null);

		RedisHandler.sendMessage(player, "\u0123quit");

		offlinePlayers.put(player.getAddress().getAddress().getHostAddress(), playerName);

		playerHelper.refreshPlayerListRedis(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event) {
		final Player player = event.getPlayer();

		event.setLeaveMessage(null);

		RedisHandler.sendMessage(player, "\u0123kick " + event.getReason());

		playerHelper.refreshPlayerListRedis(player);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player ply = event.getPlayer();
		playerHelper.pushPlayerLocationOntoTeleportStack(ply);
		Location location = playerHelper.getPlayerSpawnPosition(ply);
		event.setRespawnLocation(location);
	}

	/*@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
		Player ply = event.getPlayer();
		Location location = playerHelper.getPlayerSpawnPosition(ply);)
		event.setSpawnLocation(location);
	}*/

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		event.setFormat(playerHelper.getPlayerTag(event.getPlayer()) + "%s:\u00a7f %s");

		final Player ply = event.getPlayer();
		String message = event.getMessage();
		if(message.charAt(0) == '#') {
			RedisHandler.sendMessage(ply, "/opchat " + message.substring(1));
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		final Player ply = event.getPlayer();
		final String cmdString = event.getMessage().substring(1).trim();
		
		if (plugin.commandSystem.runCommand(ply, cmdString)) {
			event.setCancelled(true);
			event.setMessage("/youdontwantthiscommand "+event.getMessage());
		}
		else
		{
			plugin.log("Other Command: "+ply.getName()+": "+cmdString);
		}
	}

	{
		ToolBind.addGlobal(Material.INK_SACK, new ToolBind("dye", null) {
			@Override
			public boolean run(PlayerInteractEvent event) throws FoxBukkitCommandException {
				Player player = event.getPlayer();
				// This will not be logged by logblock so I only allowed it for ops+ for now.
				// A fix would be to modify the event a bit to make BB log this. 
				if (!player.hasPermission("foxbukkit.dyepaint"))
					return false;

				final Block clickedBlock = event.getClickedBlock();
				if (clickedBlock == null)
					return false;
				if (clickedBlock.getType() != Material.WOOL)
					return false;

				final ItemStack item = event.getItem();

				final byte newData = (byte)(15 - item.getDurability());

				final BlockState state = clickedBlock.getState();

				if (plugin.logBlockConsumer != null)
					plugin.logBlockConsumer.queueBlockReplace(event.getPlayer().getName(), state, 35, newData);

				final Wool wool = (Wool) state.getData();
				final Dye dye = (Dye) item.getData();
				wool.setColor(dye.getColor());
				state.setData(wool);
				state.update();

				int newAmount = item.getAmount()-1;
				if (newAmount > 0)
					item.setAmount(newAmount);
				else
					player.setItemInHand(null);

				return true;
			}
		});

		ToolBind.addGlobal(Material.RED_MUSHROOM, new ToolBind("red shroom", null) {
			@Override
			public boolean run(PlayerInteractEvent event) throws FoxBukkitCommandException {
				if (event.hasBlock())
					return false;

				final Player player = event.getPlayer();
				FBEffect.create("lsd", player).start();

				ItemStack item = event.getItem();

				int newAmount = item.getAmount()-1;
				if (newAmount > 0)
					item.setAmount(newAmount);
				else
					player.setItemInHand(null);

				return true;
			}
		});

		ToolBind.addGlobal(Material.LEASH, new ToolBind("leash", null) {
			final class Perms {
				public final boolean mayLeashToPlayers;
				public final boolean mayLeashToLeashable;
				public final boolean mayLeashToNonLeashable;
				public final boolean mayLeashToNonLiving;

				public Perms(Player player) {
					mayLeashToPlayers = player.hasPermission("foxbukkit.leash.players");
					mayLeashToLeashable = player.hasPermission("foxbukkit.leash.leashable");
					mayLeashToNonLeashable = player.hasPermission("foxbukkit.leash.nonleashable");
					mayLeashToNonLiving = player.hasPermission("foxbukkit.leash.nonliving");
				}

				private boolean isNaturallyLeashable(net.minecraft.server.v1_7_R3.Entity entity) {
					if (entity instanceof EntityTameableAnimal)
						return ((EntityTameableAnimal) entity).isTamed();

					if (entity instanceof EntityAnimal)
						return true;

					//noinspection RedundantIfStatement
					if (entity instanceof EntityWaterAnimal)
						return true;

					return false;
				}

				public boolean mayLeashTo(net.minecraft.server.v1_7_R3.Entity entity) {
					if (entity instanceof EntityPlayer)
						return mayLeashToPlayers;

					if (isNaturallyLeashable(entity))
						return mayLeashToLeashable;

					if (entity instanceof EntityLiving)
						return mayLeashToNonLeashable;

					return mayLeashToNonLiving;
				}
			}
			
			@Override
			public boolean run(PlayerInteractEntityEvent event) throws FoxBukkitCommandException {
				final Player player = event.getPlayer();
				if (!player.isSneaking())
					return false;

				if (!player.hasPermission("foxbukkit.leash"))
					return false;

				final Perms perms = new Perms(player);

				final Entity rightClicked = event.getRightClicked();
				final net.minecraft.server.v1_7_R3.Entity notchRightClicked = ((CraftEntity) rightClicked).getHandle();
				final EntityPlayer notchPlayer = ICommand.asNotchPlayer(player);
				final net.minecraft.server.v1_7_R3.World world = notchPlayer.world;

				if (!perms.mayLeashTo(notchRightClicked))
					return false;

				final Location location = player.getLocation();
				final double x = location.getX();
				final double y = location.getY();
				final double z = location.getZ();
				final double maxDistance = 7.0D + location.distance(rightClicked.getLocation());

				final AxisAlignedBB aabb = AxisAlignedBB.a(x - maxDistance, y - maxDistance, z - maxDistance, x + maxDistance, y + maxDistance, z + maxDistance);
				@SuppressWarnings("unchecked")
				List<? extends EntityInsentient> list = world.a(EntityInsentient.class, aabb); // v1_7_R1

				boolean ret = false;
				if (list != null) {
					for (EntityInsentient entityinsentient : list) {
						if (!entityinsentient.bN()) // v1_7_R2
							continue;

						final net.minecraft.server.v1_7_R3.Entity leashed = entityinsentient.getLeashHolder();

						if (leashed == notchRightClicked) {
							entityinsentient.setLeashHolder(notchPlayer, true);
							ret = true;
							continue;
						}

						if (leashed != notchPlayer)
							continue;

						if (entityinsentient == notchRightClicked)
							continue;

						entityinsentient.setLeashHolder(notchRightClicked, true);
						ret = true;
					}
				}

				return ret;
			}
		});

		ToolBind.addGlobal(Material.MONSTER_EGG, new ToolBind("monster egg", null) {
			@Override
			public boolean run(PlayerInteractEvent event) throws FoxBukkitCommandException {
				return handlePlayerEvent(event);
			}

			@Override
			public boolean run(PlayerInteractEntityEvent event) throws FoxBukkitCommandException {
				return handlePlayerEvent(event);
			}

			private boolean handlePlayerEvent(PlayerEvent event) {
				final Player player = event.getPlayer();
				final MaterialData data = player.getItemInHand().getData();
				if (!(data instanceof SpawnEgg))
					return false;

				final String typeName = ((SpawnEgg) data).getSpawnedType().name().toLowerCase();
				final Location location = player.getLocation();
				final String playerName = player.getName();
				SpawnUtils.logSpawn(playerName, location, 1, typeName);

				// Do not cancel, just log
				return false;
			}
		});
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		/*if (event.isCancelled())
			return;*/

		boolean isLeftClick = false;
		final Player ply = event.getPlayer();
		switch (event.getAction()) {
		case LEFT_CLICK_AIR:
		case LEFT_CLICK_BLOCK:
			isLeftClick = true;
			/* FALL-THROUGH */
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			try {
				final Material itemMaterial = event.getMaterial();

				final ToolBind toolBind = ToolBind.get(ply.getName(), itemMaterial, isLeftClick);
				if (toolBind != null) {
					boolean success = true;
					try {
						success = toolBind.run(event);
					}
					catch (FoxBukkitCommandException e) {
						PlayerHelper.sendDirectedMessage(ply,e.getMessage(), e.getColor());
					}
					catch (Exception e) {
						if (ply.hasPermission("foxbukkit.detailederrors")) {
							PlayerHelper.sendDirectedMessage(ply,"Command error: "+e+" in "+e.getStackTrace()[0]);
							e.printStackTrace();
						}
						else {
							PlayerHelper.sendDirectedMessage(ply,"Command error!");
						}
					}
					finally {
						if (success)
							event.setCancelled(true);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			break;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player ply = event.getPlayer();

		try {
			Material itemMaterial = ply.getItemInHand().getType();

			ToolBind toolBind = ToolBind.get(ply.getName(), itemMaterial, false);
			if (toolBind != null) {
				boolean success = true;
				try {
					success = toolBind.run(event);
				}
				catch (FoxBukkitCommandException e) {
					PlayerHelper.sendDirectedMessage(ply,e.getMessage(), e.getColor());
				}
				catch (Exception e) {
					if (ply.hasPermission("foxbukkit.detailederrors")) {
						PlayerHelper.sendDirectedMessage(ply,"Command error: "+e+" in "+e.getStackTrace()[0]);
						e.printStackTrace();
					}
					else {
						PlayerHelper.sendDirectedMessage(ply,"Command error!");
					}
				}
				finally {
					if (success)
						event.setCancelled(true);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerPortal(PlayerPortalEvent event) {
		final Player player = event.getPlayer();

		if (player.hasPermission("foxbukkit.createnetherportal"))
			return;

		event.setCancelled(true);
	}
}
