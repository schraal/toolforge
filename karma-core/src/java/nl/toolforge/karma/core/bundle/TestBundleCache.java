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
