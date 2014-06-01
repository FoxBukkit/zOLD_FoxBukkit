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
package de.doridian.foxbukkit.spawning;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class SpawnUtilsTest {
	@Ignore
	@Test(expected = ExceptionInInitializerError.class)
	public void testIsValidParticle1() throws Exception {
		assertTrue(SpawnUtils.isValidParticle("iconcrack_1"));
	}

	@Ignore
	@Test(expected = NoClassDefFoundError.class)
	public void testIsValidParticle2() throws Exception {
		assertTrue(SpawnUtils.isValidParticle("tilecrack_1_1"));
	}

	@Test
	public void testIsValidParticle3() throws Exception {
		assertFalse(SpawnUtils.isValidParticle("iconcrack_0"));
	}
}
