package de.doridian.yiffbukkit.irc;

import java.io.IOException;

import org.jibble.pircbot.*;
import de.doridian.yiffbukkit.YiffBukkit;

public class Ircbot extends PircBot implements Runnable {

	private YiffBukkit plugin;

    public Ircbot(YiffBukkit plug) {
        this.plugin = plug;
    }

    public synchronized Ircbot init() {
        this.setMessageDelay(1000);
        this.setName("YiffBot");
        this.setFinger("YiffBukkit");
        this.setLogin("YiffBukkit");
        this.setVersion("YiffBukkit");

        try {
            this.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public void start() {
        try {
            this.setAutoNickChange(true);
            this.connect("irc.esper.net", 6667);
        	this.changeNick("YiffBot");
        	this.identify("SECRET");
            try {
        		Thread.sleep(2000);
        	} catch (InterruptedException e) {
        		e.printStackTrace();
        	}
            this.joinChannel("#doriminecraft");
            this.joinChannel("#doristaff");
            this.joinChannel("#zidonuke");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IrcException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void sendToPublicChannel(String msg)
    {
    	this.sendMessage("#doriminecraft", msg);
    }
    
    public void sendToChannel(String msg)
    {
    	this.sendMessage("#doriminecraft", msg);
    	this.sendMessage("#doristaff", msg);
    }
    
    public void sendToStaffChannel(String msg)
    {
    	this.sendMessage("#doristaff", msg);
    }

    public void onJoin(String channel, String sender, String login, String hostname) {
    	if(channel.equals("#doriminecraft"))
    		plugin.getServer().broadcastMessage("§a[+] §e" + sender + "@IRC§e joined!");
    	else if(channel.equals("#doristaff"))
    		plugin.playerHelper.sendServerMessage("§e[OP]§a[+] §e" + sender + "@IRC§e joined!", 3); 
    }
    
    public void onPart(String channel, String sender, String login, String hostname) {
    	if(channel.equals("#doriminecraft"))
    		plugin.getServer().broadcastMessage("§c[-] §e" + sender + "@IRC§e left!");
    	else if(channel.equals("#doristaff"))
    		plugin.playerHelper.sendServerMessage("§e[OP]§c[-] §e" + sender + "@IRC§e left!", 3);
    }
    
    public void onQuit(String sender, String login, String hostname, String reason) {
    	plugin.getServer().broadcastMessage("§c[-] §e" + sender + "@IRC§e disconnected (" + reason + ")!");;
    }
    
    public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname,
            String recipientNick, String reason) {
        if (recipientNick.equalsIgnoreCase(this.getNick())) {
            this.joinChannel(channel);
        }
        if(channel.equals("#doriminecraft"))
        	plugin.getServer().broadcastMessage("§c[-] §e" + recipientNick + "@IRC§e was kicked (" + reason + ")!");
        else if(channel.equals("#doristaff"))
        	plugin.playerHelper.sendServerMessage("§e[OP]§c[-] §e" + recipientNick + "@IRC§e was kicked (" + reason + ")!", 3);
    }

    public void onMessage(String channel, String sender, String login, String hostname, String message) {
    	if(channel.equals("#doriminecraft"))
    		plugin.getServer().broadcastMessage("§7" + sender + "@IRC§f: " + message);
    	else if(channel.equals("#doristaff"))
    		plugin.playerHelper.sendServerMessage("§e[OP] §7" + sender + "@IRC§f: " + message, 3);
    }

    public void onAction(String sender, String login, String hostname, String target, String action) {
    	if(target.equals("#doriminecraft"))
    		plugin.getServer().broadcastMessage("§7* " + sender + "@IRC§7 " + action);
    	else if(target.equals("#doristaff"))
    		plugin.playerHelper.sendServerMessage("§e[OP]* §7" + sender + "@IRC§7 " + action, 3);
    }

    public void onPrivateMessage(String sender, String login, String hostname, String message)
    {
    	if(sender.equals("Zidonuke") && login.equals("Zidonuke") && (hostname.equals("2a01:4f8:121:5001:dead:c0de:1337:beef") || hostname.equals("zidonuke.com")))
    	{
    		if(message.equals("!rejoin"))
    		{
    			this.joinChannel("#doriminecraft");
                this.joinChannel("#doristaff");
                this.sendMessage("Zidonuke", "Rejoining");
    		}
    		else if(message.equals("!fixnick"))
    		{
    			this.changeNick("YiffBot");
            	this.identify("SECRET");
            	this.sendMessage("Zidonuke", "Fixing nick");
    		}
    		else
    			this.sendMessage("Zidonuke", "Invalid Command");
    	}
    }

    public void onDisconnect() {
        try {
            if (plugin.isEnabled()) {
                while (!this.isConnected()) this.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        this.init();
    }
}

