package de.doridian.yiffbukkit.commands;

import net.minecraft.server.Packet1Login;

import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class TestCommand extends ICommand {

	public int GetMinLevel() {
		return 5;
	}

	public TestCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		//CraftPlayer cply = (CraftPlayer)ply;
		//World world = ply.getWorld();
		//CraftServer cserver = (CraftServer)plugin.getServer();
		playerHelper.sendPacketToPlayer(ply, new Packet1Login("","",ply.getEntityId(),0,(byte)ply.getWorld().getEnvironment().ordinal()));
	}
}
