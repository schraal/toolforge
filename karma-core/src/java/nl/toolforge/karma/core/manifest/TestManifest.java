package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.test.BaseTest;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.ManifestFactory;
import nl.toolforge.karma.core.manifest.DevelopmentManifest;
import nl.toolforge.karma.core.manifest.ManifestLoader;
import nl.toolforge.karma.core.manifest.TestManifestLoader;
import nl.toolforge.karma.core.location.LocationException;
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
public class TestManifest extends TestManifestLoader {

  /**
   *
   */
  public void testConstructor() {

    ManifestLoader loader = new ManifestLoader(getWorkingContext());

    try {
      ManifestStructure structure = loader.load("test-manifest-1");

      DevelopmentManifest manifest = new DevelopmentManifest(getWorkingContext(), structure);

      assertEquals(5, manifest.getAllModules().size());

    } catch (ManifestException e) {
      fail(e.getMessage());
    } catch (LocationException e) {
      fail(e.getMessage());
    }
  }

  /**
   *
   */
  public void testDuplicateModules() {

    ManifestLoader loader = new ManifestLoader(getWorkingContext());

    try {
      ManifestStructure structure = loader.load("test-manifest-2");
      new DevelopmentManifest(getWorkingContext(), structure);

      fail("Duplicate module in manifest should have been detected.");

    } catch (ManifestException e) {
      assertEquals(ManifestException.DUPLICATE_MODULE, e.getErrorCode());
    } catch (LocationException e) {
      fail(e.getMessage());
    }
  }

  /**
   *
   */
  public void testDuplicateIncludes() {

    ManifestLoader loader = new ManifestLoader(getWorkingContext());

    try {
      ManifestStructure structure = loader.load("test-manifest-3");
      new DevelopmentManifest(getWorkingContext(), structure);

      fail("Manifest include recursion should have been detected.");

    } catch (ManifestException e) {
      assertEquals(ManifestException.MANIFEST_NAME_RECURSION, e.getErrorCode());
    } catch (LocationException e) {
      fail(e.getMessage());
    }
  }
}
