package nl.toolforge.karma.core;

import nl.toolforge.karma.core.test.BaseTest;
import nl.toolforge.core.util.file.MyFileUtils;

import java.util.Properties;
import java.io.File;
import java.io.IOException;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestManifestLoader extends BaseTest {

	public void testParse1() {

		LocalEnvironment env = LocalEnvironment.getInstance(getProperties());

		ManifestLoader ml = ManifestLoader.getInstance(env);

		try {
			Manifest m =
					ml.load("test/test-manifest-1.xml", this.getClass().getClassLoader(), "");

			assertEquals(2, m.countSourceModules());
			assertEquals(2, m.countJarModules());

		} catch (KarmaException k) {
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
