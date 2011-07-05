package de.doridian.yiffbukkit.transmute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;

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

		registerMobActions(55, // Slime
				"size",
				new MobAction() { @Override public void run(MobShape shape, String[] args, String argStr) throws YiffBukkitCommandException {
					byte size = Byte.valueOf(argStr);
					sendMetadataPacket(shape, 16, size);

					shape.transmute.plugin.playerHelper.sendDirectedMessage(shape.player, "Set your size to "+size);
				}}
		);

		registerMobActions(90, // Pig
				"saddle",
				new MetadataMobAction(16, (byte) 1, "You now have a saddle."),
				"unsaddle",
				new MetadataMobAction(16, (byte) 0, "You no longer have a saddle.")
		);

		registerMobActions(91, // Sheep
				"color",
				new MobAction() { @Override public void run(MobShape shape, String[] args, String argStr) throws YiffBukkitCommandException {
					DyeColor dyeColor = DyeColor.WHITE;
					try {
						if ("RAINBOW".equalsIgnoreCase(argStr) || "RAINBOWS".equalsIgnoreCase(argStr) || "RANDOM".equalsIgnoreCase(argStr)) {
							DyeColor[] dyes = DyeColor.values();
							dyeColor = dyes[(int)Math.floor(dyes.length*Math.random())];
						}
						else {
							dyeColor = DyeColor.valueOf(argStr.toUpperCase());
						}
					}
					catch (Exception e) { }

					sendMetadataPacket(shape, 16, dyeColor.getData());

					shape.transmute.plugin.playerHelper.sendDirectedMessage(shape.player, "You are now "+dyeColor.toString().toLowerCase().replace('_',' ')+".");
				}},
				"shorn",
				new MetadataMobAction(16, (byte) 16, "You are now shorn.")
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
