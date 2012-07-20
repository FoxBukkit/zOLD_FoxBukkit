package de.doridian.yiffbukkit.yiffpoints.commands;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

@Names("money")
@Help("Displays your current balance.")
@Permission("yiffbukkit.yiffpoints.money")
public class MoneyComand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		final String playerName = ply.getName();
		final double amount = plugin.bank.getBalance(playerName);

		PlayerHelper.sendDirectedMessage(ply, "Your current balance is "+amount+" YP.");
	}
}
