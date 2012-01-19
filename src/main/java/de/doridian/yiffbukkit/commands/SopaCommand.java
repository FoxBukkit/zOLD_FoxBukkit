package de.doridian.yiffbukkit.commands;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.Random;

@ICommand.Names("sopa")
@ICommand.Help(";3")
@ICommand.Usage("[probability]")
@ICommand.Permission("yiffbukkit.setsopa")
public class SopaCommand extends ICommand {
	@Override
	public void run(CommandSender sender, String[] args, String argStr) throws YiffBukkitCommandException {
		final float probability;
		if(args.length < 1) {
			if(listener.probability == 0.0F) {
				probability = 0.5F;
			} else {
				probability = 0.0F;
			}
		} else {
			probability = Integer.parseInt(args[0]) / 100.0F;
		}

		if(probability > 1) {
			listener.probability = 1;
		} else if(probability < 0) {
			listener.probability = 0;
		} else {
			listener.probability = probability;
		}
		
		plugin.playerHelper.sendDirectedMessage(sender, "SOPA probability = " + (int)(listener.probability * 100) + "%");
	}

	final private SopaChatListener listener;
	public SopaCommand() {
		listener = new SopaChatListener(YiffBukkit.instance);
	}
	
	class SopaChatListener implements Listener {
		Random random = new Random();
		public float probability = 0.0F;

		public SopaChatListener(YiffBukkit plugin) {
			plugin.getServer().getPluginManager().registerEvents(this, plugin);
		}

		@EventHandler(event = PlayerChatEvent.class, priority = EventPriority.LOWEST)
		public void censorChat(PlayerChatEvent event) {
			String[] words = event.getMessage().split(" +");
			if(words.length < 1) return;

			int iLast = -5;
			for(int i = 0; i < words.length; i++) {
				if(random.nextFloat() <= probability) {
					if(i != iLast) {
						words[i] = "\u00a7k" + words[i];
					}
					iLast = i + 1;
				} else if(iLast == i) {
					words[i] = "\u00a7f" + words[i];
				}
			}

			event.setMessage(Utils.concatArray(words, 0, ""));
		}
	}
}
