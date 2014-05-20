/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
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
		PotionEffect potionEffect = new PotionEffect(getPotionEffectType(), 1200, 0);
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
