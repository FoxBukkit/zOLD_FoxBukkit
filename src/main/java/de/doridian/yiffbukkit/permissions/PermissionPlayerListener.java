package de.doridian.yiffbukkit.permissions;

import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.listeners.YiffBukkitBlockListener;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spout.player.SpoutCraftPlayer;

public class PermissionPlayerListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		ItemStack item = event.getItemStack();
		Material itemMaterial = item.getType();
		if(itemMaterial == Material.AIR) return;

		Player ply = event.getPlayer();
		if(YiffBukkit.instance.playerHelper.isPlayerDisabled(ply)) {
			item.setType(Material.GOLD_HOE);
			item.setAmount(1);
			item.setDurability(Short.MAX_VALUE);
			return;
		}

		if (!ply.hasPermission("yiffbukkit.place")) {
			YiffBukkit.instance.ircbot.sendToStaffChannel(ply.getName() + " is not allowed to build but tried tried to spawn " + itemMaterial+".");
			YiffBukkit.instance.playerHelper.sendServerMessage(ply.getName() + " is not allowed to build but tried tried to spawn " + itemMaterial+".");
			item.setType(Material.GOLD_HOE);
			item.setAmount(1);
			item.setDurability(Short.MAX_VALUE);
			return;
		}

		final String permission = YiffBukkitBlockListener.blocklevels.get(itemMaterial);
		if (permission != null && !ply.hasPermission(permission)) {
			YiffBukkit.instance.ircbot.sendToStaffChannel(ply.getName() + " tried to spawn illegal block " + itemMaterial);
			YiffBukkit.instance.playerHelper.sendServerMessage(ply.getName() + " tried to spawn illegal block " + itemMaterial);
			item.setType(Material.GOLD_HOE);
			item.setAmount(1);
			item.setDurability(Short.MAX_VALUE);
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		
		if (player.getClass().getName().equals("org.getspout.spout.player.SpoutCraftPlayer")) {
			final SpoutCraftPlayer spoutPlayer = (SpoutCraftPlayer)player;
			Utils.setPrivateValue(SpoutCraftPlayer.class, spoutPlayer, "perm", new YiffBukkitPermissibleBase(player));
		} else if(player instanceof CraftHumanEntity) {
			final CraftHumanEntity craftPlayer = (CraftHumanEntity)player;
			Utils.setPrivateValue(CraftHumanEntity.class, craftPlayer, "perm", new YiffBukkitPermissibleBase(player));
		} else {
			System.out.println("Sorry, invalid stuff :(");
		}
	}
}
