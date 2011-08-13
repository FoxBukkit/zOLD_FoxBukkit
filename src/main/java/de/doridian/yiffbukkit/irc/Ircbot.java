package de.doridian.yiffbukkit.irc;

import java.io.IOException;

import org.jibble.pircbot.*;
import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.util.Configuration;

public class Ircbot extends PircBot implements Runnable {

	private YiffBukkit plugin;

	public static String STAFFCHANNEL = Configuration.getValue("irc-staff-channel", "#doristaff");
	public static String PUBLICCHANNEL = Configuration.getValue("irc-public-channel", "#doriminecraft");

	public Ircbot(YiffBukkit plug) {
		this.plugin = plug;
	}

	public synchronized Ircbot init() {
		this.setMessageDelay(1000);
		this.setName(Configuration.getValue("irc-nick", "YiffBot"));
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
			this.connect(Configuration.getValue("irc-server","irc.esper.net"), Integer.valueOf(Configuration.getValue("irc-port", "6667")));
			this.changeNick(Configuration.getValue("irc-nick", "YiffBot"));
			this.identify(Configuration.getValue("irc-nickserv-pw", "none"));
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.joinChannel(PUBLICCHANNEL);
			this.joinChannel(STAFFCHANNEL);
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
		this.sendMessage(PUBLICCHANNEL, msg);
	}

	public void sendToChannel(String msg)
	{
		this.sendMessage(PUBLICCHANNEL, msg);
		this.sendMessage(STAFFCHANNEL, msg);
	}

	public void sendToStaffChannel(String msg)
	{
		this.sendMessage(STAFFCHANNEL, msg);
	}

	public void onJoin(String channel, String sender, String login, String hostname) {
		if(channel.equals(PUBLICCHANNEL))
			plugin.getServer().broadcastMessage("§a[+] §e" + sender + "@IRC§e joined!");
		else if(channel.equals(STAFFCHANNEL))
			plugin.playerHelper.broadcastMessage("§e[#OP]§a[+] §e" + sender + "@IRC§e joined!", "yiffbukkit.opchat"); 
	}

	public void onPart(String channel, String sender, String login, String hostname) {
		if(channel.equals(PUBLICCHANNEL))
			plugin.getServer().broadcastMessage("§c[-] §e" + sender + "@IRC§e left!");
		else if(channel.equals(STAFFCHANNEL))
			plugin.playerHelper.broadcastMessage("§e[#OP]§c[-] §e" + sender + "@IRC§e left!", "yiffbukkit.opchat");
	}

	public void onQuit(String sender, String login, String hostname, String reason) {
		plugin.getServer().broadcastMessage("§c[-] §e" + sender + "@IRC§e disconnected (" + reason + ")!");;
	}

	public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname,
			String recipientNick, String reason) {
		if (recipientNick.equalsIgnoreCase(this.getNick())) {
			this.joinChannel(channel);
		}
		if(channel.equals(PUBLICCHANNEL))
			plugin.getServer().broadcastMessage("§c[-] §e" + recipientNick + "@IRC§e was kicked (" + reason + ")!");
		else if(channel.equals(STAFFCHANNEL))
			plugin.playerHelper.broadcastMessage("§e[#OP]§c[-] §e" + recipientNick + "@IRC§e was kicked (" + reason + ")!", "yiffbukkit.opchat");
	}

	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		if(channel.equals(PUBLICCHANNEL))
			if(this.getUser(sender,channel).isOp())
				plugin.getServer().broadcastMessage("@§5" + sender + "@IRC§f: " + message);
			else if(this.getUser(sender,channel).hasVoice())
				plugin.getServer().broadcastMessage("+§7" + sender + "@IRC§f: " + message);
			else
				plugin.getServer().broadcastMessage("§7" + sender + "@IRC§f: " + message);
		else if(channel.equals(STAFFCHANNEL))
			if(this.getUser(sender,channel).isOp())
				plugin.playerHelper.broadcastMessage("§e[#OP] §f@§5" + sender + "@IRC§f: " + message, "yiffbukkit.opchat");
			else
				plugin.playerHelper.broadcastMessage("§e[#OP] §f" + sender + "@IRC§f: " + message, "yiffbukkit.opchat");
	}

	public void onAction(String sender, String login, String hostname, String target, String action) {
		if(target.equals(PUBLICCHANNEL))
			if(this.getUser(sender,target).isOp())
				plugin.getServer().broadcastMessage("§7* §f@§5" + sender + "@IRC§7 " + action);
			else if(this.getUser(sender,target).hasVoice())
				plugin.getServer().broadcastMessage("§7* §f+§7" + sender + "@IRC§7 " + action);
			else
				plugin.getServer().broadcastMessage("§7* §7" + sender + "@IRC§7 " + action);
		else if(target.equals(STAFFCHANNEL))
			if(this.getUser(sender,target).isOp())
				plugin.playerHelper.broadcastMessage("§e[#OP]§7* §f@§5" + sender + "@IRC§7 " + action, "yiffbukkit.opchat");
			else
				plugin.playerHelper.broadcastMessage("§e[#OP]§7* §f" + sender + "@IRC§7 " + action, "yiffbukkit.opchat");
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

