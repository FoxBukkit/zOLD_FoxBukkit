package de.doridian.yiffbukkit.spawning;

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
