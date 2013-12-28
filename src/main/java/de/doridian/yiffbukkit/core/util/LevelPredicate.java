package de.doridian.yiffbukkit.core.util;

import com.google.common.base.Predicate;
import de.doridian.yiffbukkit.core.YiffBukkit;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;

public class LevelPredicate implements Predicate<CommandSender> {
	private final int minLevel;

	public LevelPredicate(final int minLevel) {
		this.minLevel = minLevel;
	}

	@Override
	public boolean apply(@Nullable CommandSender player) {
		if(player == null)
			return false;
		return YiffBukkit.instance.playerHelper.getPlayerLevel(player) >= minLevel;
	}
}
