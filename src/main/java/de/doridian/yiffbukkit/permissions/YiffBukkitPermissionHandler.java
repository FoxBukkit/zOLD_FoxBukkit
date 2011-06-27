package de.doridian.yiffbukkit.permissions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;

public class YiffBukkitPermissionHandler extends PermissionHandler {
	private boolean loaded = false;
	private final HashMap<String,String> playerGroups = new HashMap<String,String>();
	private final HashMap<String,HashSet<String>> groupPermissions = new HashMap<String,HashSet<String>>();
	private final HashMap<String,HashSet<String>> groupProhibitions = new HashMap<String,HashSet<String>>();
	
	@Override
	public void setDefaultWorld(String world) {
		
	}

	@Override
	public boolean loadWorld(String world) {
		load();
		return true;
	}

	@Override
	public void forceLoadWorld(String world) {
		load();
	}

	@Override
	public boolean checkWorld(String world) {
		return true;
	}

	@Override
	public void load() {
		if(loaded) return;
		reload();
	}

	@Override
	public void load(String world, Configuration config) {
		load();
	}

	@Override
	public void reload() {
		loaded = true;
		groupPermissions.clear();
		groupProhibitions.clear();
		playerGroups.clear();
		BufferedReader reader;
		try {
			String currentGroup = null;
			HashSet<String> currentPermissions = null;
			HashSet<String> currentProhibitions = null;
			reader = new BufferedReader(new FileReader("group-permissions.txt"));
			String line;
			while((line = reader.readLine()) != null) {
				line = line.trim().toLowerCase();
				if(line.length() < 1) continue;
				char c = line.charAt(0);
				if(c == '-') {
					line = line.substring(1).trim();
					currentPermissions.remove(line);
					currentProhibitions.add(line);
				} else if(c == '+') {
					line = line.substring(1).trim();
					currentPermissions.add(line);
					currentProhibitions.remove(line);
				} else {
					if(currentGroup != null) {
						groupPermissions.put(currentGroup, currentPermissions);
						groupProhibitions.put(currentGroup, currentProhibitions);
					}
					int i = line.indexOf(' ');
					currentPermissions = new HashSet<String>();
					currentProhibitions = new HashSet<String>();
					if(i > 0) {
						currentGroup = line.substring(0, i).trim();
						line = line.substring(i+1).trim();
						currentPermissions.addAll(groupPermissions.get(line));
						currentProhibitions.addAll(groupProhibitions.get(line));
					} else {
						currentGroup = line;
					}
				}
			}
			if(currentGroup != null) {
				groupPermissions.put(currentGroup, currentPermissions);
				groupProhibitions.put(currentGroup, currentProhibitions);
			}
			reader.close();
		}
		catch(Exception e) { e.printStackTrace(); }
		try {
			reader = new BufferedReader(new FileReader("player-groups.txt"));
			String line; int lpos;
			while((line = reader.readLine()) != null) {
				line = line.toLowerCase();
				lpos = line.lastIndexOf('=');
				if(lpos < 0) continue;
				playerGroups.put(line.substring(0,lpos), line.substring(lpos+1));
			}
			reader.close();
		}
		catch(Exception e) { }
	}
	
	public void save() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("player-groups.txt"));
			Set<String> e = playerGroups.keySet();
			for(String key : e) {
				String value = playerGroups.get(key);
				if(value.equals("guest")) continue;
				writer.write(key + "=" + value);
				writer.newLine();
			}
			writer.close();
		}
		catch(Exception e) { }
	}

	@Override
	public boolean reload(String world) {
		reload();
		return true;
	}

	@Override
	public void setCache(String world, Map<String, Boolean> Cache) {

	}

	@Override
	public void setCacheItem(String world, String player, String permission, boolean data) {

	}

	@Override
	public Map<String, Boolean> getCache(String world) {
		return null;
	}

	@Override
	public boolean getCacheItem(String world, String player, String permission) {
		return false;
	}

	@Override
	public void removeCachedItem(String world, String player, String permission) {

	}

	@Override
	public void clearCache(String world) {

	}

	@Override
	public void clearAllCache() {

	}

	@Override
	public boolean has(Player player, String permission) {
		return permission(player, permission);
	}

	@Override
	public boolean has(String worldName, String playerName, String permission) {
		return permission(worldName, playerName, permission);
	}

	@Override
	public boolean permission(Player player, String permission) {
		return permission(player.getName(), permission);
	}

	@Override
	public boolean permission(String worldName, String playerName, String permission) {
		return permission(playerName, permission);
	}
	
	public boolean permission(String playerName, String permission) {
		playerName = playerName.toLowerCase();
		permission = permission.toLowerCase();
		HashSet<String> currentPermissions = groupPermissions.get(getGroup(playerName));
		if(currentPermissions == null) return false;
		HashSet<String> currentProhibitions = groupProhibitions.get(getGroup(playerName));
		if(currentProhibitions != null && currentProhibitions.contains(permission)) return false;
		if(currentPermissions.contains(permission)) return true;
		
		boolean notFirstLoop = false;
		int xpos = 0;
		String tperm = permission;
		do {
			if(notFirstLoop) {
				tperm = tperm.substring(0, xpos);
				String tperm2 = tperm + ".*";
				if(currentProhibitions != null && currentProhibitions.contains(tperm2)) { currentProhibitions.add(permission); return false; }
				if(currentPermissions.contains(tperm2)) { currentPermissions.add(permission); return true; }
			} else {
				notFirstLoop = true;
			}
		} while((xpos = tperm.lastIndexOf('.')) > 0);
		
		//if(currentProhibitions != null && currentProhibitions.contains("*")) { currentProhibitions.add(permission); return false; }
		if(currentPermissions.contains("*")) { currentPermissions.add(permission); return true; }
		
		currentProhibitions.add(permission);
		return false;
	}

	@Override
	public String getGroup(String world, String name) {
		return getGroup(name);
	}
	
	public String getGroup(String name) {
		name = name.toLowerCase();
		return playerGroups.containsKey(name) ? playerGroups.get(name) : "guest";
	}
	
	public void setGroup(String name, String group) {
		group = group.toLowerCase();
		if(!groupPermissions.containsKey(group)) return;
		playerGroups.put(name.toLowerCase(), group);
		save();
	}

	@Override
	public String[] getGroups(String world, String name) {
		return new String[] { getGroup(world,name) };
	}

	@Override
	public boolean inGroup(String world, String name, String group) {
		return inGroup(name, group);
	}

	@Override
	public boolean inSingleGroup(String world, String name, String group) {
		return inGroup(world, name, group);
	}
	
	@Override
	public boolean inGroup(String name, String group) {
		return inSingleGroup(name, group);
	}

	@Override
	public boolean inSingleGroup(String name, String group) {
		return getGroup(name).equalsIgnoreCase(group);
	}

	@Override
	public String getGroupPrefix(String world, String group) {
		return "";
	}

	@Override
	public String getGroupSuffix(String world, String group) {
		return "";
	}

	@Override
	public boolean canGroupBuild(String world, String group) {
		return true;
	}

	@Override
	public String getGroupPermissionString(String world, String group, String permission) {
		return "";
	}

	@Override
	public int getGroupPermissionInteger(String world, String group, String permission) {
		return 0;
	}

	@Override
	public boolean getGroupPermissionBoolean(String world, String group, String permission) {
		return false;
	}

	@Override
	public double getGroupPermissionDouble(String world, String group, String permission) {
		return 0;
	}

	@Override
	public String getUserPermissionString(String world, String name, String permission) {
		return null;
	}

	@Override
	public int getUserPermissionInteger(String world, String name, String permission) {
		return 0;
	}

	@Override
	public boolean getUserPermissionBoolean(String world, String name, String permission) {
		return false;
	}

	@Override
	public double getUserPermissionDouble(String world, String name, String permission) {
		return 0;
	}

	@Override
	public String getPermissionString(String world, String name, String permission) {
		return null;
	}

	@Override
	public int getPermissionInteger(String world, String name, String permission) {
		return 0;
	}

	@Override
	public boolean getPermissionBoolean(String world, String name, String permission) {
		return false;
	}

	@Override
	public double getPermissionDouble(String world, String name, String permission) {
		return 0;
	}

	@Override
	public void addGroupInfo(String world, String group, String node, Object data) {

	}

	@Override
	public void removeGroupInfo(String world, String group, String node) {

	}

	@Override
	public void addUserPermission(String world, String user, String node) {

	}

	@Override
	public void removeUserPermission(String world, String user, String node) {

	}

	@Override
	public void save(String world) {

	}

	@Override
	public void saveAll() {

	}
}