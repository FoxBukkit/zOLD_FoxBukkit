package de.doridian.yiffbukkit.permissions;

import de.doridian.yiffbukkit.main.config.ConfigFileReader;
import de.doridian.yiffbukkit.main.config.ConfigFileWriter;
import de.doridian.yiffbukkit.main.util.ZooKeeperManager;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import org.apache.zookeeper.ZooKeeper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class YiffBukkitPermissionHandler {
	public static final YiffBukkitPermissionHandler instance = new YiffBukkitPermissionHandler();

	class GroupWorld {
		public final String group;
		public final String world;
		
		public GroupWorld(String group, String world) {
			this.group = group;
			this.world = world;
		}
		
		@Override
		public boolean equals(Object other) {
			return (other instanceof  GroupWorld) && equals((GroupWorld)other);
		}

		public boolean equals(GroupWorld other) {
			return other.group.equals(this.group) && other.world.equals(this.world);
		}
		
		@Override
		public int hashCode() {
			return (group.hashCode() / 2) + (world.hashCode() / 2);
		}
	}

	private boolean loaded = false;
	private final Map<String,String> playerGroups = ZooKeeperManager.createKeptMap("playergroups");
	private final HashMap<GroupWorld,HashSet<String>> groupPermissions = new HashMap<GroupWorld,HashSet<String>>();
	private final HashMap<GroupWorld,HashSet<String>> groupProhibitions = new HashMap<GroupWorld,HashSet<String>>();
	
	private String defaultWorld = "world";

	public void setDefaultWorld(String world) {
		defaultWorld = world;
	}

	public void load() {
		if(loaded) return;
		reload();
	}

	public void reload() {
		loaded = true;
		groupPermissions.clear();
		groupProhibitions.clear();
		playerGroups.clear();

		final File permissionsDirectory = new File(YiffBukkit.instance.getDataFolder() + "/permissions");
		permissionsDirectory.mkdirs();
		File[] files = permissionsDirectory.listFiles();

		BufferedReader reader;
		for(File file : files) {
			try {
				String currentWorld;
				GroupWorld currentGroupWorld = null;
				currentWorld = file.getName();
				if(currentWorld.indexOf('.') > 0) {
					currentWorld = currentWorld.substring(0, currentWorld.indexOf('.'));
				}
				HashSet<String> currentPermissions = null;
				HashSet<String> currentProhibitions = null;
				reader = new BufferedReader(new FileReader(file));
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
						if(currentGroupWorld != null) {
							groupPermissions.put(currentGroupWorld, currentPermissions);
							groupProhibitions.put(currentGroupWorld, currentProhibitions);
						}
						int i = line.indexOf(' ');
						currentPermissions = new HashSet<String>();
						currentProhibitions = new HashSet<String>();
						if(i > 0) {
							currentGroupWorld = new GroupWorld(line.substring(0, i).trim(), currentWorld);
							GroupWorld tmp = new GroupWorld(line.substring(i+1).trim(), currentWorld);
							currentPermissions.addAll(groupPermissions.get(tmp));
							currentProhibitions.addAll(groupProhibitions.get(tmp));
						} else {
							currentGroupWorld = new GroupWorld(line, currentWorld);
						}
					}
				}
				if(currentGroupWorld != null) {
					groupPermissions.put(currentGroupWorld, currentPermissions);
					groupProhibitions.put(currentGroupWorld, currentProhibitions);
				}
				reader.close();
			}
			catch(Exception e) { e.printStackTrace(); }
		}
		try {
			reader = new BufferedReader(new ConfigFileReader("player-groups.txt"));
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
			BufferedWriter writer = new BufferedWriter(new ConfigFileWriter("player-groups.txt"));
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

	public boolean has(CommandSender commandSender, String permission) {
		// Console can do everything
		return (!(commandSender instanceof Player)) || has((Player) commandSender, permission);
	}

	public boolean has(Player player, String permission) {
		return has(player.getWorld().getName(), player.getName(), permission);
	}

	public boolean has(String worldName, String playerName, String permission) {
		playerName = playerName.toLowerCase();
		permission = permission.toLowerCase();
		GroupWorld currentGroupWorld = new GroupWorld(getGroup(playerName), worldName);

		HashSet<String> currentPermissions = groupPermissions.get(currentGroupWorld);
		if(currentPermissions == null) {
			currentGroupWorld = new GroupWorld(currentGroupWorld.group, defaultWorld);
			currentPermissions = groupPermissions.get(currentGroupWorld);
			if(currentPermissions == null) return false;
		}
		if(currentPermissions.contains(permission)) return true;

		HashSet<String> currentProhibitions = groupProhibitions.get(currentGroupWorld);
		if(currentProhibitions != null && currentProhibitions.contains(permission)) return false;

		int xpos = 0;
		String tperm = permission;
		while((xpos = tperm.lastIndexOf('.')) > 0) {
			tperm = tperm.substring(0, xpos);
			String tperm2 = tperm + ".*";
			if(currentProhibitions != null && currentProhibitions.contains(tperm2)) { currentProhibitions.add(permission); return false; }
			if(currentPermissions.contains(tperm2)) { currentPermissions.add(permission); return true; }
		}

		if(currentProhibitions != null && currentProhibitions.contains("*")) { currentProhibitions.add(permission); return false; }
		if(currentPermissions.contains("*")) { currentPermissions.add(permission); return true; }

		if(currentProhibitions == null) {
			currentProhibitions = new HashSet<String>();
			groupProhibitions.put(currentGroupWorld, currentProhibitions);
		}
		currentProhibitions.add(permission);
		return false;
	}

	public boolean has(String playerName, String permission) {
		return has(defaultWorld, playerName, permission);
	}

	public String getGroup(String name) {
		name = name.toLowerCase();
		return playerGroups.containsKey(name) ? playerGroups.get(name) : "guest";
	}

	public void setGroup(String name, String group) {
		group = group.toLowerCase();
		playerGroups.put(name.toLowerCase(), group);
		save();
	}

	public boolean inGroup(String world, String name, String group) {
		return getGroup(name).equalsIgnoreCase(group);
	}

	public boolean inGroup(String name, String group) {
		return inGroup(defaultWorld, name, group);
	}
}