package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Permission;
import org.bukkit.command.CommandSender;

@Names("reloadads")
@Help("Reload ads")
@Permission("yiffbukkit.useless.reloadads")
public class ReloadAdsCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		plugin.adHandler.ReloadAds();
	}
}
