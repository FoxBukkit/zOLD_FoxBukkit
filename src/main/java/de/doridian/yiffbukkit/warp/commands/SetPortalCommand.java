package de.doridian.yiffbukkit.warp.commands;

import de.doridian.yiffbukkit.main.ToolBind;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.commands.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.ICommand.Permission;
import de.doridian.yiffbukkit.warp.portals.PortalEngine;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

@Names("setportal")
@Help("Binds a command to your current tool. The leading slash is optional. Unbind by typing '/bind' without arguments.")
@Permission("yiffbukkitsplit.useless.setportal")
public class SetPortalCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		final Material toolType = ply.getItemInHand().getType();

		if (argStr.isEmpty()) {
			playerHelper.addToolMapping(ply, toolType, null);

			playerHelper.sendDirectedMessage(ply, "Unbound your current tool (\u00a7e"+toolType.name()+"\u00a7f).");

			return;
		}

		final String portalName = args[0];

		ToolBind runnable = new ToolBind("/setportal "+portalName, ply) {
			private Block blockIn;
			private BlockFace blockFaceIn;
			boolean done; // temp

			@Override
			public void run(PlayerInteractEvent event) {
				Player player = event.getPlayer();

				if (done) { // temp
					PortalEngine.PortalPair portalPair = plugin.portalEngine.portals.get(portalName);
					portalPair.moveThroughPortal(event.getPlayer());

					playerHelper.sendDirectedMessage(player, "Moved through portal");
					return;
				}

				if (blockIn == null) {
					blockIn = event.getClickedBlock();
					blockFaceIn = event.getBlockFace();

					playerHelper.sendDirectedMessage(player, "Stored position for in portal");
				}
				else {
					Block blockOut = event.getClickedBlock();
					BlockFace blockFaceOut = event.getBlockFace();

					plugin.portalEngine.addPortal(portalName, blockIn, blockFaceIn, blockOut, blockFaceOut);

					playerHelper.sendDirectedMessage(player, "Created portal "+portalName);
					done = true;
				}
			}
		};

		playerHelper.addToolMapping(ply, toolType, runnable);

		playerHelper.sendDirectedMessage(ply, "right-click the in and out portals for \u00a79"+portalName+"\u00a7f with your current tool (\u00a7e"+toolType.name()+"\u00a7f).");
	}
}
