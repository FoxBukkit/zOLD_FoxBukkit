package de.doridian.yiffbukkit.advanced.commands;

import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.ICommand;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.mcbans.MCBansBlockChange;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissions;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@ICommand.Names({"_ycapi_"})
@ICommand.Help("YC API Command")
@ICommand.Usage("SECRET")
@ICommand.Permission("yiffbukkit.ycapi")
public class YCAPICommand extends ICommand {
	private LogBlock getLogBlock() throws YiffBukkitCommandException {
		Plugin tmp = plugin.getServer().getPluginManager().getPlugin("LogBlock");
		if(tmp == null) {
			throw new YiffBukkitCommandException("LogBlock not found!");
		}
		return (LogBlock)tmp;
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		String arg0 = args[0].toLowerCase();
		if(arg0.equals("co")) { //CheckOff
			String[] plys = YiffBukkitPermissions.checkOffPlayers.toArray(new String[YiffBukkitPermissions.checkOffPlayers.size()]);
			playerHelper.sendYiffcraftClientCommand(ply, 'g', Utils.concatArray(plys, 0, ""));
		} else if(arg0.equals("lbsb")) { //LogBlock Sum Blocks
			try {
				StringBuilder replyPacket = new StringBuilder();
				replyPacket.append(args[1]);

				LogBlock lb = getLogBlock();
				QueryParams getChangesQuery = new QueryParams(lb);
				getChangesQuery.world = ply.getWorld();
				getChangesQuery.setPlayer(args[1]);
				getChangesQuery.bct = QueryParams.BlockChangeType.BOTH;
				getChangesQuery.silent = true;
				getChangesQuery.needType = true;
				getChangesQuery.needData = true;
				getChangesQuery.needDate = false;
				getChangesQuery.needId = false;
				getChangesQuery.needPlayer = false;
				getChangesQuery.needSignText = false;
				getChangesQuery.needChestAccess = false;
				getChangesQuery.sum = QueryParams.SummarizationMode.TYPES;

				Connection conn = lb.getConnection();
				Statement stmt = conn.createStatement();

				ResultSet res = stmt.executeQuery(getChangesQuery.getQuery());
				while(res.next()) {
					replyPacket.append('|');
					replyPacket.append(res.getInt("created"));
					replyPacket.append(';');
					replyPacket.append(res.getInt("destroyed"));
					replyPacket.append(';');
					replyPacket.append(res.getInt("type"));
					replyPacket.append(';');
					replyPacket.append(res.getInt("data"));
				}

				playerHelper.sendYiffcraftClientCommand(ply, 's', replyPacket.toString());
			} catch(Exception e) {
				throw new YiffBukkitCommandException(e.getMessage());
			}
		} else if(arg0.equals("lbca")) { //LogBlock ChestAccess
			try {
				StringBuilder replyPacket = new StringBuilder();
				replyPacket.append(args[1]);

				LogBlock lb = getLogBlock();
				QueryParams getChangesQuery = new QueryParams(lb);
				getChangesQuery.world = ply.getWorld();
				getChangesQuery.setPlayer(args[1]);
				getChangesQuery.bct = QueryParams.BlockChangeType.CHESTACCESS;
				getChangesQuery.silent = true;
				getChangesQuery.needType = false;
				getChangesQuery.needData = false;
				getChangesQuery.needDate = false;
				getChangesQuery.needId = false;
				getChangesQuery.needPlayer = false;
				getChangesQuery.needSignText = false;
				getChangesQuery.needChestAccess = true;

				Connection conn = lb.getConnection();
				Statement stmt = conn.createStatement();

				ResultSet res = stmt.executeQuery(getChangesQuery.getQuery());
				while(res.next()) {
					replyPacket.append('|');
					replyPacket.append(res.getInt("itemtype"));
					replyPacket.append(';');
					replyPacket.append(res.getInt("itemamount"));
					replyPacket.append(';');
					replyPacket.append(res.getInt("itemdata"));
				}

				playerHelper.sendYiffcraftClientCommand(ply, 'a', replyPacket.toString());
			} catch(Exception e) { }
		}
	}
}
