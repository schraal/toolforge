package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.test.BaseTest;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestManifestLoader extends BaseTest {

  /**
   *
   */
  public void testLoad() {

    ManifestLoader loader = new ManifestLoader(getWorkingContext());

    ManifestStructure structure = null;

    try {
      structure = loader.load("test-manifest-1");
    } catch (ManifestException e) {
      fail(e.getMessage());
    }
    assertTrue(true);
  }

}
