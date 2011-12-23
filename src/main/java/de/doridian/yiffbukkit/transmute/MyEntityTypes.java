package de.doridian.yiffbukkit.transmute;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityEgg;
import net.minecraft.server.EntityEnderPearl;
import net.minecraft.server.EntityEnderSignal;
import net.minecraft.server.EntityExperienceOrb;
import net.minecraft.server.EntityFallingBlock;
import net.minecraft.server.EntityFishingHook;
import net.minecraft.server.EntityMagmaCube;
import net.minecraft.server.EntityPotion;
import net.minecraft.server.EntityTypes;
import de.doridian.yiffbukkit.util.Utils;

public class MyEntityTypes {
	@SuppressWarnings("unchecked")
	private static final Map<String, Class<? extends Entity>> typeNameToClass = new HashMap<String, Class<? extends Entity>>((Map<String, Class<? extends Entity>>) Utils.getPrivateValue(EntityTypes.class, null, "a"));
	@SuppressWarnings("unchecked")
	private static final Map<Class<? extends Entity>, String> classToTypeName = new HashMap<Class<? extends Entity>, String>((Map<Class<? extends Entity>, String>) Utils.getPrivateValue(EntityTypes.class, null, "b"));
	@SuppressWarnings("unchecked")
	private static final Map<Integer, Class<? extends Entity>> idToClass = new HashMap<Integer, Class<? extends Entity>>((Map<Integer, Class<? extends Entity>>) Utils.getPrivateValue(EntityTypes.class, null, "c"));
	@SuppressWarnings("unchecked")
	private static final Map<Class<? extends Entity>, Integer> classToId = new HashMap<Class<? extends Entity>, Integer>((Map<Class<? extends Entity>, Integer>) Utils.getPrivateValue(EntityTypes.class, null, "d"));

	static {
		addAliases(EntityExperienceOrb.class, "XP");
		addAliases(EntityEnderPearl.class, "Enderpearl");
		addAliases(EntityEnderSignal.class, "EnderSignal", "EyeOfEnder", "EnderEye");
		addAliases(EntityFallingBlock.class, "FallingBlock");
		addAliases(EntityMagmaCube.class, "MagmaCube");

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

	public static final Class<? extends net.minecraft.server.Entity> typeNameToClass(String mobType) throws EntityTypeNotFoundException {
		for (Entry<String, Class<? extends net.minecraft.server.Entity>> entry : typeNameToClass.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(mobType))
				return entry.getValue();
		}

		throw new EntityTypeNotFoundException();
		//return typeNameToClass.get(mobType);
	}
	
	public static final int classToId(Class<? extends net.minecraft.server.Entity> mobType) throws EntityTypeNotFoundException {
		final Integer id = classToId.get(mobType);
		if (id == null)
			throw new EntityTypeNotFoundException();

		return id;
	}

	public static final String classToTypeName(Class<? extends net.minecraft.server.Entity> mobType) throws EntityTypeNotFoundException {
		final String typeName = classToTypeName.get(mobType);
		if (typeName == null)
			throw new EntityTypeNotFoundException();

		return typeName;
	}

	public static final Class<? extends net.minecraft.server.Entity> idToClass(int id) throws EntityTypeNotFoundException {
		final Class<? extends net.minecraft.server.Entity> mobType = idToClass.get(id);
		if (mobType == null)
			throw new EntityTypeNotFoundException();

		return mobType;
	}
}
