package de.doridian.yiffbukkit.commands;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.YiffBukkitPlayerListener;

public class VanishCommand extends ICommand {
	public VanishCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	@Override
	public int GetMinLevel() {
		return 3;
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		String playerName = ply.getName();
		if (playerHelper.vanishedPlayers.contains(playerName)) {
			if (argStr.equals("on"))
				throw new YiffBukkitCommandException("Already invisible!");

			playerHelper.vanishedPlayers.remove(playerName);
			playerHelper.sendPacketToPlayersAround(ply.getLocation(), 1024, new Packet29DestroyEntity(ply.getEntityId()), ply, 3);
			playerHelper.sendPacketToPlayersAround(ply.getLocation(), 1024, new Packet20NamedEntitySpawn(((CraftPlayer)ply).getHandle()), ply, 3);
			playerHelper.SendServerMessage(ply.getName() + " reappeared.", playerHelper.GetPlayerLevel(ply));
		}
		else {
			if (argStr.equals("off"))
				throw new YiffBukkitCommandException("Already visible!");

			playerHelper.vanishedPlayers.add(playerName);
			playerHelper.sendPacketToPlayersAround(ply.getLocation(), 1024, new Packet29DestroyEntity(ply.getEntityId()), ply, 3);
			playerHelper.SendServerMessage(ply.getName() + " vanished.", playerHelper.GetPlayerLevel(ply));
		}

	}

	@Override
	public String GetHelp() {
		return "Makes you invisible";
	}

	@Override
	public String GetUsage() {
		return "[on|off]";
	}
}
