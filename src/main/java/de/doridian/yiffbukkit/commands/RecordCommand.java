package de.doridian.yiffbukkit.commands;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet50PreChunk;
import net.minecraft.server.Packet51MapChunk;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.server.Packet;
import org.bukkit.event.server.PacketListener;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.util.Utils;

@Names("record")
@Level(4)
public class RecordCommand extends ICommand {
	@Override
	public void run(CommandSender commandSender, String[] args, String argStr) throws YiffBukkitCommandException {
		Player target = playerHelper.matchPlayerSingle(args[0]);

		HumanEntity recorder = makeRecorder(target.getName(), target.getLocation());
		target.setPassenger(recorder);
	}

	//@SuppressWarnings("unchecked")
	private HumanEntity makeRecorder(String playerName, Location location) throws YiffBukkitCommandException {
		final String recorderName = String.format("§c%s", playerName);

		try {
			final Recorder recorder = new Recorder(playerName);
			recorders.put(playerName, recorder);
			recorders.put(recorderName, recorder);
		} catch (FileNotFoundException e) {
			throw new YiffBukkitCommandException("Could not create replay file.", e);
		}

		final HumanEntity bukkitEntity = Utils.makeNPC(recorderName, location);

		EntityHuman eply = ((CraftHumanEntity)bukkitEntity).getHandle();
		final WorldServer worldServer = (WorldServer)eply.world;
		worldServer.manager.addPlayer((EntityPlayer)eply);

		//eply.world.players.remove(eply);
		@SuppressWarnings({ "unchecked", "unused" }) boolean dummy1 = 
			eply.world.players.add(eply);

		//((WorldServer) eply.world).server.serverConfigurationManager.players.remove(eply);
		@SuppressWarnings({ "unchecked", "unused" }) boolean dummy2 = 
			worldServer.server.serverConfigurationManager.players.add(eply);

		return bukkitEntity;
	}

	static class Recorder {
		final String name;
		final File file;
		final OutputStream os;
		DataOutputStream dos;

		public Recorder(String name) throws FileNotFoundException {
			this.name = name;
			file = new File(name+".replay");
			os = new FileOutputStream(file);
			dos = new DataOutputStream(new BufferedOutputStream(os, 5120));
		}
	}

	Map<String, Recorder> recorders = new HashMap<String, RecordCommand.Recorder>();
	RecorderPacketListener recorderPacketListener = new RecorderPacketListener();

	class RecorderPacketListener extends PacketListener {
		public RecorderPacketListener() {
			for (int i = 0; i < 256; ++i) {
				addPacketListener(true, i, this, plugin);
			}
		}

		@Override
		public boolean onOutgoingPacket(Player ply, int packetID, Packet packet) {
			final String playerName = ply.getName();
			if (playerName.length() < 2)
				return true;

			if (playerName.charAt(0) != '§')
				return true;

			if (playerName.charAt(1) != 'c')
				return true;

			/*if (packetID == 3) {
				try {
					final DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(System.out, 5120));
					mcPacket.a(dataoutputstream);
					dataoutputstream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}*/

			final Recorder recorder = recorders.get(playerName);
			final DataOutputStream dos = recorder.dos;
			switch (packetID) {
			case 255:
				recorder.dos = null;
				try {
					dos.flush();
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;

			case 50:
				if (dos != null) recordPacket(packetID, dos, (net.minecraft.server.Packet) packet);
				Packet50PreChunk p50 = (Packet50PreChunk) packet;
				packetID = 51;
				packet = new Packet51MapChunk(p50.a << 4, 0, p50.b << 4, 16, 128, 16, ((CraftWorld)ply.getWorld()).getHandle());
				break;

			/*case 34:
				Packet34EntityTeleport p34 = (Packet34EntityTeleport) packet;
				if (ply.getEntityId() == p34.a) {
					final Packet11PlayerPosition p11 = new Packet11PlayerPosition();
					p11.x = p34.b/32D;
					p11.stance = p34.c/32D;
					p11.y = p34.c/32D;
					p11.z = p34.d/32D;
					ReplayCommand.fakeIncomingPacket(((CraftPlayer)ply).getHandle(), p11);
				}

				break;*/
			}

			if (dos == null)
				return false;

			recordPacket(packetID, dos, (net.minecraft.server.Packet) packet);

			return false;
		}

		private void recordPacket(int packetID, DataOutputStream dos, net.minecraft.server.Packet packet) {
			try {
				// write packet to a temporary ByteArrayOutputStream, to determine the size
				final ByteArrayOutputStream bos = new ByteArrayOutputStream();
				final DataOutputStream bosdos = new DataOutputStream(new BufferedOutputStream(bos, 5120));
				packet.a(bosdos);
				bosdos.close();

				// store packet time
				dos.writeLong(System.currentTimeMillis());

				// store packet size
				dos.writeInt(bos.size());

				// store packet ID
				dos.writeByte(packetID);

				// store packet data
				bos.writeTo(dos);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
