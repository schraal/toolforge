package nl.toolforge.karma.core;

import junit.framework.TestCase;
import nl.toolforge.core.util.file.MyFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public final class TestLocalEnvironment extends TestCase {
	private static File f1;
	private static File f2;
	private static File f3;
	private static Properties p;
	private static LocalEnvironment localEnvironment;

	static {
		try {
			f1 = MyFileUtils.createTempDirectory();
			f2 = MyFileUtils.createTempDirectory();
			f3 = MyFileUtils.createTempDirectory();

			p = new Properties();
			p.put(LocalEnvironment.DEVELOPMENT_STORE_DIRECTORY, f1.getPath());
			p.put(LocalEnvironment.MANIFEST_STORE_DIRECTORY, f2.getPath());
			p.put(LocalEnvironment.LOCATION_STORE_DIRECTORY, f3.getPath());

		} catch (IOException ioe) {
			fail();
		}
	}


	public void testConstructor() {
		try {
			localEnvironment = LocalEnvironment.getInstance(p);
			assertNotNull(localEnvironment);
    } catch (KarmaException ke) {
      fail(ke.getMessage());
    } catch (KarmaRuntimeException kre) {
			fail(kre.getMessage());
		}
	}

	public void testGetDevelopmentHome() {
		try {
			assertEquals(f1, localEnvironment.getDevelopmentHome());

			f1.delete();
			try {
				localEnvironment.getDevelopmentHome();
				fail("The development home should not have been there");
			} catch (KarmaException ke) {
				assertTrue(true);
			}
		} catch (KarmaException e) {
			fail();
		}

	}

	public void testGetManifestStore() {
		try {
			assertEquals(f2, localEnvironment.getManifestStore());

			f2.delete();
			try {
				localEnvironment.getManifestStore();
				fail("The manifest store should not have been there");
			} catch (KarmaException ke) {
				assertTrue(true);
			}
		} catch (KarmaException e) {
			fail();
		}

	}

	public void testGetLocationStore() {
		try {
			assertEquals(f3, localEnvironment.getLocationStore());

			f3.delete();
			try {
				localEnvironment.getLocationStore();
				fail("The location store should not have been there");
			} catch (KarmaException ke) {
				assertTrue(true);
			}
		} catch (KarmaException e) {
			fail();
		}

	}


}
