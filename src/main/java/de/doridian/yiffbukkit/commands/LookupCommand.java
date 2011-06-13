package de.doridian.yiffbukkit.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.Help;
import de.doridian.yiffbukkit.commands.ICommand.Level;
import de.doridian.yiffbukkit.commands.ICommand.Names;
import de.doridian.yiffbukkit.commands.ICommand.Usage;
import de.doridian.yiffbukkit.mcbans.MCBansUtil;
import de.doridian.yiffbukkit.offlinebukkit.OfflinePlayer;

@Names("lookup")
@Help("Does an MCBans lookup on target player")
@Usage("<name>")
@Level(3) 
public class LookupCommand extends ICommand {
	@Override
	public void run(CommandSender from, String[] args, String argStr) throws YiffBukkitCommandException {
		final CommandSender commandSender = from;
		
		Matcher matcher = Pattern.compile("^\"(.*)\"$").matcher(args[0]);
		
		final Player otherply;
		if (matcher.matches())
			otherply = new OfflinePlayer(plugin.getServer(), plugin.getOrCreateWorld("world", Environment.NORMAL), matcher.group(1));
		else
			otherply = playerHelper.matchPlayerSingle(args[0]);
		
		new Thread() {
			public void run() {
				JSONObject lookupret = MCBansUtil.apiQuery("exec=lookup_user&player="+MCBansUtil.URLEncode(otherply.getName()));
				//{"ban_num":2,"ban_rep":10,"ban_reasons_local":
				//	["mc.agrogamerz.com .:. No reason specified",
				//	"PGenesis.com .:. Speedhacking, flyhacking, despite repeated warnings with HeroicRebuke"],"ban_reasons_global":[]
				//}
				
				plugin.playerHelper.sendDirectedMessage(commandSender, "Player §3" + otherply.getName() + "§f has §4" + lookupret.get("ban_num").toString() + " ban(s)§f and §9"+lookupret.get("ban_rep").toString()+" REP§f.");
				plugin.playerHelper.sendDirectedMessage(commandSender, "§4Global bans");
				dispBanArray(commandSender, (JSONArray)lookupret.get("ban_reasons_global"));
				plugin.playerHelper.sendDirectedMessage(commandSender, "§6Local bans");
				dispBanArray(commandSender, (JSONArray)lookupret.get("ban_reasons_local"));
			}
		}.start();
	}
	
	private void dispBanArray(CommandSender commandSender, JSONArray array) {
		boolean has = false;
		for(Object obj : array) {
			has = true;
			plugin.playerHelper.sendDirectedMessage(commandSender, (String)obj);
		}
		if(!has) plugin.playerHelper.sendDirectedMessage(commandSender, "None");
	}
}