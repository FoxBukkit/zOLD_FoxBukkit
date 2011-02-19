package com.bukkit.doridian.yiffbukkit.commands;

import net.minecraft.server.Packet1Login;

//import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.bukkit.doridian.yiffbukkit.YiffBukkit;

public class TestCommand extends ICommand {

	public int GetMinLevel() {
		return 5;
	}
	
	public TestCommand(YiffBukkit plug) {
		super(plug);
	}

	public void Run(Player ply, String[] args, String argStr) {
		CraftPlayer cply = (CraftPlayer)ply;
		//World world = ply.getWorld();
		//CraftServer cserver = (CraftServer)plugin.getServer();
		cply.getHandle().a.b(new Packet1Login("","",ply.getEntityId(),0,(byte)ply.getWorld().getEnvironment().ordinal()));
	}

	public String GetHelp() {
		return "Faggot";
	}

	public String GetUsage() {
		return "Faggot";
	}

}
