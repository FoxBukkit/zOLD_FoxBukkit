package de.doridian.yiffbukkit.commands;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet100OpenWindow;
import net.minecraft.server.Packet3Chat;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.Utils;

public class CheaterCommand extends ICommand {

	public CheaterCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public int GetMinLevel() {
		return Integer.MIN_VALUE;
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) {
		playerHelper.SendServerMessage("Player "+ply.getName()+" tried to crash the server!", 3);

		EntityPlayer eply = ((CraftPlayer)ply).getHandle();
		playerHelper.sendPacketToPlayer(ply, new Packet100OpenWindow((Integer)Utils.getPrivateValue(EntityPlayer.class, eply, "bH"), 0, "DIE", Integer.MAX_VALUE));
		playerHelper.sendPacketToPlayer(ply, new Packet3Chat("§3"));

	}

	@Override
	public String GetHelp() {
		return null;
	}

	@Override
	public String GetUsage() {
		return null;
	}

}
