package de.doridian.yiffbukkit;

import de.doridian.yiffbukkit.config.ConfigFileReader;
import de.doridian.yiffbukkit.config.ConfigFileWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SignSaver extends StateContainer implements Listener {
	private final YiffBukkit plugin;
	List<SignDescriptor> saved_signs = new ArrayList<SignDescriptor>();

	public SignSaver(YiffBukkit plugin) {
		this.plugin = plugin;

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		fixSigns();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		fixSigns();
	}

	@Loader({"signsaver", "sign_saver"})
	public void loadSignSaver() {
		saved_signs.clear();
		try {
			BufferedReader stream = new BufferedReader(new ConfigFileReader("signsaver.txt"));
			String line;
			SignDescriptor current = new SignDescriptor();
			while((line = stream.readLine()) != null) {
				if (line.isEmpty())
					continue;

				char field = line.charAt(0);
				String value = line.substring(1);

				switch (field) {
				case 'x':
					current.location.setX(Integer.parseInt(value));
					break;

				case 'y':
					current.location.setY(Integer.parseInt(value));
					break;

				case 'z':
					current.location.setZ(Integer.parseInt(value));
					break;

				case 'w':
					current.location.setWorld(plugin.getServer().getWorld(value));
					break;

				case '1':
				case '2':
				case '3':
				case '4':
					current.lines[Character.digit(field, 10)-1] = value;
					break;

				case 'a':
					saved_signs.add(current);
					current = new SignDescriptor();
					break;
				}
			}
			stream.close();
		}
		catch (IOException e) {
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Saver({"signsaver", "sign_saver"})
	public void saveSignSaver() {
		try {
			BufferedWriter stream = new BufferedWriter(new ConfigFileWriter("signsaver.txt"));
			for (SignDescriptor current : saved_signs) {
				stream.write("x"+current.location.getBlockX());
				stream.newLine();

				stream.write("y"+current.location.getBlockY());
				stream.newLine();

				stream.write("z"+current.location.getBlockZ());
				stream.newLine();

				stream.write("w"+current.location.getWorld().getName());
				stream.newLine();

				final String[] lines = current.lines;
				for (int i = 0; i < 4; ++i) {
					stream.write((i+1)+lines[i]);
					stream.newLine();
				}
				stream.write('a');
				stream.newLine();

				stream.newLine();
			}
			stream.close();
		}
		catch(IOException e) { }
	}

	public void fixSigns() {
		for (SignDescriptor current : saved_signs) {
			BlockState state = current.location.getBlock().getState();
			if (!(state instanceof Sign)) {
				plugin.log(Level.WARNING, "SignSaver: Block at " + current.location + " is not a sign.");
				continue;
			}

			Sign sign = (Sign) state;
			final String[] lines = current.lines;
			for (int i = 0; i < 4; ++i) {
				sign.setLine(i, lines[i]);
			}
			sign.update(true);
		}
	}

	private static class SignDescriptor {
		public Location location = new Location(Bukkit.getServer().getWorld("world"), 0,0,0);
		public String[] lines = { "", "", "", "" };
	}

	public void addSign(Location location) throws YiffBukkitCommandException {
		BlockState state = location.getBlock().getState();
		if (!(state instanceof Sign)) {
			throw new YiffBukkitCommandException("SignSaver: Block at "+location+" is not a sign."); 
		}

		Sign sign = (Sign) state;

		SignDescriptor signDescriptor = new SignDescriptor();
		signDescriptor.location = location;
		signDescriptor.lines = sign.getLines();

		saved_signs.add(signDescriptor);

		saveSignSaver();
	}
}
