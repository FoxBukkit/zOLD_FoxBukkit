/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.main.commands;

import com.foxelbox.foxbukkit.core.FoxBukkit;
import com.foxelbox.foxbukkit.core.util.PlayerHelper;
import com.foxelbox.foxbukkit.main.FoxBukkitCommandException;
import com.foxelbox.foxbukkit.main.commands.system.ICommand;
import com.foxelbox.foxbukkit.main.util.Utils;
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
@ICommand.Permission("foxbukkit.advertisment")
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
			FileOutputStream stream = new FileOutputStream(FoxBukkit.instance.getDataFolder() + "/advertisments.dat");
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
				FileInputStream stream = new FileInputStream(FoxBukkit.instance.getDataFolder() + "/advertisments.dat");
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
	public void run(CommandSender commandSender, String[] args, String argStr, String commandName) throws FoxBukkitCommandException {
		final String method = args[0].toLowerCase();
		if(method.equals("add")) {
			String res = Utils.concatArray(args, 1, "").trim();
			if(res.isEmpty()) {
				throw new FoxBukkitCommandException("Empty message");
			}
			serializable.advertismentMsgs.add(res);
			save();
			PlayerHelper.sendDirectedMessage(commandSender, "Added message: " + res);
		} else if(method.equals("remove")) {
			String msgRemoved = serializable.advertismentMsgs.remove(Integer.parseInt(args[1]));
			if(msgRemoved == null) {
				throw new FoxBukkitCommandException("Invalid message");
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
		private AdvertismentThread() {
			setDaemon(true);
			setName("FoxBukkit-AdvertismentThread");
		}

		@Override
		public void run() {
			while(true) {
				if(serializable.delayInSeconds <= 0) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) { }
					continue;
				}

				Bukkit.getScheduler().scheduleSyncDelayedTask(FoxBukkit.instance, new Runnable() {
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
