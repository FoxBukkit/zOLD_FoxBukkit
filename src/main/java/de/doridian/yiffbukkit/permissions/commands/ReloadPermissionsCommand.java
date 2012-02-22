package de.doridian.yiffbukkit.permissions.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.ICommand.Usage;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissionHandler;
import org.bukkit.command.CommandSender;

@Names("reloadpermissions")
@Help("Reloads the permissions system.")
@Usage("")
@Permission("yiffbukkit.reloadpermissions")
public class ReloadPermissionsCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		YiffBukkitPermissionHandler.instance.reload();
		plugin.playerHelper.sendDirectedMessage(commandSender, "Permissions system reloaded!");
	}
}
