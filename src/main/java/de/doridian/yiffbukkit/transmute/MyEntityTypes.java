package de.doridian.yiffbukkit.transmute;

import de.doridian.yiffbukkit.main.util.Utils;
import net.minecraft.server.v1_6_R2.Entity;
import net.minecraft.server.v1_6_R2.EntityEgg;
import net.minecraft.server.v1_6_R2.EntityEnderDragon;
import net.minecraft.server.v1_6_R2.EntityEnderPearl;
import net.minecraft.server.v1_6_R2.EntityEnderSignal;
import net.minecraft.server.v1_6_R2.EntityExperienceOrb;
import net.minecraft.server.v1_6_R2.EntityFallingBlock;
import net.minecraft.server.v1_6_R2.EntityFireworks;
import net.minecraft.server.v1_6_R2.EntityFishingHook;
import net.minecraft.server.v1_6_R2.EntityMagmaCube;
import net.minecraft.server.v1_6_R2.EntityOcelot;
import net.minecraft.server.v1_6_R2.EntityPotion;
import net.minecraft.server.v1_6_R2.EntityTNTPrimed;
import net.minecraft.server.v1_6_R2.EntityTypes;
import net.minecraft.server.v1_6_R2.EntityWither;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MyEntityTypes {
	@SuppressWarnings("unchecked")
	private static final Map<String, Class<? extends Entity>> typeNameToClass = new HashMap<String, Class<? extends Entity>>((Map<String, Class<? extends Entity>>) Utils.getPrivateValue(EntityTypes.class, null, "b")); // v1_6_R2
	@SuppressWarnings("unchecked")
	private static final Map<Class<? extends Entity>, String> classToTypeName = new HashMap<Class<? extends Entity>, String>((Map<Class<? extends Entity>, String>) Utils.getPrivateValue(EntityTypes.class, null, "c")); // v1_6_R2
	@SuppressWarnings("unchecked")
	private static final Map<Integer, Class<? extends Entity>> idToClass = new HashMap<Integer, Class<? extends Entity>>((Map<Integer, Class<? extends Entity>>) Utils.getPrivateValue(EntityTypes.class, null, "d")); // v1_6_R2
	@SuppressWarnings("unchecked")
	private static final Map<Class<? extends Entity>, Integer> classToId = new HashMap<Class<? extends Entity>, Integer>((Map<Class<? extends Entity>, Integer>) Utils.getPrivateValue(EntityTypes.class, null, "e")); // v1_6_R2

	static {
		addAliases(EntityExperienceOrb.class, "XP");
		addAliases(EntityEnderPearl.class, "Enderpearl");
		addAliases(EntityEnderSignal.class, "EnderSignal", "EyeOfEnder", "EnderEye");
		addAliases(EntityFireworks.class, "Fireworks", "Firework");
		addAliases(EntityTNTPrimed.class, "Tnt");
		addAliases(EntityFallingBlock.class, "FallingBlock");
		addAliases(EntityMagmaCube.class, "MagmaCube");
		addAliases(EntityEnderDragon.class, "Dragon");
		addAliases(EntityOcelot.class, "Ocelot", "Cat");
		addAliases(EntityWither.class, "Wither");

		addType(EntityFishingHook.class, 1000, "FishingHook", "Fish", "Hook", "FishingPole");
		addType(EntityPotion.class, 1001, "Potion", "ThrownPotion");
		addType(EntityEgg.class, 1002, "Egg", "ThrownEgg");
	}

	private static void addAliases(Class<? extends Entity> cls, String... aliases) {
		for (String alias : aliases) {
			typeNameToClass.put(alias, cls);
		}
	}

	private static void addType(Class<? extends Entity> cls, int mobType, String... aliases) {
		for (String alias : aliases) {
			typeNameToClass.put(alias, cls);
		}

		classToTypeName.put(cls, aliases[0]);
		idToClass.put(Integer.valueOf(mobType), cls);
		classToId.put(cls, Integer.valueOf(mobType));
	}

	public static final Class<? extends net.minecraft.server.v1_6_R2.Entity> typeNameToClass(String mobType) throws EntityTypeNotFoundException {
		for (Entry<String, Class<? extends net.minecraft.server.v1_6_R2.Entity>> entry : typeNameToClass.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(mobType))
				return entry.getValue();
		}

		throw new EntityTypeNotFoundException();
		//return typeNameToClass.get(mobType);
	}
	
	public static final int classToId(Class<? extends net.minecraft.server.v1_6_R2.Entity> mobType) throws EntityTypeNotFoundException {
		final Integer id = classToId.get(mobType);
		if (id == null)
			throw new EntityTypeNotFoundException();

		return id;
	}

	public static final String classToTypeName(Class<? extends net.minecraft.server.v1_6_R2.Entity> mobType) throws EntityTypeNotFoundException {
		final String typeName = classToTypeName.get(mobType);
		if (typeName == null)
			throw new EntityTypeNotFoundException();

		return typeName;
	}

	public static final Class<? extends net.minecraft.server.v1_6_R2.Entity> idToClass(int id) throws EntityTypeNotFoundException {
		final Class<? extends net.minecraft.server.v1_6_R2.Entity> mobType = idToClass.get(id);
		if (mobType == null)
			throw new EntityTypeNotFoundException();

		return mobType;
	}
}
