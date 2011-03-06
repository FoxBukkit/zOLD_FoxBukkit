package de.doridian.yiffbukkit;

import java.util.Hashtable;

import de.doridian.yiffbukkit.commands.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
//import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author Doridian
 */
public class YiffBukkitPlayerListener extends PlayerListener {
	private final YiffBukkit plugin;

	public YiffBukkitPlayerListener(YiffBukkit instance) {
		plugin = instance;

		commands.put("me", new MeCommand(plugin));
		commands.put("pm", new PmCommand(plugin));

		commands.put("who", new WhoCommand(plugin));
		commands.put("help", new HelpCommand(plugin));

		commands.put("setrank", new SetRankCommand(plugin));
		commands.put("settag", new SetTagCommand(plugin));

		commands.put("kick", new KickCommand(plugin));
		commands.put("ban", new BanCommand(plugin));
		commands.put("unban", new UnbanCommand(plugin));
		commands.put("pardon", new UnbanCommand(plugin));

		commands.put("banish", new BanishCommand(plugin));
		commands.put("vanish", new VanishCommand(plugin));

		commands.put("tp", new TpCommand(plugin));
		commands.put("summon", new SummonCommand(plugin));
		commands.put("send", new SendCommand(plugin));

		commands.put("notp", new NoTpCommand(plugin));
		commands.put("nosummon", new NoSummonCommand(plugin));
		commands.put("noport", new NoPortCommand(plugin));

		commands.put("home", new HomeCommand(plugin));
		commands.put("sethome", new SetHomeCommand(plugin));
		commands.put("spawn", new SpawnCommand(plugin));
		commands.put("compass", new CompassCommand(plugin));

		commands.put("give", new GiveCommand(plugin));
		commands.put("time", new TimeCommand(plugin));
		commands.put("servertime", new ServerTimeCommand(plugin));
		
		commands.put("reloadads", new ReloadAdsCommand(plugin));
		
		commands.put("warp", new WarpCommand(plugin));
		commands.put("setwarp", new SetWarpCommand(plugin));
		
		commands.put("jail", new JailCommand(plugin));
		commands.put("setjail", new SetJailCommand(plugin));
		
		commands.put("§", new CheaterCommand(plugin));
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		String rank = plugin.playerHelper.GetPlayerRank(event.getPlayer());
		if(rank.equals("banned")) event.disallow(Result.KICK_BANNED, "[YB] You're banned");
	}

	@Override
	public void onPlayerJoin(PlayerEvent event) {
		plugin.getServer().broadcastMessage("§2[+] §e" + plugin.playerHelper.GetFullPlayerName(event.getPlayer()) + "§e joined!");
	}

	@Override
	public void onPlayerQuit(PlayerEvent event) {
		plugin.getServer().broadcastMessage("§4[-] §e" + plugin.playerHelper.GetFullPlayerName(event.getPlayer()) + "§e disconnected!");
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		plugin.getServer().broadcastMessage("§4[-] §e" + plugin.playerHelper.GetFullPlayerName(event.getPlayer()) + "§e was kicked (reason: " + event.getReason() + ")!");
	}

	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		Player ply = event.getPlayer();
		if(ply.getHealth() <= 0) {
			event.setCancelled(true);
			return;
		}
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		String msg = event.getMessage();
		if(msg.charAt(0) == '!') {
			//onPlayerCommand(event);
			plugin.playerHelper.SendDirectedMessage(event.getPlayer(), "!commands are disabled because they show up in the web chat. Please use /commands.");
			event.setCancelled(true);
			return;
		}
		event.setFormat(plugin.playerHelper.GetPlayerTag(event.getPlayer()) + "%s:§f %s");
	}

	public Hashtable<String,ICommand> commands = new Hashtable<String,ICommand>();
	@Override
	public void onPlayerCommand(PlayerChatEvent event) {
		String baseCmd = event.getMessage().trim().substring(1);
		int posSpace = baseCmd.indexOf(' ');
		String cmd; String args[]; String argStr;
		if(posSpace < 0) {
			cmd = baseCmd;
			args = new String[0];
			argStr = "";
		} else {
			cmd = baseCmd.substring(0, posSpace).trim();
			argStr = baseCmd.substring(posSpace).trim();
			args = argStr.split(" ");
		}
		if(commands.containsKey(cmd)) {
			event.setCancelled(true);
			Player ply = event.getPlayer();
			ICommand icmd = commands.get(cmd);
			if(icmd.GetMinLevel() > plugin.playerHelper.GetPlayerLevel(ply)) {
				plugin.playerHelper.SendPermissionDenied(ply);
				return;
			}
			try {
				icmd.Run(ply,args,argStr);
			}
			catch(Exception e) {
				plugin.playerHelper.SendDirectedMessage(ply,"Command error!");
			}
		}
	}
}

