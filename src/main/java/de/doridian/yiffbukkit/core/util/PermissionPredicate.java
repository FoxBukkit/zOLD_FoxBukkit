package de.doridian.yiffbukkit.core.util;

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
