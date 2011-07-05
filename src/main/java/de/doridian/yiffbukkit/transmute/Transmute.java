package de.doridian.yiffbukkit.transmute;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkit;

public class Transmute {
	final YiffBukkit plugin;
	@SuppressWarnings("unused")
	private final TransmutePacketListener transmutePacketListener;
	@SuppressWarnings("unused")
	private TransmutePlayerListener transmutePlayerListener;
	private final Map<Integer, Shape> transmuted = new HashMap<Integer, Shape>();

	public Transmute(YiffBukkit plugin) {
		this.plugin = plugin;
		transmutePacketListener = new TransmutePacketListener(this);
		transmutePlayerListener = new TransmutePlayerListener(this);
	}

	public boolean isTransmuted(int entityID) {
		return transmuted.containsKey(entityID);
	}

	public boolean isTransmuted(Entity entity) {
		return transmuted.containsKey(entity.getEntityId());
	}

	public boolean isTransmuted(net.minecraft.server.Entity entity) {
		return transmuted.containsKey(entity.id);
	}

	public Shape getShape(int entityID) {
		return transmuted.get(entityID);
	}

	public Shape getShape(Entity entity) {
		return transmuted.get(entity.getEntityId());
	}

	public Shape getShape(net.minecraft.server.Entity entity) {
		return transmuted.get(entity.id);
	}

	public void setShape(Player player, Shape shape) {
		if (shape.player != player)
			throw new IllegalArgumentException("Assigned a shape to the wrong player!");

		transmuted.put(player.getEntityId(), shape);
		shape.deleteEntity();
		shape.createTransmutedEntity();
	}

	public void setShape(Player player, int mobType) {
		setShape(player, new MobShape(this, player, mobType));
	}

	public void setShape(Player player, String mobType) {
		setShape(player, new MobShape(this, player, mobType));
	}

	public void resetShape(Player player) {
		Shape shape = transmuted.remove(player.getEntityId());
		if (shape == null)
			return;

		shape.deleteEntity();
		shape.createOriginalEntity();
	}

	public void removeShape(Player player) {
		Shape shape = transmuted.remove(player.getEntityId());
		if (shape == null)
			return;

		shape.deleteEntity();
	}
}
