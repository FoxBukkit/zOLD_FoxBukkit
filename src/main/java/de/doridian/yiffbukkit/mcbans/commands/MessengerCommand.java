package de.doridian.yiffbukkit.mcbans.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Help;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Names;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Permission;
import de.doridian.yiffbukkit.main.commands.system.ICommand.Usage;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkit.mcbans.MCBansUtil;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;

import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

@Names({"messenger","msngr"})
@Help("Command for the MCBans messenger system")
@Usage("<inbox/read/send/block/unblock> [parameter]")
@Permission("yiffbukkit.messenger")
public class MessengerCommand extends ICommand {
	@Override
	public void Run(final Player commandSender, final String[] args, String argStr) throws YiffBukkitCommandException {
		if (args.length < 1)
			throw new YiffBukkitCommandException("Argument expected.");
		
		new Thread() {
			public void run() {
				final String cmd = args[0].toLowerCase();
				if(cmd.equals("inbox")) {
					final JSONObject connret = MCBansUtil.apiQuery("exec=getInbox&player="+MCBansUtil.URLEncode(commandSender.getName()));
					if(MCBansUtil.isKeyYesOrNo(connret, "result")) {
						PlayerHelper.sendDirectedMessage(commandSender, "You have the following new messages:");
						String[] msgs = ((String)connret.get("messages")).split(";");
						for(String msg : msgs) {
							PlayerHelper.sendDirectedMessage(commandSender, "\u00a72"+msg);
						}
					} else {
						PlayerHelper.sendDirectedMessage(commandSender, "You have no new messages");
					}
				} else if(cmd.equals("read")) {
					final JSONObject connret;
					if(args.length > 1)
						connret = MCBansUtil.apiQuery("exec=getMessage&player="+MCBansUtil.URLEncode(commandSender.getName())+"&message="+args[1]);
					else
						connret = MCBansUtil.apiQuery("exec=getNewMessage&player="+MCBansUtil.URLEncode(commandSender.getName()));
					
					if(MCBansUtil.isKeyYesOrNo(connret, "result")) {
						PlayerHelper.sendDirectedMessage(commandSender, "\u00a73From:\u00a72 " + ((String)connret.get("sender")));
						PlayerHelper.sendDirectedMessage(commandSender, "\u00a73Date:\u00a72 " + ((String)connret.get("date")));
						PlayerHelper.sendDirectedMessage(commandSender, "\u00a73Message");
						PlayerHelper.sendDirectedMessage(commandSender, ((String)connret.get("message")));
					} else {
						PlayerHelper.sendDirectedMessage(commandSender, "No message found or invalid message specified!");
					}
				} else if(cmd.equals("send")) {
					if(args.length < 3) {
						PlayerHelper.sendDirectedMessage(commandSender, "Not enough arguments!");
						return;
					}
					
					final String recipient = playerHelper.completePlayerName(args[1], true);
					if(recipient == null) {
						PlayerHelper.sendDirectedMessage(commandSender, "Sorry, I need a valid recipient!");
						return;
					}
					final String message = Utils.concatArray(args, 2, null).trim();
					if(message == null || message.length() < 1) {
						PlayerHelper.sendDirectedMessage(commandSender, "Sorry, I need a message!");
						return;
					}
						
					final JSONObject connret = MCBansUtil.apiQuery("exec=sendMessage&player="+MCBansUtil.URLEncode(commandSender.getName())+"&target="+MCBansUtil.URLEncode(recipient)+"&message="+MCBansUtil.URLEncode(message));
					if(MCBansUtil.isKeyYesOrNo(connret, "result")) {
						PlayerHelper.sendDirectedMessage(commandSender, "Message sent successfully!");
					} else {
						PlayerHelper.sendDirectedMessage(commandSender, "Message could not be sent!");
					}
				} else if(cmd.equals("block")) {
					if(args.length < 2) {
						PlayerHelper.sendDirectedMessage(commandSender, "Not enough arguments!");
						return;
					}
					
					final String target = playerHelper.completePlayerName(args[1], true);
					if(target == null) {
						PlayerHelper.sendDirectedMessage(commandSender, "Sorry, I need a valid target!");
						return;
					}
					
					final JSONObject connret = MCBansUtil.apiQuery("exec=playerBlock&player="+MCBansUtil.URLEncode(commandSender.getName())+"&target="+MCBansUtil.URLEncode(target));
					if(MCBansUtil.isKeyYesOrNo(connret, "result")) {
						PlayerHelper.sendDirectedMessage(commandSender, "Player blocked!");
					} else {
						PlayerHelper.sendDirectedMessage(commandSender, "Player was already blocked!");
					}
				} else if(cmd.equals("unblock")) {
					if(args.length < 2) {
						PlayerHelper.sendDirectedMessage(commandSender, "Not enough arguments!");
						return;
					}
					
					final String target = playerHelper.completePlayerName(args[1], true);
					if(target == null) {
						PlayerHelper.sendDirectedMessage(commandSender, "Sorry, I need a valid target!");
						return;
					}
					
					final JSONObject connret = MCBansUtil.apiQuery("exec=playerUnBlock&player="+MCBansUtil.URLEncode(commandSender.getName())+"&target="+MCBansUtil.URLEncode(target));
					if(MCBansUtil.isKeyYesOrNo(connret, "result")) {
						PlayerHelper.sendDirectedMessage(commandSender, "Player unblocked!");
					} else {
						PlayerHelper.sendDirectedMessage(commandSender, "Player was not blocked!");
					}
				} else {
					PlayerHelper.sendDirectedMessage(commandSender, "Invalid subcommand!");
				}
			}
		}.start();
	}
}