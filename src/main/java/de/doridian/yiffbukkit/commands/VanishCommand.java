package de.doridian.yiffbukkit.commands;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("vanish")
@Help("Makes you invisible")
@Usage("[on|off]")
@Level(3)
public class VanishCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		String playerName = ply.getName();
		if (playerHelper.vanishedPlayers.contains(playerName)) {
			if (argStr.equals("on"))
				throw new YiffBukkitCommandException("Already invisible!");

			playerHelper.vanishedPlayers.remove(playerName);
			playerHelper.sendPacketToPlayersAround(ply.getLocation(), 1024, new Packet29DestroyEntity(ply.getEntityId()), ply, 3);
			playerHelper.sendPacketToPlayersAround(ply.getLocation(), 1024, new Packet20NamedEntitySpawn(((CraftPlayer)ply).getHandle()), ply, 3);
			playerHelper.sendServerMessage(ply.getName() + " reappeared.", playerHelper.getPlayerLevel(ply));
		}
		else {
			if (argStr.equals("off"))
				throw new YiffBukkitCommandException("Already visible!");

			playerHelper.vanishedPlayers.add(playerName);
			playerHelper.sendPacketToPlayersAround(ply.getLocation(), 1024, new Packet29DestroyEntity(ply.getEntityId()), ply, 3);
			playerHelper.sendServerMessage(ply.getName() + " vanished.", playerHelper.getPlayerLevel(ply));
		}

	}
}
