package de.doridian.yiffbukkit.transmute;

import net.minecraft.server.DataWatcher;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet40EntityMetadata;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;

public abstract class Shape {
	final protected Transmute transmute;
	final protected int entityID;
	final protected Player player;
	protected DataWatcher datawatcher;

	protected Shape(Transmute transmute, Player player) {
		this.transmute = transmute;
		this.player = player;
		entityID = player.getEntityId();
		datawatcher = new DataWatcher();
	}

	public void deleteEntity() {
		transmute.plugin.playerHelper.sendPacketToPlayersAround(player.getLocation(), 1024, new Packet29DestroyEntity(player.getEntityId()), player);
	}

	public void createOriginalEntity() {
		transmute.plugin.playerHelper.sendPacketToPlayersAround(player.getLocation(), 1024, createPlayerSpawnPacket(), player);
	}

	private Packet20NamedEntitySpawn createPlayerSpawnPacket() {
		return new Packet20NamedEntitySpawn(((CraftPlayer)player).getHandle());
	}

	public byte getDataByte(int index) {
		try {
			return datawatcher.a(index);
		}
		catch (NullPointerException e) {
			return 0;
		}
	}

	public int getDataInteger(int index) {
		try {
			return datawatcher.b(index);
		}
		catch (NullPointerException e) {
			return 0;
		}
	}

	public String getDataString(int index) {
		try {
			return datawatcher.c(index);
		}
		catch (NullPointerException e) {
			return null;
		}

	}

	public void setData(int index, Object value) {
		Packet40EntityMetadata p40 = createMetadataPacket(index, value);

		transmute.plugin.playerHelper.sendPacketToPlayersAround(player.getLocation(), 1024, p40, player);
	}

	private Packet40EntityMetadata createMetadataPacket(int index, Object value) {
		try {
			// create entry
			datawatcher.a(index, value.getClass().getConstructor(String.class).newInstance("0"));

			// mark dirty
			datawatcher.watch(index, value.getClass().getConstructor(String.class).newInstance("1"));
		}
		catch (Exception e) { }

		// put the actual data in
		datawatcher.watch(index, value);

		return new Packet40EntityMetadata(entityID, datawatcher);
	}

	abstract public void createTransmutedEntity();
	abstract public void createTransmutedEntity(Player forPlayer);

	abstract public void runAction(String action) throws YiffBukkitCommandException;
}
