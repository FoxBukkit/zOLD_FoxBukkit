package de.doridian.yiffbukkit.transmute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import net.minecraft.server.DataWatcher;
import net.minecraft.server.Packet40EntityMetadata;

final class MobActions {
	private static HashMap<Integer, Map<String, MobAction>> mobActions = new HashMap<Integer, Map<String, MobAction>>();

	public static final Map<String, MobAction> get(int mobType) {
		return mobActions.get(mobType);
	}

	static class MetadataMobAction implements MobAction {
		int index;
		Object value;
		String message;

		public MetadataMobAction(int index, Object value, String message) {
			this.index = index;
			this.value = value;
			this.message = message;
		}

		@Override
		public void run(MobShape shape, String[] args, String argStr) {
			sendMetadataPacket(shape, index, value);

			shape.transmute.plugin.playerHelper.sendDirectedMessage(shape.player, message);
		}
	}

	static {
		registerMobActions(50, // Creeper
				"sss", "ssss", "sssss", "ssssss", "hiss", "fuse", "ignite",
				new MetadataMobAction(16, (byte) 1, "Hissing..."),
				"charge",
				new MetadataMobAction(17, (byte) 1, "Charged..."),
				"uncharge",
				new MetadataMobAction(17, (byte) 0, "Uncharged...")
		);

		/*registerMobActions(95, // Wolf
				"sit",
				new MetadataMobAction(16, 1, "Sitting down"),
				"unsit",
				new MetadataMobAction(16, 0, "Getting up"),
				"shake",
				new MobAction() { @Override public void run(MobShape shape, String[] args, String argStr) {
					//sendMetadataPacket(shape, 16, (byte) 0);

					final Location location = shape.player.getLocation();
					final World world = location.getWorld();
					for (Player player : world.getPlayers()) {
						player.sendBlockChange(location, 9, (byte) 0);
						//shape.transmute.plugin.playerHelper.sendPacketToPlayer(player, new Packet53BlockChange(location.getBlockX(), location.getBlockY(), location.getBlockZ(), ((CraftWorld) world).getHandle()));
					}

					shape.transmute.plugin.playerHelper.sendDirectedMessage(shape.player, "Shaking...");
				}}
		);*/
	}

	private static final Player sendMetadataPacket(MobShape shape, int index, Object value) {
		Packet40EntityMetadata p40 = createMetadataPacket(shape, index, value);

		final Player player = shape.player;
		shape.transmute.plugin.playerHelper.sendPacketToPlayersAround(player.getLocation(), 1024, p40, player);
		return player;
	}

	private static final Packet40EntityMetadata createMetadataPacket(MobShape shape, int index, Object value) {

		try {
			DataWatcher datawatcher = new DataWatcher();

			// create entry
			datawatcher.a(index, value.getClass().getConstructor(String.class).newInstance("0"));

			// mark dirty
			datawatcher.b(index, value.getClass().getConstructor(String.class).newInstance("1"));

			// put the actual data in
			datawatcher.b(index, value);

			return new Packet40EntityMetadata(shape.entityID, datawatcher);
		} catch (Exception e) {

			throw new RuntimeException("Could not create DataWatcher", e);
		}

	}

	private static void registerMobActions(int mobType, Object... objects) {
		Map<String, MobAction> actions = new HashMap<String, MobAction>();

		List<String> names = new ArrayList<String>();
		for (Object object : objects) {
			if (object instanceof String) {
				names.add((String)object);
			}
			else if (object instanceof MobAction) {
				for (String name : names) {
					actions.put(name, (MobAction)object);
				}
				names.clear();
			}
		}

		mobActions.put(mobType, actions);
	}
}
