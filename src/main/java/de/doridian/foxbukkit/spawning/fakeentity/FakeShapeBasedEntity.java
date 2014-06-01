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
package de.doridian.foxbukkit.spawning.fakeentity;

import de.doridian.foxbukkit.core.FoxBukkit;
import de.doridian.foxbukkit.main.FoxBukkitCommandException;
import de.doridian.foxbukkit.transmute.EntityTypeNotFoundException;
import de.doridian.foxbukkit.transmute.Shape;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FakeShapeBasedEntity extends FakeEntity {
	private final Shape shape;

	public FakeShapeBasedEntity(Location location, String mobType) throws EntityTypeNotFoundException {
		super(location);

		shape = Shape.getShape(FoxBukkit.instance.transmute, this, mobType);
	}

	public FakeShapeBasedEntity(Location location, int mobType) throws EntityTypeNotFoundException {
		super(location);

		shape = Shape.getShape(FoxBukkit.instance.transmute, this, mobType);
	}

	@Override
	public void send(Player player) {
		shape.createTransmutedEntity(player);
	}

	public void runAction(CommandSender commandSender, String action) throws FoxBukkitCommandException {
		shape.runAction(commandSender, action);
	}

	public Shape getShape() {
		return shape;
	}
}
