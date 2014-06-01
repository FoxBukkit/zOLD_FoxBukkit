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
package de.doridian.foxbukkit.main;

public class PermissionDeniedException extends FoxBukkitCommandException {
	private static final long serialVersionUID = 1L;

	public PermissionDeniedException() {
		this("Permission denied!");
	}

	public PermissionDeniedException(String message) {
		super(message);
		setColor('4');
	}

	public PermissionDeniedException(Throwable cause) {
		this("Permission denied!", cause);
	}

	public PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
		setColor('4');
	}

}
