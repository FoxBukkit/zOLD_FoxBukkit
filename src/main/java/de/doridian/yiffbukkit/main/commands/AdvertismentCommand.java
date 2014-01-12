package de.doridian.yiffbukkit.main.commands;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.doridian.yiffbukkit.core.util.PlayerHelper;
import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.main.commands.system.ICommand;
import de.doridian.yiffbukkit.main.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
	private class AdSerializable implements Serializable {
		private static final long serialVersionUID = 1L;

		private int delayInSeconds = 60;
		private final ArrayList<String> advertismentMsgs = new ArrayList<>();
	}

	private final Random random = new Random();

	private final AdSerializable serializable;

	private void save() {
		try {
			FileOutputStream stream = new FileOutputStream(YiffBukkit.instance.getDataFolder() + "/advertisments.dat");
			ObjectOutputStream writer = new ObjectOutputStream(stream);
			try {
				writer.writeObject(serializable);
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
		AdSerializable tmp;

		try (
				FileInputStream stream = new FileInputStream(YiffBukkit.instance.getDataFolder() + "/advertisments.dat");
				ObjectInputStream reader = new ObjectInputStream(stream)
		) {
			tmp = (AdSerializable) reader.readObject();
		} catch (IOException e) {
			tmp = null;
		} catch (Exception e) {
			e.printStackTrace();
			tmp = null;
		}

		if(tmp == null)
			serializable = new AdSerializable();
		else
			serializable = tmp;

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
			serializable.advertismentMsgs.add(res);
			save();
			PlayerHelper.sendDirectedMessage(commandSender, "Added message: " + res);
		} else if(method.equals("remove")) {
			String msgRemoved = serializable.advertismentMsgs.remove(Integer.parseInt(args[1]));
			if(msgRemoved == null) {
				throw new YiffBukkitCommandException("Invalid message");
			} else {
				save();
				PlayerHelper.sendDirectedMessage(commandSender, "Remove message: " + msgRemoved);
			}
		} else if(method.equals("list")) {
			PlayerHelper.sendDirectedMessage(commandSender, "Current ad messages:");
			for(int i = 0; i < serializable.advertismentMsgs.size(); i++) {
				PlayerHelper.sendDirectedMessage(commandSender, i + ") " + serializable.advertismentMsgs.get(i));
			}
		} else if(method.equals("delay")) {
			serializable.delayInSeconds = Integer.parseInt(args[1]);
			save();
			PlayerHelper.sendDirectedMessage(commandSender, "Set ad delay to " + serializable.delayInSeconds);
		}
	}

	private class AdvertismentThread extends Thread {
		@Override
		public void run() {
			while(true) {
				if(serializable.delayInSeconds <= 0) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) { }
					continue;
				}

				Bukkit.getScheduler().scheduleSyncDelayedTask(YiffBukkit.instance, new Runnable() {
					@Override
					public void run() {
						int max = serializable.advertismentMsgs.size();
						if(max <= 0)
							return;
						String msg = serializable.advertismentMsgs.get(random.nextInt(max));
						if(msg == null || msg.isEmpty())
							return;
						PlayerHelper.sendServerMessage(msg);
					}
				});

				try {
					Thread.sleep(serializable.delayInSeconds * 1000);
				} catch (Exception e) { }
			}
		}
	}
}
