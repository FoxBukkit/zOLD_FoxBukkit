package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("reloadpermissions")
@Help("Reloads the permissions system.")
@Usage("")
@Permission("yiffbukkit.reloadpermissions")
public class ReloadPermissionsCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		plugin.permissionHandler.reload();
		plugin.playerHelper.sendDirectedMessage(commandSender, "Permissions system reloaded!");
	}
}
