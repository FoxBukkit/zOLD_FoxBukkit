package de.doridian.yiffbukkit.commands;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet0KeepAlive;
import net.minecraft.server.Packet3Chat;
import net.minecraft.server.Packet4UpdateTime;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;
import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.util.Utils;

@Names("replay")
@Level(4)
@BooleanFlags("s")
public class ReplayCommand extends ICommand {
	ReplayPacketListener replayPacketListener = new ReplayPacketListener();
	public Map<Player, Replayer> replayers = new HashMap<Player, Replayer>();
	static boolean bypass = false;

	@Override
	public void Run(Player ply, String[] args, String argStr) throws YiffBukkitCommandException {
		args = parseFlags(args);

		if (booleanFlags.contains('s')) {
			Replayer replayer = replayers.get(ply);
			if (replayer == null)
				throw new YiffBukkitCommandException("No replay playing");

			replayer.stop();
			playerHelper.sendDirectedMessage(ply, "Stopped replay");
			return;
		}

		if (replayers.containsKey(ply))
			throw new YiffBukkitCommandException("Already playing a replay. Stop with /replay -s.");


		String replayName = args[0];

		try {
			replayers.put(ply, new Replayer(ply, replayName));
		} catch (IOException e) {
			throw new YiffBukkitCommandException("Error loading replay", e);
		}
	}

	class Replayer implements Runnable {
		private final Player player;
		private EntityPlayer eply;
		private final NetServerHandler netServerHandler;

		private final File file;
		private final InputStream is;
		private final DataInputStream dis;

		private final int taskId;

		private final long offset;

		public Replayer(Player player, String filename) throws IOException {
			this.player = player;
			oldLocation = player.getLocation();
			eply = ((CraftPlayer)player).getHandle();
			netServerHandler = eply.netServerHandler;

			file = new File(filename);
			is = new FileInputStream(file);
			dis = new DataInputStream(is);

			taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1);
			nextTime = System.currentTimeMillis()+1000;
			offset = nextTime - dis.readLong();

			//dos.writeLong(System.currentTimeMillis());
			//dos.write(packetID);
			//mcPacket.a(dos);
		}

		private long nextTime = Long.MIN_VALUE;
		private int[] packetCounters = new int[256];
		private Location oldLocation;

		@Override
		public void run() {
			try {
				while (true) {
					long currentTime = System.currentTimeMillis();
					if (currentTime < nextTime)
						break;

					// Determine packet ID
					final int length = dis.readInt();
					final int packetID = dis.read();

					DataOutputStream dos = Utils.getPrivateValue(NetworkManager.class, netServerHandler.networkManager, "output");

					++packetCounters [packetID];

					byte[] buffer = new byte[length+1];
					buffer[0] = (byte) packetID;
					int bytesRead = dis.read(buffer, 1, length);

					if (bytesRead != length) {
						stop();
						return;
					}
					switch (packetID) {
					case 3:
						// Instantiate empty packet object
						Packet3Chat p3 = new Packet3Chat();

						// Read data into the empty packet object
						p3.a(new DataInputStream(new ByteArrayInputStream(buffer, 1, length)));

						// prefix replayed chat messages so they can be told from out-of-replay chat.
						p3.a = "§c[RPL] §f"+p3.a;

						dos.write(packetID);
						p3.a(dos);
						break;

					case 4:
						// Instantiate empty packet object
						Packet4UpdateTime p4 = new Packet4UpdateTime();

						// Read data into the empty packet object
						p4.a(new DataInputStream(new ByteArrayInputStream(buffer, 1, length)));

						// prefix replayed chat messages so they can be told from out-of-replay chat.
						p4.a = 12000;

						dos.write(packetID);
						p4.a(dos);
						break;

					default:
						dos.write(buffer);
						break;
					}

					nextTime = dis.readLong()+offset;
					//System.out.println("Sent packet ID "+packetID+" next due in "+(nextTime-System.currentTimeMillis())+"ms.");
					//stop();
					//break;
				}
			}
			catch (EOFException e) {
				stop();
				playerHelper.sendDirectedMessage(player, "End of replay reached.");
				StringBuilder sb = new StringBuilder("Packet counts: ");
				for (int i = 0; i < 256; ++i) {
					final int amount = packetCounters[i];
					if (amount == 0)
						continue;

					sb.append(i+"="+amount+";");
				}
				final String s = sb.toString();
				playerHelper.sendDirectedMessage(player, s);
				System.out.println(s);
				return;
			}
			catch (IOException e) {
				e.printStackTrace();
				stop();
				return;
			}

			fakeIncomingPacket(eply, new Packet0KeepAlive());
		}

		public void stop() {
			plugin.getServer().getScheduler().cancelTask(taskId);
			replayers.remove(player);

			player.teleport(new Location(oldLocation.getWorld(), 1e5, 129, 1e5));
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					player.teleport(oldLocation);
				}
			}, 3);
		}
	}

	@SuppressWarnings("unchecked")
	public static void fakeIncomingPacket(EntityPlayer eply, net.minecraft.server.Packet packet) {
		final NetworkManager networkManager = eply.netServerHandler.networkManager;
		int[] aint = NetworkManager.d;
		int i = packet.b();

		aint[i] += packet.a() + 1;
		@SuppressWarnings("rawtypes")
		List m = Utils.getPrivateValue(NetworkManager.class, networkManager, "m");
		m.add(packet);
	}

	class ReplayPacketListener extends PacketListener {
		public ReplayPacketListener() {
			for (int packetID = 1; packetID < 256; ++packetID) {
				if (packetID != 3) {
					addPacketListener(true, packetID, this, plugin);
					addPacketListener(false, packetID, this, plugin);
				}
			}
		}
		@Override
		public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
			if (bypass)
				return true;

			return !replayers.containsKey(ply);
		}

		@Override
		public boolean onIncomingPacket(Player ply, int packetID, Packet packet) {
			return !replayers.containsKey(ply);
		}
	}
}