package com.bukkit.doridian.yiffbukkit;

import java.util.ArrayList;
import java.util.Hashtable;

import com.bukkit.doridian.network.YiffBukkitNetworkManager;
import com.bukkit.doridian.yiffbukkit.commands.*;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.NetworkListenThread;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
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
        
        //commands.put("kick", new KickCommand(plugin));
        //commands.put("ban", new BanCommand(plugin));
        //commands.put("unban", new UnbanCommand(plugin));
        //commands.put("pardon", new UnbanCommand(plugin));
        
        commands.put("tp", new TpCommand(plugin));
        commands.put("summon", new SummonCommand(plugin));
        
        commands.put("notp", new NoTpCommand(plugin));
        commands.put("nosummon", new NoSummonCommand(plugin));
        commands.put("noport", new NoPortCommand(plugin));
        
        commands.put("gonether", new GoNetherCommand(plugin));
        commands.put("home", new HomeCommand(plugin));
        commands.put("sethome", new SetHomeCommand(plugin));
        commands.put("spawn", new SpawnCommand(plugin));
        commands.put("compass", new CompassCommand(plugin));
        
        commands.put("give", new GiveCommand(plugin));
        commands.put("time", new TimeCommand(plugin));
    }
    
    public void onPlayerLogin(PlayerLoginEvent event) {
    	String rank = plugin.playerHelper.GetPlayerRank(event.getPlayer());
    	if(rank.equals("banned")) event.disallow(Result.KICK_BANNED, "[YB] You're banned");

    	replaceNetworkManagers(event.getPlayer());
    }

    private void replaceNetworkManagers(Player ply) {
    	CraftPlayer cply = (CraftPlayer)ply;
    	EntityPlayer eply = cply.getHandle();

    	MinecraftServer srv = eply.b;
    	NetworkListenThread nlt = srv.c;
    	ArrayList<NetLoginHandler> loginHandlerList = Utils.getPrivateValue(NetworkListenThread.class, nlt, "g");
    	for (NetLoginHandler handler : loginHandlerList) {
    		if (handler.b.getClass().equals(YiffBukkitNetworkManager.class))
    			continue;

    		handler.b = new YiffBukkitNetworkManager(handler.b, plugin, ply);
    	}
	}

    public void onPlayerJoin(PlayerEvent event) {
    	plugin.getServer().broadcastMessage("§2[+] §e" + plugin.playerHelper.GetFullPlayerName(event.getPlayer()) + "§e joined!");
    }
    
    public void onPlayerQuit(PlayerEvent event) {
    	plugin.getServer().broadcastMessage("§4[-] §e" + plugin.playerHelper.GetFullPlayerName(event.getPlayer()) + "§e disconnected!");
    }
    
    public void onPlayerKick(PlayerKickEvent event) {
    	plugin.getServer().broadcastMessage("§4[-] §e" + plugin.playerHelper.GetFullPlayerName(event.getPlayer()) + "§e was kicked (reason: " + event.getReason() + ")!");
    }
    
    public void onPlayerMove(PlayerMoveEvent event) {
    	Location pos = event.getTo();
    	Block block = pos.getWorld().getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
    	if(block.getType() == Material.PORTAL) {
        	event.getPlayer().teleportTo(plugin.TogglePlayerWorlds(event.getPlayer(),pos));
        	event.setCancelled(true);
    	}
    }
    
    public void onPlayerChat(PlayerChatEvent event) {
    	String msg = event.getMessage();
    	if(msg.charAt(0) == '!') {
    		this.onPlayerCommand(event);
    		event.setCancelled(true);
    		return;
    	}
    	event.setFormat(plugin.playerHelper.GetPlayerTag(event.getPlayer()) + "%s:§f %s");
    }
    
    public Hashtable<String,ICommand> commands = new Hashtable<String,ICommand>(); 
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

