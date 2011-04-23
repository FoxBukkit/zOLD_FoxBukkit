package de.doridian.yiffbukkit.commands;

import net.minecraft.server.Packet53BlockChange;
import net.minecraft.server.Packet54PlayNoteBlock;
//import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitPlayerListener;

public class NoteCommand extends ICommand {

	@Override
	public int GetMinLevel() {
		return 4;
	}

	public NoteCommand(YiffBukkitPlayerListener playerListener) {
		super(playerListener);
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) {
		/*
		CraftPlayer cply = (CraftPlayer)ply;
		//World world = ply.getWorld();
		//CraftServer cserver = (CraftServer)plugin.getServer();
		cply.getHandle().a.b(new Packet1Login("","",ply.getEntityId(),0,(byte)ply.getWorld().getEnvironment().ordinal()));
		 */

		Location loc = ply.getLocation();
		World world = loc.getWorld();
		CraftWorld cworld = (CraftWorld)world;
		net.minecraft.server.World notchWorld = cworld.getHandle();


		int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
		int instrument = Integer.valueOf(args[0]);
		int note = Integer.valueOf(args[1]);

		Packet53BlockChange p53 = new Packet53BlockChange(x, y, z, notchWorld);
		p53.d = 25;
		playerHelper.sendPacketToPlayer(ply, p53);
		Packet54PlayNoteBlock p54 = new Packet54PlayNoteBlock(x, y, z, instrument, note);
		playerHelper.sendPacketToPlayer(ply, p54);
		playerHelper.sendPacketToPlayer(ply, p54);
		/*
		for (int i = 0; i < 128; ++i) {
			note = (int)(Math.random()*24);
			eply.a.b(new Packet54PlayNoteBlock(x, y, z, instrument, note));
		}
		 */
		playerHelper.sendPacketToPlayer(ply, new Packet53BlockChange(x, y, z, notchWorld));

		playerHelper.SendDirectedMessage(ply, "sent note "+instrument + "/" + note);
	}
}
