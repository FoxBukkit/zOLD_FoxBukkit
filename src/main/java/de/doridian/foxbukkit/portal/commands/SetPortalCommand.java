/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.foxbukkit.portal.commands;

import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.main.ToolBind;
import de.doridian.foxbukkit.main.commands.BindCommand;
import de.doridian.foxbukkit.main.commands.system.ICommand;
import de.doridian.foxbukkit.main.commands.system.ICommand.BooleanFlags;
import de.doridian.foxbukkit.main.commands.system.ICommand.Help;
import de.doridian.foxbukkit.main.commands.system.ICommand.Names;
import de.doridian.foxbukkit.main.commands.system.ICommand.Permission;
import de.doridian.foxbukkit.portal.PortalEngine;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

@Names("setportal")
@Help("Binds a command to your current tool. The leading slash is optional. Unbind by typing '/bind' without arguments.")
@Permission("foxbukkit.useless.setportal")
@BooleanFlags("x")
public class SetPortalCommand extends ICommand {
	@Override
	public void Run(Player ply, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		final Material toolType = ply.getItemInHand().getType();

		boolean left = booleanFlags.contains('x');

		if (argStr.isEmpty()) {
			BindCommand.unbind(ply, toolType, left);
			return;
		}

		final String portalName = args[0];

		ToolBind runnable = new ToolBind("/setportal "+portalName, ply) {
			private Block blockIn;
			private BlockFace blockFaceIn;
			boolean done; // temp

			@Override
			public boolean run(PlayerInteractEvent event) {
				Player player = event.getPlayer();

				if (done) { // temp
					PortalEngine.PortalPair portalPair = plugin.portalEngine.portals.get(portalName);
					portalPair.moveThroughPortal(event.getPlayer());

					PlayerHelper.sendDirectedMessage(player, "Moved through portal");
					return true;
				}

				if (blockIn == null) {
					blockIn = event.getClickedBlock();
					blockFaceIn = event.getBlockFace();

					PlayerHelper.sendDirectedMessage(player, "Stored position for in portal");
				}
				else {
					Block blockOut = event.getClickedBlock();
					BlockFace blockFaceOut = event.getBlockFace();

					plugin.portalEngine.addPortal(portalName, blockIn, blockFaceIn, blockOut, blockFaceOut);

					PlayerHelper.sendDirectedMessage(player, "Created portal "+portalName);
					done = true;
				}
				return true;
			}
		};

		ToolBind.add(ply, toolType, left, runnable);

		PlayerHelper.sendDirectedMessage(ply, "right-click the in and out portals for \u00a79"+portalName+"\u00a7f with your current tool (\u00a7e"+toolType.name()+"\u00a7f).");
	}
}
