package nl.toolforge.karma.core.manifest;

import nl.toolforge.core.util.file.XMLFilenameFilter;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.location.LocationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Class that collects all manifests.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ManifestCollector {

  private static Log logger = LogFactory.getLog(ManifestCollector.class);

  private static ManifestCollector instance = null;
  private static Collection manifests = new ArrayList();

  /**
   * @return
   */
  public static ManifestCollector getInstance() {

    if (instance == null) {
      instance = new ManifestCollector();
    }
    return instance;
  }

  public ManifestCollector() {
    init();
  }

  public Collection getAllManifests() {
    File manifestStore = LocalEnvironment.getManifestStore();

    Object[] mList = manifestStore.list(new XMLFilenameFilter());

    if (mList == null) {
      return new ArrayList();
    }
    manifests = Arrays.asList(mList);
    if (manifests.size() == 0) {
      return new ArrayList();
    }

    return manifests;
  }

  public synchronized void refresh() {
    init();
  }

  /**
   * Retrieves the last used manifest or <code>null</code> when none was found.
   *
   * @return The last used manifest.
   * @throws ManifestException When the manifest referred to by {@link Manifest.HISTORY_KEY} could not be loaded.
   */
  public Manifest loadFromHistory() throws LocationException, ManifestException {

    String contextManifest = LocalEnvironment.getContextManifestPreference();

    String manifestId = Preferences.userRoot().get(contextManifest, null);
    if (manifestId != null) {

      ManifestFactory manifestFactory = ManifestFactory.getInstance();

      Manifest manifest = null;
      try {
        manifest = manifestFactory.createManifest(manifestId);
      } catch (ManifestException m) {
        if (m.getErrorCode().equals(ManifestException.MANIFEST_FILE_NOT_FOUND)) {
          Preferences.userRoot().remove(LocalEnvironment.getContextManifestPreference());
          try {
            Preferences.userRoot().flush();
          } catch (BackingStoreException e) {
            logger.warn("Could not write user preferences due to java.util.prefs.BackingStoreException.");
          }
        }
        // Rethrow, the removal from userPrefs has been performed
        //
        throw m;
      }

      return manifest;
    }

    return null;
  }

  private synchronized void init() {
    manifests = new HashSet();
  }
}
