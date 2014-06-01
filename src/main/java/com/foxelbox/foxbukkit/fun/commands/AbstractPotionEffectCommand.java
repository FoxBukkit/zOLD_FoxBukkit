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
package com.foxelbox.foxbukkit.fun.commands;

import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.PermissionDeniedException;
import com.foxelbox.foxbukkit.main.commands.AbstractPlayerStateCommand;
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
		PotionEffect potionEffect = new PotionEffect(getPotionEffectType(), 1200, 0);
		ply.addPotionEffect(potionEffect, true);
	}

	@Override
	protected final void onStateChange(boolean prevState, boolean newState, String targetName, CommandSender commandSender) throws FoxBukkitCommandException {
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
