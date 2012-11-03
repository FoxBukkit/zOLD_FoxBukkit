package de.doridian.yiffbukkit.transmute;

import de.doridian.yiffbukkit.main.YiffBukkitCommandException;
import de.doridian.yiffbukkit.spawning.commands.GiveCommand;
import de.doridian.yiffbukkitsplit.YiffBukkit;
import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.server.EnumArt;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ShapeActions {
	private static TIntObjectMap<Map<String, ShapeAction>> mobActions = new TIntObjectHashMap<Map<String, ShapeAction>>();

	public static Map<String, ShapeAction> get(int mobType) {
		return mobActions.get(mobType);
	}

	static {
		registerMobActions(1, // Item
				"help",
				new HelpMobAction("/sac type <type>[:<data>][ <count>]"),
				"type",
				new ShapeAction() { @Override public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
					final ItemShape itemShape = (ItemShape) shape;

					final int count;
					if (args.length >= 2) {
						try {
							count = Integer.valueOf(args[1]);
						}
						catch(NumberFormatException e) {
							throw new YiffBukkitCommandException("Number expected");
						}
					}
					else {
						count = itemShape.getCount();
					}

					String materialName = args[0];
					final int colonPos = materialName.indexOf(':');
					String colorName = null;
					if (colonPos >= 0) {
						colorName = materialName.substring(colonPos+1);
						materialName = materialName.substring(0, colonPos);
					}
					final Material material = GiveCommand.matchMaterial(materialName);
					if (material == null) {
						throw new YiffBukkitCommandException("Material "+materialName+" not found");
					}

					if (material.getId() == 0)
						throw new YiffBukkitCommandException("Material "+materialName+" not found");

					final ItemStack stack = new ItemStack(material, count, (short) itemShape.getDataValue());

					if (colorName != null) {
						colorName = colorName.toUpperCase();
						Short dataValue = GiveCommand.getDataValue(material, colorName);
						if (dataValue != null) {
							stack.setDurability(dataValue);
						}
						else if (material == Material.WOOL || material == Material.INK_SACK) {
							try {
								DyeColor dyeColor = DyeColor.valueOf(colorName.replace("GREY", "GRAY"));

								if (material == Material.WOOL)
									stack.setDurability(dyeColor.getData());
								else
									stack.setDurability((short) (15-dyeColor.getData()));
							}
							catch (IllegalArgumentException e) {
								throw new YiffBukkitCommandException("Color "+colorName+" not found", e);
							}
						}
						else {
							throw new YiffBukkitCommandException("Material "+materialName+" cannot have a data value.");
						}
					}

					itemShape.setType(stack.getTypeId(), stack.getDurability(), stack.getAmount());
				}}
		);

		//registerMobActions(2, // XPOrb

		registerMobActions(9, // Painting
				"help",
				new HelpMobAction("/sac art <name>"),
				"art", "painting", "name", "type",
				new ShapeAction() { @Override public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
					for (EnumArt art : EnumArt.values()) {
						final String currentName = art.B;
						if (!currentName.equalsIgnoreCase(argStr))
							continue;

						((PaintingShape) shape).setPaintingName(currentName);
						PlayerHelper.sendDirectedMessage(player, "Set painting to "+currentName);
						return;
					}

					throw new YiffBukkitCommandException("Cannot find a painting with that name.");
				}}
		);

		//registerMobActions(10, // Arrow
		//registerMobActions(11, // Snowball
		//registerMobActions(12, // Fireball
		//registerMobActions(13, // SmallFireball
		//registerMobActions(14, // ThrownEnderpearl
		//registerMobActions(15, // EyeOfEnderSignal
		//registerMobActions(16, // ThrownPotion
		//registerMobActions(17, // ThrownExpBottle
		//registerMobActions(20, // PrimedTnt
		//registerMobActions(21, // FallingSand

		registerMobActions(40, // Minecart
				"help",
				new HelpMobAction("/sac empty|chest|furnace|bob[ <amount>[ <time>]]|smoke [on|off]"),
				"empty",
				new ShapeAction() { @Override public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
					((VehicleShape) shape).setVehicleType(10);

					PlayerHelper.sendDirectedMessage(player, "Now a regular minecart...");
				}},
				"chest",
				new ShapeAction() { @Override public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
					((VehicleShape) shape).setVehicleType(11);

					PlayerHelper.sendDirectedMessage(player, "Now a storage minecart...");
				}},
				"furnace",
				new ShapeAction() { @Override public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
					((VehicleShape) shape).setVehicleType(12);

					PlayerHelper.sendDirectedMessage(player, "Now a powered minecart...");
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

					PlayerHelper.sendDirectedMessage(player, "Hissing...");
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

		registerMobActions(64, // WitherBoss
				"help",
				new HelpMobAction("/sac health <0..300>|size <0..900 - inverse>|headleft/center/right [<name>]"),
				"health",
				new MetadataCustomValueAction(16, "Set your health to %s", Integer.class),
				"head1", "headcenter", "centerhead",
				new WitherHeadAction(17),
				"head2", "headright", "righthead",
				new WitherHeadAction(18),
				"head3", "headleft", "lefthead",
				new WitherHeadAction(19),
				"size",
				new MetadataCustomValueAction(20, "Set your size to %s", Integer.class)
		);

		registerMobActions(65, // Bat
				"help",
				new HelpMobAction("/sac land|takeoff|flap [on|off]"),
				"flap",
				new MetadataBitMobAction(16, (byte) 0x1, "Took off...", "Landed..."),
				"land",
				new MetadataMobAction(16, (byte) 1, "Landed..."),
				"takeoff",
				new MetadataMobAction(16, (byte) 0, "Took off...")
		);

		//registerMobActions(66, // Witch

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

					PlayerHelper.sendDirectedMessage(player, "You are now "+dyeColor.toString().toLowerCase().replace('_',' ')+".");
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

		registerMobActions(98, // Ozelot
				"help",
				new HelpMobAction("/sac sit [on|off]|type (black|red|siamese|wild)|baby|adult"),
				"sit",
				new MetadataBitMobAction(16, 0x1, "Getting up...", "Sitting down..."),
				"baby",
				new MetadataMobAction(12, -24000, "Now a baby..."),
				"adult",
				new MetadataMobAction(12, 0, "Now an adult..."),
				"type",
				new ShapeAction() { @Override public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
					argStr = argStr.toLowerCase();
					byte data;
					if(argStr.equals("black")) {
						data = 0x1;
					} else if(argStr.equals("red")) {
						data = 0x2;
					} else if(argStr.equals("siamese")) {
						data = 0x3;
					} else if(argStr.equals("wild") || argStr.equals("ocelot")) {
						data = 0x0;
					} else {
						throw new YiffBukkitCommandException("Invalid ocelot type");
					}
					shape.setData(18, data);
					PlayerHelper.sendDirectedMessage(player, "Changed ocelot type!");
				}}
		);

		//registerMobActions(99, //VillagerGolem
		//registerMobActions(120, // Villager
		//registerMobActions(200, // EnderCrystal
		//registerMobActions(1000, // FishingHook

		registerMobActions(1001, // Potion
				"help",
				new HelpMobAction("/sac type <type>"),
				"type",
				new ShapeAction() { @Override public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
					try {
						((VehicleShape) shape).setVehicleType(Integer.parseInt(argStr));
					}
					catch (NumberFormatException e) {
						throw new YiffBukkitCommandException("Number expected.", e);
					}

					PlayerHelper.sendDirectedMessage(player, "Now potion type "+argStr+"...");
				}}
		);

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

				PlayerHelper.sendDirectedMessage(player, String.format(message, value.toString()));
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
			PlayerHelper.sendDirectedMessage(player, message);
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
				PlayerHelper.sendDirectedMessage(player, unsetMessage);
			}
			else {
				if ("off".equalsIgnoreCase(argStr))
					throw new YiffBukkitCommandException("Already off");
				shape.setData(index, (byte)(oldData | bit));
				PlayerHelper.sendDirectedMessage(player, setMessage);
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
			shape.sendEntityStatus(status);
			PlayerHelper.sendDirectedMessage(player, message);
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

			PlayerHelper.sendDirectedMessage(player, message);
		}
	}

	static class WitherHeadAction implements ShapeAction {
		private final int index;

		public WitherHeadAction(int index) {
			this.index = index;
		}

		@Override
		public void run(EntityShape shape, Player player, String[] args, String argStr) throws YiffBukkitCommandException {
			final Player target;
			final boolean toggle;
			if (argStr.isEmpty()) {
				target = player;
				toggle = true;
			}
			else {
				target = YiffBukkit.instance.playerHelper.matchPlayerSingle(argStr);
				toggle = false;
			}

			if (!toggle) {
				shape.setData(index, target.getEntityId());

				PlayerHelper.sendDirectedMessage(player, "That head is now following "+target.getName()+".");
			}
			else if (shape.getDataInteger(index) == 0) {
				shape.setData(index, target.getEntityId());

				PlayerHelper.sendDirectedMessage(player, "That head is now following you.");
			}
			else {
				shape.setData(index, 0);

				PlayerHelper.sendDirectedMessage(player, "That head is no longer following anyone.");
			}
		}
	}
}
