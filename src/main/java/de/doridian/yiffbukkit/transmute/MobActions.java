package de.doridian.yiffbukkit.transmute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.Packet38EntityStatus;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.util.PlayerHelper;

final class MobActions {
	private static HashMap<Integer, Map<String, MobAction>> mobActions = new HashMap<Integer, Map<String, MobAction>>();

	public static final Map<String, MobAction> get(int mobType) {
		return mobActions.get(mobType);
	}

	static {
		registerMobActions(50, // Creeper
				"help",
				new HelpMobAction("/sac hiss|charge [on|off]"),
				"sss", "ssss", "sssss", "ssssss", "hiss", "fuse", "ignite",
				new MobAction() { @Override public void run(MobShape shape, String[] args, String argStr) throws YiffBukkitCommandException {
					shape.setData(16, (byte) 0);
					shape.setData(16, (byte) 1);

					shape.transmute.plugin.playerHelper.sendDirectedMessage(shape.player, "Hissing...");
				}},
				"charge",
				new MetadataBitMobAction(17, (byte) 0x1, "Uncharged...", "Charged...")
		);
		//registerMobActions(51, // Skeleton
		//registerMobActions(52, // Spider
		//registerMobActions(53, // Giant
		//registerMobActions(54, // Zombie

		final Object[] slimeActions = new Object[] {
				"help",
				new HelpMobAction("/sac size <1..127>"),
				"size",
				new MobAction() { @Override public void run(MobShape shape, String[] args, String argStr) throws YiffBukkitCommandException {
					byte size = Byte.valueOf(argStr);
					shape.setData(16, size);

					shape.transmute.plugin.playerHelper.sendDirectedMessage(shape.player, "Set your size to "+size);
				}}
		};
		registerMobActions(55, // Slime
				slimeActions
		);

		registerMobActions(56, // Ghast
				"help",
				new HelpMobAction("/sac fire [on|off]"),
				"fire",
				new MetadataBitMobAction(16, (byte) 0x1, "Ceasing fire...", "Firing...")
		);

		//registerMobActions(57, // PigZombie
		//registerMobActions(58, // Enderman
		//registerMobActions(59, // CaveSpider
		//registerMobActions(60, // Silverfish
		//registerMobActions(61, // Blaze

		registerMobActions(62, // LavaSlime
				slimeActions
		);

		//registerMobActions(63, // EnderDragon

		registerMobActions(90, // Pig
				"help",
				new HelpMobAction("/sac saddle [on|off]|baby|adult"),
				"saddle",
				new MetadataBitMobAction(16, (byte) 0x1, "You no longer have a saddle.", "You now have a saddle."),
				"baby",
				new MetadataMobAction(12, -24000, "Now a baby..."),
				"adult",
				new MetadataMobAction(12, 0, "Now an adult...")
		);

		registerMobActions(91, // Sheep
				"help",
				new HelpMobAction("/sac shorn|color <color>|baby|adult"),
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

					shape.setData(16, dyeColor.getData());

					shape.transmute.plugin.playerHelper.sendDirectedMessage(shape.player, "You are now "+dyeColor.toString().toLowerCase().replace('_',' ')+".");
				}},
				"shorn",
				new MetadataMobAction(16, (byte) 16, "You are now shorn."),
				"baby",
				new MetadataMobAction(12, -24000, "Now a baby..."),
				"adult",
				new MetadataMobAction(12, 0, "Now an adult...")
		);

		final Object[] animalActions = new Object[] {
				"help",
				new HelpMobAction("/sac baby|adult"),
				"baby",
				new MetadataMobAction(12, -24000, "Now a baby..."),
				"adult",
				new MetadataMobAction(12, 0, "Now an adult...")
		};
		registerMobActions(92, // Cow
				animalActions
		);

		registerMobActions(93, // Chicken
				animalActions
		);

		//registerMobActions(94, // Squid

		registerMobActions(95, // Wolf
				"help",
				new HelpMobAction("/sac sit [on|off]|angry [on|off]|tame [on|off]|shake|hearts|smoke|baby|adult"),
				"sit",
				new MetadataBitMobAction(16, 0x1, "Getting up...", "Sitting down..."),
				"angry",
				new MetadataBitMobAction(16, 0x2, "Now peaceful...", "Now angry..."),
				"tame", "tamed",
				new MetadataBitMobAction(16, 0x4, "Now untamed...", "Now tamed..."),
				"shake",
				new EntityStatusMobAction(8, "Shaking..."),
				"hearts","heart", "love",
				new EntityStatusMobAction(7, "Loving..."),
				"smoke",
				new EntityStatusMobAction(6, "Smoking..."),
				"baby",
				new MetadataMobAction(12, -24000, "Now a baby..."),
				"adult",
				new MetadataMobAction(12, 0, "Now an adult...")
		);

		registerMobActions(96, // MushroomCow
				animalActions
		);
		//registerMobActions(97, // SnowMan
		//registerMobActions(120, // Villager
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

	private static class HelpMobAction implements MobAction {
		final String message;

		public HelpMobAction(String message) {
			this.message = message;
		}

		@Override
		public void run(MobShape shape, String[] args, String argStr) {
			shape.transmute.plugin.playerHelper.sendDirectedMessage(shape.player, message);
		}

	}

	private static class MetadataBitMobAction implements MobAction {
		private final int index;
		private final byte bit;
		private final String unsetMessage;
		private final String setMessage;


		public MetadataBitMobAction(int index, int bit, String unsetMessage, String setMessage) {
			this.index = index;
			this.bit = (byte) bit;
			this.unsetMessage = unsetMessage;
			this.setMessage = setMessage;
		}

		@Override
		public void run(MobShape shape, String[] args, String argStr) throws YiffBukkitCommandException {
			final byte oldData = shape.getDataByte(index);
			if ((oldData & bit) != 0) {
				if ("on".equalsIgnoreCase(argStr))
					throw new YiffBukkitCommandException("Already on");

				shape.setData(index, (byte)(oldData & ~bit));
				shape.transmute.plugin.playerHelper.sendDirectedMessage(shape.player, unsetMessage);
			}
			else {
				if ("off".equalsIgnoreCase(argStr))
					throw new YiffBukkitCommandException("Already off");
				shape.setData(index, (byte)(oldData | bit));
				shape.transmute.plugin.playerHelper.sendDirectedMessage(shape.player, setMessage);
			}
		}
	}

	static class EntityStatusMobAction implements MobAction {
		private final byte status;
		private final String message;

		public EntityStatusMobAction(int i, String message) {
			this.status = (byte) i;
			this.message = message;
		}

		@Override
		public void run(MobShape shape, String[] args, String argStr) {
			final Location location = shape.entity.getLocation();
			final World world = location.getWorld();
			for (Player player : world.getPlayers()) {
				PlayerHelper.sendPacketToPlayer(player, new Packet38EntityStatus(shape.entityID, status));
			}

			shape.transmute.plugin.playerHelper.sendDirectedMessage(shape.player, message);
		}
	}

	static class MetadataMobAction implements MobAction {
		private final int index;
		private final Object value;
		private final String message;

		public MetadataMobAction(int index, Object value, String message) {
			this.index = index;
			this.value = value;
			this.message = message;
		}

		@Override
		public void run(MobShape shape, String[] args, String argStr) {
			shape.setData(index, value);

			shape.transmute.plugin.playerHelper.sendDirectedMessage(shape.player, message);
		}
	}
}
