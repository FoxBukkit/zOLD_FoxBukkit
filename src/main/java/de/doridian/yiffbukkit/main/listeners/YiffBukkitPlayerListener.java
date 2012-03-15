package de.doridian.yiffbukkit.main.listeners;

import de.doridian.yiffbukkit.main.ToolBind;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissibleBase;
import de.doridian.yiffbukkitsplit.util.IPGeolocation;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChatEvent;
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
import org.getspout.spout.player.SpoutCraftPlayer;

import java.io.File;
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
		final String playerName = event.getPlayer().getName();
		if (!playerName.matches("^.*[A-Za-z].*$")) {
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "[YB] Sorry, get some letters into your name.");
			return;
		}

		if (plugin.serverClosed && playerHelper.getPlayerRank(event.getPlayer()).equals("guest")) {
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, "[YB] Sorry, we're closed for guests right now");
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

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
			event.setJoinMessage("\u00a72[+] \u00a7e" + playerHelper.GetFullPlayerName(player) + "\u00a7e joined from " + IPGeolocation.getCountry(player.getAddress()) + "!");
		} else {
			Location location = playerHelper.getPlayerSpawnPosition(player);
			player.teleport(location);
			plugin.ircbot.sendToPublicChannel(player.getName() + " joined for the first time!");
			plugin.ircbot.sendToStaffChannel(player.getName() + " joined with the IP " + player.getAddress().toString() + " for the first time!");
			event.setJoinMessage("\u00a72[+] \u00a7e" + playerHelper.GetFullPlayerName(player) + "\u00a7e joined from " + IPGeolocation.getCountry(player.getAddress()) + " for the first time!");
		}

		playerHelper.updateToolMappings(player);
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
		event.setQuitMessage("\u00a74[-] \u00a7e" + playerHelper.GetFullPlayerName(player) + "\u00a7e disconnected!");
		plugin.chatManager.popCurrentOrigin();

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
		event.setLeaveMessage("\u00a74[-] \u00a7e" + playerHelper.GetFullPlayerName(player) + "\u00a7e was kicked (" + event.getReason() + ")!");
		plugin.chatManager.popCurrentOrigin();

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
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled())
			return;

		event.setFormat(playerHelper.getPlayerTag(event.getPlayer()) + "%s:\u00a7f %s");

		final Player ply = event.getPlayer();
		String conversationTarget = playerHelper.conversations.get(ply.getName());
		String message = event.getMessage();
		String formattedMessage = String.format(event.getFormat(), ply.getDisplayName(), message);
		if (conversationTarget != null) {
			formattedMessage = "\u00a7e[CONV]\u00a7f "+formattedMessage;

			plugin.chatManager.pushCurrentOrigin(ply);
			ply.sendMessage(formattedMessage);
			plugin.getServer().getPlayer(conversationTarget).sendMessage(formattedMessage);
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

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		/*if (event.isCancelled())
			return;*/

		Player ply = event.getPlayer();
		Block clickedBlock = event.getClickedBlock();
		switch (event.getAction()) {
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			try {
				Material itemMaterial = event.getMaterial();
				// This will not be logged by logblock so I only allowed it for ops+ for now.
				// A fix would be to modify the event a bit to make BB log this. 
				if (itemMaterial == Material.INK_SACK && ply.hasPermission("yiffbukkit.dyepaint")) {
					if (clickedBlock != null && clickedBlock.getType() == Material.WOOL) {
						ItemStack item = event.getItem();

						final byte newData = (byte)(15 - item.getDurability());

						if (plugin.logBlockConsumer != null)
							plugin.logBlockConsumer.queueBlockReplace(event.getPlayer().getName(), event.getClickedBlock().getState(), 35, newData);
						clickedBlock.setData(newData);

						int newAmount = item.getAmount()-1;
						if (newAmount > 0)
							item.setAmount(newAmount);
						else
							ply.setItemInHand(null);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Material itemMaterial = event.getMaterial();

				String key = ply.getName()+" "+itemMaterial.name();
				ToolBind toolBind = playerHelper.toolMappings.get(key);
				if (toolBind != null) {
					event.setCancelled(true);
					try {
						toolBind.run(event);
					}
					catch (YiffBukkitCommandException e) {
						playerHelper.sendDirectedMessage(ply,e.getMessage(), e.getColor());
					}
					catch (Exception e) {
						if (ply.hasPermission("yiffbukkit.detailederrors")) {
							playerHelper.sendDirectedMessage(ply,"Command error: "+e+" in "+e.getStackTrace()[0]);
							e.printStackTrace();
						}
						else {
							playerHelper.sendDirectedMessage(ply,"Command error!");
						}
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

			String key = ply.getName()+" "+itemMaterial.name();
			ToolBind toolBind = playerHelper.toolMappings.get(key);
			if (toolBind != null) {
				event.setCancelled(true);
				try {
					toolBind.run(event);
				}
				catch (YiffBukkitCommandException e) {
					playerHelper.sendDirectedMessage(ply,e.getMessage(), e.getColor());
				}
				catch (Exception e) {
					if (ply.hasPermission("yiffbukkit.detailederrors")) {
						playerHelper.sendDirectedMessage(ply,"Command error: "+e+" in "+e.getStackTrace()[0]);
						e.printStackTrace();
					}
					else {
						playerHelper.sendDirectedMessage(ply,"Command error!");
					}
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
