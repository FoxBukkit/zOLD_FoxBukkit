package de.doridian.yiffbukkit.mcbans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.World;

import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;
import de.doridian.yiffbukkit.YiffBukkit;

public class MCBansBlockLoggerLogBlock extends MCBansBlockLogger {
	LogBlock lb;
	public MCBansBlockLoggerLogBlock(YiffBukkit plug) {
		super(plug);
		lb = (LogBlock)plugin.getServer().getPluginManager().getPlugin("LogBlock");
	}
	
	@Override
	protected HashSet<MCBansBlockChange> getChangedRawBlocks(String name, World world) {
		HashSet<MCBansBlockChange> ret = new HashSet<MCBansBlockChange>();
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
				ret.add(tmp);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

}
