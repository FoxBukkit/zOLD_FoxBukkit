package de.doridian.yiffbukkit.yiffpoints.commands;

import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import org.bukkit.entity.Player;

@Names("money")
@Help("Displays your current balance.")
@Permission("yiffbukkit.yiffpoints.money")
public class MoneyComand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		final double amount = plugin.bank.getBalance(ply.getUniqueId());

		PlayerHelper.sendDirectedMessage(ply, "Your current balance is "+amount+" YP.");
	}
}
