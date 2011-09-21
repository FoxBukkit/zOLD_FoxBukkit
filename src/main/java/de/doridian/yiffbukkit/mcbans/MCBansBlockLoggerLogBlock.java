package de.doridian.yiffbukkit.mcbans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;
import de.diddiz.LogBlock.Session;
import de.doridian.yiffbukkit.YiffBukkit;

public class MCBansBlockLoggerLogBlock extends MCBansBlockLogger {
	LogBlock lb;
	public MCBansBlockLoggerLogBlock(YiffBukkit plug) {
		super(plug);
		lb = (LogBlock)plugin.getServer().getPluginManager().getPlugin("LogBlock");
	}
	
	public HashMap<Location,MCBansBlockChange> getChangedLastBlocks(Player admin) {
		HashMap<Location,MCBansBlockChange> ret = new HashMap<Location,MCBansBlockChange>();
		try {
			Session lbsession = lb.getSession(admin.getName());
			QueryParams params = new QueryParams(lb);
			params.merge(lbsession.lastQuery);
			params.needCoords = true;
			params.limit = -1;
			params.silent = true;
			params.needChestAccess = false;
			params.needSignText = true;
			params.needType = true;
			params.order = QueryParams.Order.DESC;
			params.sum = QueryParams.SummarizationMode.NONE;
			if(params.world == null) params.world = admin.getWorld();
			
			Connection conn = lb.getConnection();
			Statement stmt = conn.createStatement();
			
			ResultSet res = stmt.executeQuery(params.getQuery());
			while(res.next()) {
				MCBansBlockChange tmp = new MCBansBlockChange();
		        int type = res.getInt("type");
		        int replaced = res.getInt("replaced");
				tmp.blockData = res.getInt("data");
				tmp.position = new Location(params.world, res.getInt("x"), res.getInt("y"), res.getInt("z"));
				tmp.action = (type == 0) ? 2 : 1;
				tmp.blockID = (tmp.action == 2) ? replaced : type;
				ret.put(tmp.position,tmp);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	@Override
	protected HashMap<Location,MCBansBlockChange> getChangedRawBlocks(String name, World world) {
		HashMap<Location,MCBansBlockChange> ret = new HashMap<Location,MCBansBlockChange>();
		try {
			QueryParams getChangesQuery = new QueryParams(lb);
			getChangesQuery.world = world;
			getChangesQuery.needCoords = true;
			getChangesQuery.setPlayer(name);
			getChangesQuery.bct = BlockChangeType.BOTH;
			getChangesQuery.silent = true;
			
			Connection conn = lb.getConnection();
			Statement stmt = conn.createStatement();
			
			ResultSet res = stmt.executeQuery(getChangesQuery.getQuery());
			while(res.next()) {
				MCBansBlockChange tmp = new MCBansBlockChange();
		        int type = res.getInt("type");
		        int replaced = res.getInt("replaced");
				tmp.blockData = res.getInt("data");
				tmp.position = new Location(world, res.getInt("x"), res.getInt("y"), res.getInt("z"));
				tmp.action = (type == 0) ? 2 : 1;
				tmp.blockID = (tmp.action == 2) ? replaced : type;
				ret.put(tmp.position,tmp);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

}
