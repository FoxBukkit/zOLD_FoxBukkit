package de.doridian.yiffbukkit.commands;

import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.mcbans.MCBansUtil;

@Names("lookup")
@Help("Does an MCBans lookup on target player")
@Usage("<name>")
@Level(3)
@Permission("yiffbukkit.users.lookup")
public class LookupCommand extends ICommand {
	@Override
	public void run(final CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		if (args.length < 1)
			throw new YiffBukkitCommandException("Argument expected.");

		final String otherName = playerHelper.completePlayerName(args[0], true);

		new Thread() {
			public void run() {
				JSONObject lookupret = MCBansUtil.apiQuery("exec=lookup_user&player="+MCBansUtil.URLEncode(otherName));
				//{"ban_num":2,"ban_rep":10,"ban_reasons_local":
				//	["mc.agrogamerz.com .:. No reason specified",
				//	"PGenesis.com .:. Speedhacking, flyhacking, despite repeated warnings with HeroicRebuke"],"ban_reasons_global":[]
				//}

				playerHelper.sendDirectedMessage(commandSender, "Player §3" + otherName + "§f has §4" + lookupret.get("ban_num").toString() + " ban(s)§f and §9"+lookupret.get("ban_rep").toString()+" REP§f.");

				final JSONArray banReasonsGlobal = (JSONArray)lookupret.get("ban_reasons_global");
				if (!banReasonsGlobal.isEmpty()) {
					playerHelper.sendDirectedMessage(commandSender, "§4Global bans");
					for(Object obj : banReasonsGlobal) {
						playerHelper.sendDirectedMessage(commandSender, (String)obj);
					}
				}

				final JSONArray banReasonsLocal = (JSONArray)lookupret.get("ban_reasons_local");
				if (!banReasonsLocal.isEmpty()) {
					playerHelper.sendDirectedMessage(commandSender, "§6Local bans");
					for(Object obj : banReasonsLocal) {
						playerHelper.sendDirectedMessage(commandSender, (String)obj);
					}
				}
			}
		}.start();
	}
}