/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core.bundle;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.test.BaseTest;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Test class for {@link BundleCache}.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestBundleCache extends BaseTest {

	private BundleCache cache = null;

	public void setUp() {
		super.setUp();
		cache = BundleCache.getInstance();
	}

	public void tearDown() {
    super.tearDown();
		cache.flush();
	}

	public void testFlush() {

		cache = BundleCache.getInstance();

		try {
			cache.flush();
			cache.getBundle("BUNDLE1");

			fail("Impossible, no such bundle in the cache.");
		} catch (KarmaRuntimeException e) {
			assertTrue(true);
		}
	}

	public void testRegister1() {

		BundleCache cache2 = BundleCache.getInstance();

		ResourceBundle bundle = null;
		bundle = ResourceBundle.getBundle("error-messages", Locale.ENGLISH);
		assertNotNull(bundle);

		cache.flush();
		cache2.register("BUNDLE", bundle);

		assertEquals(cache2.getBundle("BUNDLE"), bundle);
	}

	public void testRegister2() {

		try {
			cache.flush();
			cache.register("BUNDLE", null);
			fail("A NullPointerException should have been thrown.");

		} catch (NullPointerException n) {
			assertTrue(true);
		}
	}

	public void testRegister3() {

		try {
			cache.flush();
			cache.register("", null);
			fail("A KarmaRuntimeException should have been thrown. First parameter cannot be empty.");
		} catch (KarmaRuntimeException n) {
			assertTrue(true);
		}

	}

	public void testRegister4() {

		try {
			cache.flush();
			cache.register(null, null);
			fail("A KarmaRuntimeException should have been thrown. First parameter cannot be null.");
		} catch (KarmaRuntimeException n) {
			assertTrue(true);
		}
	}

	public void testRegister5() {

		try {
			cache.flush();
			ResourceBundle bundle = ResourceBundle.getBundle("error-messages", Locale.ENGLISH);
			cache.register("bundle_1", bundle);

			assertEquals(cache.getBundle("bundle_1"), bundle);

		} catch (Exception n) {
			fail();
		}
	}
}
