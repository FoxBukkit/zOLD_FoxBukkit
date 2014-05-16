package de.doridian.yiffbukkit.spawning;

import de.doridian.yiffbukkit.core.YiffBukkit;
import de.kumpelblase2.remoteentities.EntityManager;
import de.kumpelblase2.remoteentities.RemoteEntities;
import de.kumpelblase2.remoteentities.api.RemoteEntity;
import de.kumpelblase2.remoteentities.api.RemoteEntityType;
import org.bukkit.Location;

public class SpawnUtilsNPCDependency {
	public static EntityManager entityManager = null;

	public static RemoteEntity makeNPC(String name, Location location) {
		if(entityManager == null)
			entityManager = RemoteEntities.createManager(YiffBukkit.instance);
		RemoteEntity entity = entityManager.createNamedEntity(RemoteEntityType.Human, location, name);
		entity.setPushable(false);
		entity.setStationary(true, true);
		return entity;
	}
}
