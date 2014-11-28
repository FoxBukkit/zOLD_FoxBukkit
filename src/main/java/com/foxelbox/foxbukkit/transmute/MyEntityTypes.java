/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.foxelbox.foxbukkit.transmute;

import net.minecraft.server.v1_8_R1.Entity;
import net.minecraft.server.v1_8_R1.EntityEgg;
import net.minecraft.server.v1_8_R1.EntityEnderDragon;
import net.minecraft.server.v1_8_R1.EntityEnderPearl;
import net.minecraft.server.v1_8_R1.EntityEnderSignal;
import net.minecraft.server.v1_8_R1.EntityExperienceOrb;
import net.minecraft.server.v1_8_R1.EntityFallingBlock;
import net.minecraft.server.v1_8_R1.EntityFireworks;
import net.minecraft.server.v1_8_R1.EntityFishingHook;
import net.minecraft.server.v1_8_R1.EntityHorse;
import net.minecraft.server.v1_8_R1.EntityLeash;
import net.minecraft.server.v1_8_R1.EntityMagmaCube;
import net.minecraft.server.v1_8_R1.EntityOcelot;
import net.minecraft.server.v1_8_R1.EntityPotion;
import net.minecraft.server.v1_8_R1.EntityTNTPrimed;
import net.minecraft.server.v1_8_R1.EntityTypes;
import net.minecraft.server.v1_8_R1.EntityWither;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MyEntityTypes {
	@SuppressWarnings("unchecked")
	private static final Map<String, Class<? extends Entity>> typeNameToClass = new HashMap<>((Map<String, Class<? extends Entity>>) EntityTypes.c); // v1_7_R1
	@SuppressWarnings("unchecked")
	private static final Map<Class<? extends Entity>, String> classToTypeName = new HashMap<>((Map<Class<? extends Entity>, String>) EntityTypes.d); // v1_7_R1
	@SuppressWarnings("unchecked")
	private static final Map<Integer, Class<? extends Entity>> idToClass = new HashMap<>((Map<Integer, Class<? extends Entity>>) EntityTypes.e); // v1_7_R1
	@SuppressWarnings("unchecked")
	private static final Map<Class<? extends Entity>, Integer> classToId = new HashMap<>((Map<Class<? extends Entity>, Integer>) EntityTypes.f); // v1_7_R1

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
		addAliases(EntityLeash.class, "Leash");
		addAliases(EntityHorse.class, "Horse");

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
		idToClass.put(mobType, cls);
		classToId.put(cls, mobType);
	}

	public static Class<? extends net.minecraft.server.v1_8_R1.Entity> typeNameToClass(String mobType) throws EntityTypeNotFoundException {
		for (Entry<String, Class<? extends net.minecraft.server.v1_8_R1.Entity>> entry : typeNameToClass.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(mobType))
				return entry.getValue();
		}

		throw new EntityTypeNotFoundException();
		//return typeNameToClass.get(mobType);
	}
	
	public static int classToId(Class<? extends net.minecraft.server.v1_8_R1.Entity> mobType) throws EntityTypeNotFoundException {
		final Integer id = classToId.get(mobType);
		if (id == null)
			throw new EntityTypeNotFoundException();

		return id;
	}

	public static String classToTypeName(Class<? extends net.minecraft.server.v1_8_R1.Entity> mobType) throws EntityTypeNotFoundException {
		final String typeName = classToTypeName.get(mobType);
		if (typeName == null)
			throw new EntityTypeNotFoundException();

		return typeName;
	}

	public static Class<? extends net.minecraft.server.v1_8_R1.Entity> idToClass(int id) throws EntityTypeNotFoundException {
		final Class<? extends net.minecraft.server.v1_8_R1.Entity> mobType = idToClass.get(id);
		if (mobType == null)
			throw new EntityTypeNotFoundException();

		return mobType;
	}
}
