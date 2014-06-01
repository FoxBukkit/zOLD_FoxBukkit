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
package de.doridian.foxbukkit.core.util;

import com.google.common.base.Predicate;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;

public class PermissionPredicate implements Predicate<CommandSender> {
	private final String permission;

	public PermissionPredicate(final String permission) {
		this.permission = permission;
	}

	@Override
	public boolean apply(@Nullable CommandSender player) {
		if(player == null)
			return false;
		return player.hasPermission(permission);
	}
}
