package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.main.commands.ICommand.Level;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet100OpenWindow;
import net.minecraft.server.Packet3Chat;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

@Names("\u00a7")
@Level(Integer.MIN_VALUE)
public class CheaterCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) {
		playerHelper.sendServerMessage("Player "+ply.getName()+" tried to crash the server!", "yiffbukkit.opchat");

		EntityPlayer eply = ((CraftPlayer)ply).getHandle();
		PlayerHelper.sendPacketToPlayer(ply, new Packet100OpenWindow((Integer) Utils.getPrivateValue(EntityPlayer.class, eply, "bH"), 0, "DIE", Integer.MAX_VALUE));
		PlayerHelper.sendPacketToPlayer(ply, new Packet3Chat("\u00a73"));

	}
}
