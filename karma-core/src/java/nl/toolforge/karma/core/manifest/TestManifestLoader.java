package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.test.BaseTest;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.ManifestLoader;
import nl.toolforge.core.util.file.MyFileUtils;

import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

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
