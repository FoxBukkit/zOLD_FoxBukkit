package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.AbstractPlayerStateCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class AbstractPotionEffectCommand extends AbstractPlayerStateCommand implements Runnable {
	public AbstractPotionEffectCommand() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 300);
	}

	@Override
	public void run() {
		for(String plyN : states) {
			Player ply = plugin.getServer().getPlayerExact(plyN);
			if(ply == null || !ply.isOnline())
				continue;
			addPotionEffect(ply);
		}
	}

	protected abstract String getPermissionOthers();

	protected abstract PotionEffectType getPotionEffectType();

	private void addPotionEffect(Player ply) {
		PotionEffect potionEffect = new PotionEffect(getPotionEffectType(), 600, 1);
		ply.addPotionEffect(potionEffect, true);
	}

	@Override
	protected final void onStateChange(boolean prevState, boolean newState, String targetName, CommandSender commandSender) throws YiffBukkitCommandException {
		if (!commandSender.getName().equals(targetName)) {
			if (!commandSender.hasPermission(getPermissionOthers()))
				throw new PermissionDeniedException();
		}

		Player ply = plugin.getServer().getPlayerExact(targetName);
		if(ply != null && ply.isOnline()) {
			if(!newState) {
				ply.removePotionEffect(getPotionEffectType());
			} else {
				addPotionEffect(ply);
			}
		}
	}
}
