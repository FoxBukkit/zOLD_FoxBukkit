package de.doridian.yiffbukkit.yiffpoints.commands;

import de.doridian.yiffbukkit.bans.FishBansResolver;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.PermissionDeniedException;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@Names("addfunds")
@Help("Adds funds to the specified player's balance.")
@Usage("<exact player name> <cents/yiffpoints to add>")
@Permission("yiffbukkit.yiffpoints.addfunds")
public class AddFundsComand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws YiffBukkitCommandException {
		if (commandSender instanceof Player)
			throw new PermissionDeniedException();

		if (args.length < 2)
			throw new YiffBukkitCommandException("Not enough arguments");

		final UUID playerName = FishBansResolver.getUUID(args[0]);
		final double amount = Double.parseDouble(args[1]);
		plugin.bank.addFunds(playerName, amount);

		PlayerHelper.sendDirectedMessage(commandSender, "Added "+amount+" YP to the account of "+playerName+".");

		// Notify the player if they're online
		final Player player = Bukkit.getServer().getPlayer(playerName);
		if (player != null) {
			double total = plugin.bank.getBalance(playerName);
			PlayerHelper.sendDirectedMessage(player, "Your balance was increased by "+amount+" YP. You now have "+total +" YP.");
		}
	}
}
