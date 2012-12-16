package de.doridian.yiffbukkit.fun.commands;

import de.doridian.yiffbukkit.main.commands.system.ICommand;
import org.bukkit.potion.PotionEffectType;

@ICommand.Names("fullbright")
@ICommand.Help("Activates or deactivates fullbright mode.")
@ICommand.Usage("[<name>] [on|off]")
@ICommand.Permission("yiffbukkit.players.fullbright")
public class FullbrightCommand extends AbstractPotionEffectCommand {
	@Override
	protected PotionEffectType getPotionEffectType() {
		return PotionEffectType.NIGHT_VISION;
	}

	@Override
	protected String getPermissionOthers() {
		return "yiffbukkit.players.fullbright.others";
	}
}
