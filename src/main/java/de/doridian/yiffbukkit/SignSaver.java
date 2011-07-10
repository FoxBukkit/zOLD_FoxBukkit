package de.doridian.yiffbukkit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginManager;

public class SignSaver extends StateContainer {
	private final YiffBukkit plugin;
	List<SignDescriptor> saved_signs = new ArrayList<SignDescriptor>();

	public SignSaver(YiffBukkit plugin) {
		this.plugin = plugin;

		PlayerListener listener = new PlayerListener() {
			@Override
			public void onPlayerJoin(PlayerJoinEvent event) {
				fixSigns();
			}

			@Override
			public void onPlayerRespawn(PlayerRespawnEvent event) {
				fixSigns();
			}
		};

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_JOIN, listener, Priority.Monitor, plugin);
		pm.registerEvent(Type.PLAYER_RESPAWN, listener, Priority.Monitor, plugin);
	}

	@Loader({"signsaver", "sign_saver"})
	public void loadSignSaver() {
		saved_signs.clear();
		try {
			BufferedReader stream = new BufferedReader(new FileReader("signsaver.txt"));
			String line;
			SignDescriptor current = new SignDescriptor();
			while((line = stream.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}

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
		catch (Exception e) { }
	}

	@Saver({"signsaver", "sign_saver"})
	public void saveSignSaver() {
		try {
			BufferedWriter stream = new BufferedWriter(new FileWriter("signsaver.txt"));
			for (SignDescriptor current : saved_signs) {
				stream.write("x"+current.location.getX());
				stream.newLine();

				stream.write("y"+current.location.getX());
				stream.newLine();

				stream.write("z"+current.location.getX());
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
				System.out.println("SignSaver: Block at "+current.location+" is not a sign."); 
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
