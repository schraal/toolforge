package nl.toolforge.karma.core.bundle;

import nl.toolforge.karma.core.test.BaseTest;
import nl.toolforge.karma.core.KarmaRuntimeException;

import java.util.ResourceBundle;
import java.util.Locale;

/**
 * Test class for {@link BundleCache}.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class TestBundleCache extends BaseTest {

	private BundleCache cache = null;

	public void setUp() {

		super.setUp();

		cache = BundleCache.getInstance();
		cache.register("BUNDLE1", ResourceBundle.getBundle("error-messages", Locale.ENGLISH));
	}

	public void tearDown() {
		cache.flush();
	}

	public void testFlush() {

		cache.flush();

		assertEquals(cache.getBundle("BUNDLE1"), null);
	}

	/**
	 * Expects <code>error-messages_en.properties</code> on the classpath.
	 */
	public void testRegister1() {

		BundleCache cache2 = BundleCache.getInstance();
		ResourceBundle bundle = ResourceBundle.getBundle("error-messages", Locale.ENGLISH);

		assertNotNull(bundle);

		cache2.register("BUNDLE", bundle);

		assertEquals(cache2.getBundle("BUNDLE"), bundle);
	}

	public void testRegister2() {

		try {

			cache.register("BUNDLE", null);
			fail("A NullPointerException should have been thrown.");

		} catch (NullPointerException n) {
			assertTrue(true);
		}
	}

	public void testRegister3() {

		try {
			cache.register("", null);
			fail("A KarmaRuntimeException should have been thrown. First parameter cannot be empty.");
		} catch (KarmaRuntimeException n) {
			assertTrue(true);
		}

	}

	public void testRegister4() {

		try {
			cache.register(null, null);
			fail("A KarmaRuntimeException should have been thrown. First parameter cannot be null.");
		} catch (KarmaRuntimeException n) {
			assertTrue(true);
		}
	}

	public void testRegister5() {

		try {

			ResourceBundle bundle = ResourceBundle.getBundle("error-messages", Locale.ENGLISH);
			cache.register("bundle_1", bundle);

			assertEquals(cache.getBundle("BUNDLE_1"), bundle);

		} catch (Exception n) {
			fail();
		}
	}

	public void testRegister6() {

		try {

			ResourceBundle bundle = ResourceBundle.getBundle("error-messages", Locale.ENGLISH);
			cache.register("bundle_1", bundle);

			cache.getBundle("BUNDLE_2");

		} catch (Exception n) {

			fail("A KarmaRuntimeException should have been thrown. First parameter cannot be null.");
			fail();
		}
	}
}
