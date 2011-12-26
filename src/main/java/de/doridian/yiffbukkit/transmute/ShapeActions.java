package de.doridian.yiffbukkit.transmute;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import net.minecraft.server.Packet38EntityStatus;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ShapeActions {
	private static HashMap<Integer, Map<String, ShapeAction>> mobActions = new HashMap<Integer, Map<String, ShapeAction>>();

	public static final Map<String, ShapeAction> get(int mobType) {
		return mobActions.get(mobType);
	}

	static {
		//registerMobActions(1, // Item
		//registerMobActions(2, // XPOrb
		//registerMobActions(9, // Painting
		//registerMobActions(10, // Arrow
		//registerMobActions(11, // Snowball
		//registerMobActions(12, // Fireball
		//registerMobActions(13, // SmallFireball
		//registerMobActions(14, // ThrownEnderpearl
		//registerMobActions(15, // EyeOfEnderSignal
		//registerMobActions(20, // PrimedTnt
		//registerMobActions(21, // FallingSand

		registerMobActions(40, // Minecart
				"help",
				new HelpMobAction("/sac empty|chest|furnace|bob[ <amount>[ <time>]]|smoke [on|off]"),
				"empty",
				new ShapeAction() { @Override public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
					((VehicleShape) shape).setVehicleType(10);
				}},
				"chest",
				new ShapeAction() { @Override public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
					((VehicleShape) shape).setVehicleType(11);
				}},
				"furnace",
				new ShapeAction() { @Override public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
					((VehicleShape) shape).setVehicleType(12);
				}},
				"bob",
				new VehicleBobAction(),
				"smoke",
				new MetadataBitMobAction(16, 0x1, "No longer smoking.", "Now smoking.")
		);

		registerMobActions(41, // Boat
				"help",
				new HelpMobAction("/sac bob[ <amount>[ <time>]]"),
				"bob",
				new VehicleBobAction(),
				"17",
				new MetadataCustomValueAction(17, "Set your 17 to %s", Integer.class),
				"18",
				new MetadataCustomValueAction(18, "Set your 18 to %s", Integer.class),
				"19",
				new MetadataCustomValueAction(19, "Set your 19 to %s", Integer.class)
		);
		//registerMobActions(48, // Mob
		//registerMobActions(49, // Monster

		registerMobActions(50, // Creeper
				"help",
				new HelpMobAction("/sac hiss|charge [on|off]"),
				"sss", "ssss", "sssss", "ssssss", "hiss", "fuse", "ignite",
				new ShapeAction() { @Override public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
					shape.setData(16, (byte) 0);
					shape.setData(16, (byte) 1);

					shape.transmute.plugin.playerHelper.sendDirectedMessage(player, "Hissing...");
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
				new MetadataCustomValueAction(16, "Set your size to %s", Byte.class)
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

		registerMobActions(58, // Enderman
				"help",
				new HelpMobAction("/sac type <0..255>|data <0..15>"),
				"type",
				new MetadataCustomValueAction(16, "Set the type of the block you carry to %s", Byte.class),
				"data",
				new MetadataCustomValueAction(17, "Set the data value of the block you carry to %s", Byte.class)
		);

		//registerMobActions(59, // CaveSpider
		//registerMobActions(60, // Silverfish
		//registerMobActions(61, // Blaze

		registerMobActions(62, // LavaSlime
				slimeActions
		);

		registerMobActions(63, // EnderDragon
				"help",
				new HelpMobAction("/sac health <0..200>"),
				"health",
				new MetadataCustomValueAction(16, "Set your health to %s", Integer.class)
		);
		
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
				new ShapeAction() { @Override public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
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

					shape.transmute.plugin.playerHelper.sendDirectedMessage(player, "You are now "+dyeColor.toString().toLowerCase().replace('_',' ')+".");
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
		//registerMobActions(200, // EnderCrystal
		//registerMobActions(1000, // FishingHook
		//registerMobActions(1001, // Potion
		//registerMobActions(1002, // Egg
	}

	private static void registerMobActions(int mobType, Object... objects) {
		Map<String, ShapeAction> actions = new HashMap<String, ShapeAction>();

		List<String> names = new ArrayList<String>();
		for (Object object : objects) {
			if (object instanceof String) {
				names.add((String)object);
			}
			else if (object instanceof ShapeAction) {
				for (String name : names) {
					actions.put(name, (ShapeAction)object);
				}
				names.clear();
			}
		}

		mobActions.put(mobType, actions);
	}

	private static class VehicleBobAction implements ShapeAction {
		@Override
		public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
			final int amplitude;
			final int time;
			try {
				if (args.length < 1) {
					amplitude = 30;
				}
				else {
					amplitude = Integer.parseInt(args[0]);
				}

				if (args.length < 2) {
					time = 10;
				}
				else {
					time = Integer.parseInt(args[1]);
				}
			}
			catch (NumberFormatException e) {
				throw new YiffBukkitCommandException("Number expected.", e);
			}

			shape.setData(17, 0);
			shape.setData(19, 0);
			shape.setData(17, time);
			shape.setData(18, shape.getDataInteger(18) > 0 ? -1 : 1);
			shape.setData(19, amplitude);
		}
	}

	private static class MetadataCustomValueAction implements ShapeAction {
		final int index;
		final String message;
		final Constructor<? extends Number> constructor;

		public MetadataCustomValueAction(int index, String message, Class<? extends Number> numberClass) {
			this.index = index;
			this.message = message;
			try {
				this.constructor = numberClass.getConstructor(String.class);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
			try {
				final Number value = constructor.newInstance(argStr);
				shape.setData(index, value);

				shape.transmute.plugin.playerHelper.sendDirectedMessage(player, String.format(message, value.toString()));
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof RuntimeException) {
					throw (RuntimeException) e.getTargetException();
				}

				throw new RuntimeException(e);
			}
		}
	}

	private static class HelpMobAction implements ShapeAction {
		final String message;

		public HelpMobAction(String message) {
			this.message = message;
		}

		@Override
		public void run(EntityShape shape, Player player, String[] args, String argStr) {
			shape.transmute.plugin.playerHelper.sendDirectedMessage(player, message);
		}

	}

	private static class MetadataBitMobAction implements ShapeAction {
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
		public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
			final byte oldData = shape.getDataByte(index);
			if ((oldData & bit) != 0) {
				if ("on".equalsIgnoreCase(argStr))
					throw new YiffBukkitCommandException("Already on");

				shape.setData(index, (byte)(oldData & ~bit));
				shape.transmute.plugin.playerHelper.sendDirectedMessage(player, unsetMessage);
			}
			else {
				if ("off".equalsIgnoreCase(argStr))
					throw new YiffBukkitCommandException("Already off");
				shape.setData(index, (byte)(oldData | bit));
				shape.transmute.plugin.playerHelper.sendDirectedMessage(player, setMessage);
			}
		}
	}

	static class EntityStatusMobAction implements ShapeAction {
		private final byte status;
		private final String message;

		public EntityStatusMobAction(int i, String message) {
			this.status = (byte) i;
			this.message = message;
		}

		@Override
		public void run(EntityShape shape, Player player, String[] args, String argStr) {
			shape.sendPacketToPlayersAround(new Packet38EntityStatus(shape.entityId, status));
			shape.transmute.plugin.playerHelper.sendDirectedMessage(player, message);
		}
	}

	static class MetadataMobAction implements ShapeAction {
		private final int index;
		private final Object value;
		private final String message;

		public MetadataMobAction(int index, Object value, String message) {
			this.index = index;
			this.value = value;
			this.message = message;
		}

		@Override
		public void run(EntityShape shape, Player player, String[] args, String argStr) {
			shape.setData(index, value);

			shape.transmute.plugin.playerHelper.sendDirectedMessage(player, message);
		}
	}
}
