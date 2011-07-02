package de.doridian.yiffbukkit.commands;

import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;

import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("rcon")
@Help("Pushes a command to console")
@Usage("<command>")
@Level(5)
@Permission("yiffbukkit.rcon")
public class ConsoleCommand extends ICommand {
	private final MinecraftServer mcServer;

	public ConsoleCommand() {
		mcServer = ((CraftServer) plugin.getServer()).getHandle().server;
	}

	private final void sendServerCmd(String cmd, CommandSender sender) {
		if (mcServer != null && !mcServer.isStopped && MinecraftServer.isRunning(mcServer)) {
			mcServer.issueCommand(cmd, mcServer);
		}
		else {
			if (sender != null) {
				sender.sendMessage(ChatColor.RED + "Can't send console command!");
			}
			else {
				Logger.getLogger("Minecraft").log(java.util.logging.Level.WARNING, "Can't send console command!");
			}
		}
	}

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) {
		sendServerCmd(argStr, commandSender);
	}
}
