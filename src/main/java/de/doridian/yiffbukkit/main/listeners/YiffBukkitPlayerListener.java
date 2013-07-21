package de.doridian.yiffbukkit.main.listeners;

import de.doridian.yiffbukkit.chat.RedisHandler;
import de.doridian.yiffbukkit.main.ToolBind;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.spawning.effects.system.YBEffect;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_6_R2.AxisAlignedBB;
import net.minecraft.server.v1_6_R2.EntityAnimal;
import net.minecraft.server.v1_6_R2.EntityInsentient;
import net.minecraft.server.v1_6_R2.EntityLiving;
import net.minecraft.server.v1_6_R2.EntityPlayer;
import net.minecraft.server.v1_6_R2.EntityTameableAnimal;
import net.minecraft.server.v1_6_R2.EntityWaterAnimal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handle events for all Player related events
 * @author Doridian
 */
public class YiffBukkitPlayerListener extends BaseListener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		final Player player = event.getPlayer();
		final String playerName = player.getName();
		if (!playerName.matches("^.*[A-Za-z].*$")) {
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "[YB] Sorry, get some letters into your name.");
			return;
		}

		if (playerHelper.isGuest(player)) {
			switch (plugin.lockdownMode) {
			case FIREWALL:
				final String ip = event.getAddress().getHostAddress();
				try {
					Runtime.getRuntime().exec("./wally I "+ip);
					System.out.println("Firewalled IP "+ip+" of player "+playerName+".");
					return;
				}
				catch (IOException e) {
					System.out.println("Failed to firewall IP "+ip+" of player "+playerName+".");
				}
				/* FALL-THROUGH */

			case KICK:
				event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "[YB] Sorry, we're closed for guests right now");
				/* FALL-THROUGH */

			case OFF:
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		playerHelper.applyTime(player);

		playerHelper.setYiffcraftState(player, false);
		playerHelper.pushPlayerLocationOntoTeleportStack(player);

		String nick = playerHelper.getPlayerNick(player.getName());
		if (nick == null)
			nick = player.getName();
		player.setDisplayName(nick);

		playerHelper.setPlayerListName(player);

		final File playerFile = PlayerHelper.getPlayerFile(player.getName(), "world");
		plugin.chatManager.pushCurrentOrigin(player);
		if (playerFile != null && playerFile.exists()) {
			plugin.ircbot.sendToPublicChannel(player.getName() + " joined!");
			plugin.ircbot.sendToStaffChannel(player.getName() + " joined with the IP " + player.getAddress().toString() + "!");
			event.setJoinMessage(null);
			//event.setJoinMessage("\u00a72[+] \u00a7e" + playerHelper.GetFullPlayerName(player) + "\u00a7e joined!");
		} else {
			Location location = playerHelper.getPlayerSpawnPosition(player);
			player.teleport(location);
			plugin.ircbot.sendToPublicChannel(player.getName() + " joined for the first time!");
			plugin.ircbot.sendToStaffChannel(player.getName() + " joined with the IP " + player.getAddress().toString() + " for the first time!");
			event.setJoinMessage(null);
			//event.setJoinMessage("\u00a72[+] \u00a7e" + playerHelper.GetFullPlayerName(player) + "\u00a7e joined for the first time!");
		}

		RedisHandler.sendMessage(player, "\u0123join");

		new Thread() {
			@Override
			public void run() {
				try {
					HttpURLConnection conn = (HttpURLConnection)(new URL("https://dl.dropbox.com/u/44740336/Nodus/capes/"+player.getName().toLowerCase()+".png").openConnection());
					conn.setRequestMethod("HEAD");
					System.setProperty("http.agent", "");
					conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
					conn.setConnectTimeout(10000);
					conn.setReadTimeout(20000);

					int responseCode = conn.getResponseCode();
					if(responseCode == 403 || responseCode == 404) {
						return; //No nodus (donator)
					} else if(responseCode >= 500 && responseCode <= 599) {
						throw new Exception("Error from fetching Nodus details from DropBox: " + responseCode);
					}

					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							plugin.playerHelper.broadcastMessage("\u00a7d[YB]\u00a7f " + player.getName() + " is a Nodus Donator! Watch out :3", "yiffbukkit.opchat");
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

		ToolBind.updateToolMappings(player);
		plugin.chatManager.popCurrentOrigin();
		playerHelper.pushWeather(player);
	}

	public Hashtable<String,String> offlinePlayers = new Hashtable<String, String>();
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final String playerName = player.getName();

		playerHelper.teleportHistory.remove(playerName.toLowerCase());

		plugin.chatManager.pushCurrentOrigin(player);
		plugin.ircbot.sendToChannel(playerName + " disconnected!");
		event.setQuitMessage(null);
		plugin.chatManager.popCurrentOrigin();

		RedisHandler.sendMessage(player, "\u0123quit");

		offlinePlayers.put(player.getAddress().getAddress().getHostAddress(), playerName);

		for (Map<Player, ?> map : playerHelper.registeredMaps)
			map.remove(player);
		for (Set<Player> set : playerHelper.registeredSets)
			set.remove(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		if(event.isCancelled())
			return;

		final Player player = event.getPlayer();

		plugin.chatManager.pushCurrentOrigin(player);
		plugin.ircbot.sendToChannel(player.getName() + " was kicked (" + event.getReason() + ")!");
		event.setLeaveMessage(null);
		plugin.chatManager.popCurrentOrigin();

		RedisHandler.sendMessage(player, "\u0123kick " + event.getReason());

		for (Map<Player, ?> map : playerHelper.registeredMaps)
			map.remove(player);
		for (Set<Player> set : playerHelper.registeredSets)
			set.remove(player);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player ply = event.getPlayer();
		playerHelper.pushPlayerLocationOntoTeleportStack(ply);
		Location location = playerHelper.getPlayerSpawnPosition(ply);
		event.setRespawnLocation(location);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled())
			return;

		event.setFormat(playerHelper.getPlayerTag(event.getPlayer()) + "%s:\u00a7f %s");

		final Player ply = event.getPlayer();
		final String conversationTargetName = playerHelper.conversations.get(ply.getName());
		final Player conversationTarget = conversationTargetName == null ? null : plugin.getServer().getPlayer(conversationTargetName);
		String message = event.getMessage();
		String formattedMessage = String.format(event.getFormat(), ply.getDisplayName(), message);
		if (conversationTarget != null) {
			formattedMessage = "\u00a7e[CONV]\u00a7f "+formattedMessage;

			plugin.chatManager.pushCurrentOrigin(ply);
			ply.sendMessage(formattedMessage);
			conversationTarget.sendMessage(formattedMessage);
			plugin.chatManager.popCurrentOrigin();

			event.setCancelled(true);
		}
		else if(message.charAt(0) == '#') {
			event.setCancelled(true);
			plugin.ircbot.sendToStaffChannel("[OP] [" + event.getPlayer().getName() + "]: " + message.substring(1));
			message = "\u00a7e[#OP] \u00a7f" + ply.getDisplayName() + "\u00a7f: " + message.substring(1);
			playerHelper.broadcastMessage(message, "yiffbukkit.opchat");

			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled())
			return;

		final Player ply = event.getPlayer();
		plugin.chatManager.pushCurrentOrigin(ply);
		final String cmdString = event.getMessage().substring(1).trim();
		
		if (plugin.commandSystem.runCommand(ply, cmdString)) {
			event.setCancelled(true);
			event.setMessage("/youdontwantthiscommand "+event.getMessage());
		}
		else
		{
			if(!(ply.hasPermission("yiffbukkit.ignoreworldeditlogging") && event.getMessage().startsWith("//")))
				plugin.ircbot.sendToStaffChannel("Other Command: " + ply.getName() + ": " +cmdString);
			plugin.log("Other Command: "+ply.getName()+": "+cmdString);
		}
		plugin.chatManager.popCurrentOrigin();
	}

	{
		ToolBind.addGlobal(Material.INK_SACK, new ToolBind("dye", null) {
			@Override
			public boolean run(PlayerInteractEvent event) throws YiffBukkitCommandException {
				Player player = event.getPlayer();
				// This will not be logged by logblock so I only allowed it for ops+ for now.
				// A fix would be to modify the event a bit to make BB log this. 
				if (!player.hasPermission("yiffbukkit.dyepaint"))
					return false;

				Block clickedBlock = event.getClickedBlock();
				if (clickedBlock == null)
					return false;
				if (clickedBlock.getType() != Material.WOOL)
					return false;

				ItemStack item = event.getItem();

				final byte newData = (byte)(15 - item.getDurability());

				if (plugin.logBlockConsumer != null)
					plugin.logBlockConsumer.queueBlockReplace(event.getPlayer().getName(), event.getClickedBlock().getState(), 35, newData);
				clickedBlock.setData(newData);

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
			public boolean run(PlayerInteractEvent event) throws YiffBukkitCommandException {
				if (event.hasBlock())
					return false;

				final Player player = event.getPlayer();
				YBEffect.create("lsd", player).start();

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
					mayLeashToPlayers = player.hasPermission("yiffbukkit.leash.players");
					mayLeashToLeashable = player.hasPermission("yiffbukkit.leash.leashable");
					mayLeashToNonLeashable = player.hasPermission("yiffbukkit.leash.nonleashable");
					mayLeashToNonLiving = player.hasPermission("yiffbukkit.leash.nonliving");
				}

				private boolean isNaturallyLeashable(net.minecraft.server.v1_6_R2.Entity entity) {
					if (entity instanceof EntityTameableAnimal)
						return ((EntityTameableAnimal) entity).isTamed();

					if (entity instanceof EntityAnimal)
						return true;

					if (entity instanceof EntityWaterAnimal)
						return true;
					return false;
				}

				public boolean mayLeashTo(net.minecraft.server.v1_6_R2.Entity entity) {
					if (entity instanceof EntityPlayer)
						return mayLeashToPlayers;

					if (isNaturallyLeashable(entity))
						return mayLeashToLeashable;

					if (entity instanceof EntityLiving)
						return mayLeashToNonLeashable;

					return mayLeashToNonLiving;
				}
			};
			
			@Override
			public boolean run(PlayerInteractEntityEvent event) throws YiffBukkitCommandException {
				final Player player = event.getPlayer();
				if (!player.isSneaking())
					return false;

				if (!player.hasPermission("yiffbukkit.leash"))
					return false;

				final Perms perms = new Perms(player);

				final Entity rightClicked = event.getRightClicked();
				final net.minecraft.server.v1_6_R2.Entity notchRightClicked = ((CraftEntity) rightClicked).getHandle();
				final EntityPlayer notchPlayer = ICommand.asNotchPlayer(player);
				final net.minecraft.server.v1_6_R2.World world = notchPlayer.world;

				if (!perms.mayLeashTo(notchRightClicked))
					return false;

				final Location location = player.getLocation();
				final double x = location.getX();
				final double y = location.getY();
				final double z = location.getZ();
				final double maxDistance = 7.0D + location.distance(rightClicked.getLocation());

				final AxisAlignedBB aabb = AxisAlignedBB.a().a(x - maxDistance, y - maxDistance, z - maxDistance, x + maxDistance, y + maxDistance, z + maxDistance);
				@SuppressWarnings("unchecked")
				List<? extends EntityInsentient> list = world.a(EntityInsentient.class, aabb); // v1_6_R2

				boolean ret = false;
				if (list != null) {
					for (EntityInsentient entityinsentient : list) {
						if (!entityinsentient.bH()) // v1_6_R2
							continue;

						final net.minecraft.server.v1_6_R2.Entity leashed = entityinsentient.bI();

						if (leashed == notchRightClicked) { // v1_6_R2
							entityinsentient.b(notchPlayer, true); // v1_6_R2
							ret = true;
							continue;
						}

						if (leashed != notchPlayer) // v1_6_R2
							continue;

						if (entityinsentient == notchRightClicked) // v1_6_R2
							continue;

						entityinsentient.b(notchRightClicked, true); // v1_6_R2
						ret = true;
					}
				}

				return ret;
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
					catch (YiffBukkitCommandException e) {
						PlayerHelper.sendDirectedMessage(ply,e.getMessage(), e.getColor());
					}
					catch (Exception e) {
						if (ply.hasPermission("yiffbukkit.detailederrors")) {
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
				catch (YiffBukkitCommandException e) {
					PlayerHelper.sendDirectedMessage(ply,e.getMessage(), e.getColor());
				}
				catch (Exception e) {
					if (ply.hasPermission("yiffbukkit.detailederrors")) {
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (event.isCancelled())
			return;

		final Item item = event.getItemDrop();
		final ItemStack itemStack = item.getItemStack();
		final int typeId = itemStack.getTypeId();

		int amount = itemStack.getAmount();

		final List<Entity> nearbyEntities = item.getNearbyEntities(2, 2, 2);
		if (nearbyEntities.size() < 15)
			return;

		for (Entity entity : nearbyEntities) {
			if (!(entity instanceof Item))
				continue;

			final Item otherItem = (Item) entity;

			if (otherItem.isDead())
				continue;

			final ItemStack otherItemStack = otherItem.getItemStack();
			if (typeId != otherItemStack.getTypeId())
				continue;

			amount += otherItemStack.getAmount();

			otherItem.remove();
		}

		itemStack.setAmount(amount);
	}
}
