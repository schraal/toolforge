package nl.toolforge.karma.core;

import junit.framework.TestCase;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.cmd.CommandLoader;
import nl.toolforge.karma.core.test.BaseTest;

import java.util.Set;
import java.io.File;

/**
 *
 * @author D.A. Smedes
 */
public class TestManifestLoader extends BaseTest {

	public void testParse1() {

		ManifestLoader ml = ManifestLoader.getInstance();

		try {
			Manifest m =
				ml.load("test-manifest-1.xml", this.getClass().getClassLoader(), "test");

			assertEquals(2, m.countSourceModules());
			assertEquals(2, m.countJarModules());

		} catch (ManifestException k) {
			//k.printStackTrace();
			fail(k.getMessage());
		}
	}

//	public void testParse2() {
//
//		ManifestLoader ml = ManifestLoader.getInstance();
//
//		try {
//			Manifest m =
//				ml.load("test-manifest-1.xml", this.getClass().getClassLoader(), "/test");
//
//			fail("A ManifestException should be thrown, because the \"/\" is not allowed prefixing the relative path.");
//
//		} catch (ManifestException k) {
//			assertTrue(true);
//		}
//	}
}
