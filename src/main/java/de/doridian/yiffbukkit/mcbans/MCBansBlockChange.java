package de.doridian.yiffbukkit.mcbans;

import java.sql.Date;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class MCBansBlockChange {
	public Location position;
	public Date date = new Date(0);
	public int type = 0;
	public int replaced = 0;
	
	public MCBansBlockChange() {
		
	}
	
	public MCBansBlockChange(Block block) {
		position = block.getLocation();
		type = block.getTypeId();
		replaced = type;
		date = new Date(0);
	}
	
	@Override
	public String toString() {
		return toString(0, 0, 0);
	}
	
	public String toString(int centerX, int centerY, int centerZ) {
		return "{d="+date.getTime()+", x="+(position.getBlockX() - centerX)+", y="+(position.getBlockY() - centerY)+", z="+(position.getBlockZ() - centerZ)+", t="+type+", r="+replaced+"}";
	}
	
	@Override
	public int hashCode() {
		return position.hashCode();
	}
}
