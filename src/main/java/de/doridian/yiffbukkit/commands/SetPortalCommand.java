package de.doridian.yiffbukkit.commands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import de.doridian.yiffbukkit.ToolBind;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.portals.PortalEngine;

public class SetPortalCommand extends ICommand {
	public SetPortalCommand(YiffBukkit plug) {
		super(plug);
	}

	@Override
	public int GetMinLevel() {
		return 5;
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		final Material toolType = ply.getItemInHand().getType();

		if (argStr.isEmpty()) {
			playerHelper.addToolMapping(ply, toolType, null);

			playerHelper.SendDirectedMessage(ply, "Unbound your current tool (§e"+toolType.name()+"§f).");

			return;
		}

		final String portalName = args[0];

		ToolBind runnable = new ToolBind("/setportal "+portalName, ply) {
			private Block blockIn;
			private BlockFace blockFaceIn;
			boolean done; // temp

			public void run(PlayerInteractEvent event) {
				Player player = event.getPlayer();

				if (done) { // temp
					PortalEngine.PortalPair portalPair = plugin.portalEngine.portals.get(portalName);
					portalPair.moveThroughPortal(event.getPlayer());

					playerHelper.SendDirectedMessage(player, "Moved through portal");
					return;
				}

				if (blockIn == null) {
					blockIn = event.getClickedBlock();
					blockFaceIn = event.getBlockFace();

					playerHelper.SendDirectedMessage(player, "Stored position for in portal");
				}
				else {
					Block blockOut = event.getClickedBlock();
					BlockFace blockFaceOut = event.getBlockFace();

					plugin.portalEngine.addPortal(portalName, blockIn, blockFaceIn, blockOut, blockFaceOut);

					playerHelper.SendDirectedMessage(player, "Created portal "+portalName);
					done = true;
				}
			}
		};

		playerHelper.addToolMapping(ply, toolType, runnable);

		playerHelper.SendDirectedMessage(ply, "right-click the in and out portals for §9"+portalName+"§f with your current tool (§e"+toolType.name()+"§f).");
	}

	@Override
	public String GetHelp() {
		return "Binds a command to your current tool. The leading slash is optional. Unbind by typing '/bind' without arguments.";
	}

	@Override
	public String GetUsage() {
		return "[<command>[;<command>[;<command> ...]]]";
	}

}
