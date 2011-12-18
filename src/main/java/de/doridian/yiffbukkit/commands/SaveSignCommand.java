package de.doridian.yiffbukkit.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;

@Names("savesign")
@Help("Select one sign with worldedit, then run this command to automatically restore this sign each time someone joins or respawns. Removing entries is impossible, so use this on permanent signs only.")
@Permission("yiffbukkit.savesign")
public class SaveSignCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr)
	throws YiffBukkitCommandException {
		args = parseFlags(args);
		LocalSession session = plugin.worldEdit.getSession(ply);

		World world = ply.getWorld();

		final com.sk89q.worldedit.Vector vec;
		try {
			final Region selected = session.getSelection(BukkitUtil.getLocalWorld(ply.getWorld()));
			vec = ((CuboidRegion) selected).getPos1();
		}
		catch (IncompleteRegionException e) {
			throw new YiffBukkitCommandException("Please select a region.", e);
		}

		Location location = new Location(world, vec.getX(), vec.getY(), vec.getZ());

		plugin.signSaver.addSign(location);

		playerHelper.sendDirectedMessage(ply, "Sign at "+location+" will now be restored each time a player spawns.");
	}
}
