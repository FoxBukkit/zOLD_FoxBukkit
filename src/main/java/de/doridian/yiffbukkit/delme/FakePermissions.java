package de.doridian.yiffbukkit.delme;

import org.bukkit.command.CommandSender;

public class FakePermissions {
	public static boolean has(CommandSender sender, String permission) {
		return sender.isOp() || sender.hasPermission(permission);
	}

	public static String getGroup(String name) {
		return "guest";
	}

	public static void setGroup(String name, String rankname) {
		// TODO Auto-generated method stub
		
	}

	public static void reload() {
		// TODO Auto-generated method stub
		
	}
}
