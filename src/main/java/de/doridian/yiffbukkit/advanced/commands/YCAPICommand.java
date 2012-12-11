package de.doridian.yiffbukkit.advanced.commands;

import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissions;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Names("_ycapi_")
@Help("YC API Command")
@Usage("SECRET")
@Permission("yiffbukkit.ycapi")
public class YCAPICommand extends ICommand {
	private LogBlock getLogBlock() throws YiffBukkitCommandException {
		Plugin tmp = plugin.getServer().getPluginManager().getPlugin("LogBlock");
		if (tmp == null) {
			throw new YiffBukkitCommandException("LogBlock not found!");
		}
		return (LogBlock) tmp;
	}

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		String arg0 = args[0].toLowerCase();
		if (arg0.equals("co")) { // CheckOff
			String[] plys = YiffBukkitPermissions.checkOffPlayers.toArray(new String[YiffBukkitPermissions.checkOffPlayers.size()]);
			if(plys.length > 0) {
				StringBuilder sb = new StringBuilder();
				for(String plyName : plys) {
					sb.append(' ');
					OfflinePlayer plyOther = plugin.getServer().getOfflinePlayer(plyName);
					if(plyOther != null && plyOther.isOnline()) {
						sb.append('1');
					} else {
						sb.append('0');
					}
					sb.append(plyOther.getName());
				}
				arg0 = sb.substring(1);
			} else {
				arg0 = "";
			}
			playerHelper.sendYiffcraftClientCommand(ply, 'g', arg0);
		} else if (arg0.equals("lbsb")) { // LogBlock Sum Blocks
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
				getChangesQuery.needData = false;
				getChangesQuery.needDate = false;
				getChangesQuery.needId = false;
				getChangesQuery.needPlayer = false;
				getChangesQuery.needSignText = false;
				getChangesQuery.needChestAccess = false;
				getChangesQuery.sum = QueryParams.SummarizationMode.TYPES;

				Connection conn = lb.getConnection();
				Statement stmt = conn.createStatement();

				ResultSet res = stmt.executeQuery(getChangesQuery.getQuery());
				while (res.next()) {
					replyPacket.append('|');
					replyPacket.append(res.getInt("created"));
					replyPacket.append(';');
					replyPacket.append(res.getInt("destroyed"));
					replyPacket.append(';');
					replyPacket.append(res.getInt("type"));
				}

				conn.close();

				playerHelper.sendYiffcraftClientCommand(ply, 's', replyPacket.toString());
			} catch (Exception e) {
				throw new YiffBukkitCommandException(e.getMessage());
			}
		} else if (arg0.equals("lbca")) { // LogBlock ChestAccess
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
				getChangesQuery.needDate = true;
				getChangesQuery.needId = false;
				getChangesQuery.needPlayer = false;
				getChangesQuery.needSignText = false;
				getChangesQuery.needChestAccess = true;

				Connection conn = lb.getConnection();
				Statement stmt = conn.createStatement();

				ResultSet res = stmt.executeQuery(getChangesQuery.getQuery());
				while (res.next()) {
					replyPacket.append('|');
					replyPacket.append(res.getInt("itemtype"));
					replyPacket.append(';');
					replyPacket.append(res.getInt("itemamount"));
					replyPacket.append(';');
					replyPacket.append(res.getInt("itemdata"));
					replyPacket.append(';');
					replyPacket.append(res.getString("date"));
				}

				conn.close();

				playerHelper.sendYiffcraftClientCommand(ply, 'a', replyPacket.toString());
			} catch (Exception e) { }
		}
	}
}
