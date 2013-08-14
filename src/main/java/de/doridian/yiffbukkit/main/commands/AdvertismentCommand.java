package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.util.Utils;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

@ICommand.Names({"ad", "ads", "advertisment", "advertisments"})
@ICommand.Help("Manages advertisment system")
@ICommand.Usage("<add/remove/list/delay> <text/id/-/seconds>")
@ICommand.Permission("yiffbukkit.advertisment")
public class AdvertismentCommand extends ICommand {
	private class AdSerilizable implements Serializable {
		private static final long serialVersionUID = 1L;

		private int delayInSeconds = 60;
		private final ArrayList<String> advertismentMsgs = new ArrayList<String>();
	}

	private final Random random = new Random();

	private final AdSerilizable serilizable;

	private void save() {
		try {
			FileOutputStream stream = new FileOutputStream(YiffBukkit.instance.getDataFolder() + "/advertisments.dat");
			ObjectOutputStream writer = new ObjectOutputStream(stream);
			try {
				writer.writeObject(serilizable);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			writer.close();
			stream.close();
		}
		catch (Exception e) { }
	}

	public AdvertismentCommand() {
		AdSerilizable tmp = null;

		try {
			FileInputStream stream = new FileInputStream(YiffBukkit.instance.getDataFolder() + "/advertisments.dat");
			try {
				ObjectInputStream reader = new ObjectInputStream(stream);
				try {
					tmp = (AdSerilizable)reader.readObject();
				}
				finally {
					reader.close();
				}
			}
			finally {
				stream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			tmp = new AdSerilizable();
		}

		if(tmp == null)
			serilizable = new AdSerilizable();
		else
			serilizable = tmp;

		new AdvertismentThread().start();
	}

	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		final String method = args[0].toLowerCase();
		if(method.equals("add")) {
			String res = Utils.concatArray(args, 1, "").trim();
			if(res.isEmpty()) {
				throw new YiffBukkitCommandException("Empty message");
			}
			serilizable.advertismentMsgs.add(res);
			save();
			PlayerHelper.sendDirectedMessage(commandSender, "Added message: " + res);
		} else if(method.equals("remove")) {
			String msgRemoved = serilizable.advertismentMsgs.remove(Integer.parseInt(args[1]));
			if(msgRemoved == null) {
				throw new YiffBukkitCommandException("Invalid message");
			} else {
				save();
				PlayerHelper.sendDirectedMessage(commandSender, "Remove message: " + msgRemoved);
			}
		} else if(method.equals("list")) {
			PlayerHelper.sendDirectedMessage(commandSender, "Current ad messages:");
			for(int i = 0; i < serilizable.advertismentMsgs.size(); i++) {
				PlayerHelper.sendDirectedMessage(commandSender, i + ") " + serilizable.advertismentMsgs.get(i));
			}
		} else if(method.equals("delay")) {
			serilizable.delayInSeconds = Integer.parseInt(args[1]);
			save();
			PlayerHelper.sendDirectedMessage(commandSender, "Set ad delay to " + serilizable.delayInSeconds);
		}
	}

	private class AdvertismentThread extends Thread {
		@Override
		public void run() {
			while(true) {
				if(serilizable.delayInSeconds <= 0) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) { }
					continue;
				}

				Bukkit.getScheduler().scheduleSyncDelayedTask(YiffBukkit.instance, new Runnable() {
					@Override
					public void run() {
						int max = serilizable.advertismentMsgs.size();
						if(max <= 0)
							return;
						String msg = serilizable.advertismentMsgs.get(random.nextInt(max));
						if(msg == null || msg.isEmpty())
							return;
						playerHelper.sendServerMessage(msg);
					}
				});

				try {
					Thread.sleep(serilizable.delayInSeconds * 1000);
				} catch (Exception e) { }
			}
		}
	}
}
