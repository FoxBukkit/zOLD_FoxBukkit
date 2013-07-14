package de.doridian.yiffbukkit.remote.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.AbusePotential;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Level;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.remote.RemotePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@Names("rawrconapi")
@Level(9000)
@AbusePotential
public class RawRconApiCommand extends ICommand {
	public void run(final CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		if(!(commandSender instanceof RemotePlayer)) return;
		commandSender.sendMessage(exec(args));
	}
	
	private String exec(String[] args) {
		char pType = args[0].charAt(0);
		char pCmd = args[0].charAt(1);
		switch(pType) {
			case 'i': //inventory
				PlayerInventory inv = plugin.getServer().getPlayer(args[1]).getInventory();
				switch(pCmd) {
					case 'e': //enumerate
						ItemStack[] stacks = inv.getContents();
						StringBuilder str = new StringBuilder();
						for(int i=0;i<stacks.length;i++) {
							ItemStack current = stacks[i];
							if(current == null) continue;
							str.append("\n" + i + " " + current.getTypeId() + " " + current.getAmount() + " " + current.getDurability());
						}
						return str.deleteCharAt(0).toString();
					case 'g': //get
						ItemStack stack = inv.getItem(Integer.valueOf(args[2]));
						return stack.getTypeId() + " " + stack.getAmount() + " " + stack.getDurability();
					case 's': //set
						inv.setItem(Integer.valueOf(args[2]), new ItemStack(Integer.valueOf(args[3]), Integer.valueOf(args[4]), Short.valueOf(args[5])));
						break;
					case 'd': //delete
						inv.clear(Integer.valueOf(args[2]));
						break;
					case 'c': //clear
						inv.clear();
						break;
					case 'm': //move
						int from = Integer.valueOf(args[2]);
						inv.setItem(Integer.valueOf(args[3]), inv.getItem(from));
						inv.clear(from);
						break;
				}
				break;
			case 'p': //player
				switch(pCmd) {
					case 'e': //enumerate
						StringBuilder str = new StringBuilder();
						for(Player ply : plugin.getServer().getOnlinePlayers()) {
							str.append("\n" + ply.getName() + " " + plugin.playerHelper.GetFullPlayerName(ply));
						}
						return str.deleteCharAt(0).toString();
					case 'r': //rank
						return plugin.playerHelper.getPlayerRank(args[1]);
				}
		}
		return null;
	}
}
