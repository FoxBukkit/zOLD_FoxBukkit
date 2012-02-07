package de.doridian.yiffbukkit.mcbans;

import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

public class MCBansBlockLoggerLogBlock extends MCBansBlockLogger {
	LogBlock lb;
	public MCBansBlockLoggerLogBlock(YiffBukkit plug) throws Exception {
		super(plug);
		Plugin tmp = plugin.getServer().getPluginManager().getPlugin("LogBlock");
		if(tmp == null) {
			throw new Exception("LogBlock not found!");
		}
		lb = (LogBlock)tmp;
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
			getChangesQuery.needType = true;
			getChangesQuery.needData = false;
			getChangesQuery.needDate = true;
			getChangesQuery.needId = true;
			getChangesQuery.needPlayer = false;
			getChangesQuery.needSignText = false;
			
			Connection conn = lb.getConnection();
			Statement stmt = conn.createStatement();
			
			ResultSet res = stmt.executeQuery(getChangesQuery.getQuery());
			while(res.next()) {
				MCBansBlockChange tmp = new MCBansBlockChange();
				tmp.position = new Location(world, res.getInt("x"), res.getInt("y"), res.getInt("z"));
				tmp.type = res.getInt("type");
				tmp.replaced = res.getInt("replaced");
				tmp.date = res.getDate("date");
				ret.add(tmp);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

}
