package nl.toolforge.karma.core.manifest;

import nl.toolforge.core.util.file.XMLFilenameFilter;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.location.LocationException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class ManifestCollector {

  private static Log logger = LogFactory.getLog(ManifestCollector.class);

  private static ManifestCollector instance = null;
  private static Collection manifests = new ArrayList();

  private LocalEnvironment env = null;

  public static ManifestCollector getInstance(LocalEnvironment env) {

    if (instance == null) {
      instance = new ManifestCollector(env);
    }
    return instance;
  }

  /**
   * Same as {@link #getInstance(nl.toolforge.karma.core.LocalEnvironment)}, but this method can only be used when
   * {@link ManifestCollector#init()} has been called.
   *
   * @return
   */
  public static ManifestCollector getInstance() {

    if (instance == null) {
      throw new KarmaRuntimeException(
          "ManifestCollector has not been initialized. Use 'getInstance(LocalEnvironment)'.");
    }
    return instance;
  }

  public ManifestCollector(LocalEnvironment env) {
    this.env = env;
    init();
  }

  public Collection getAllManifests() {

    try {
      File manifestStore = getLocalEnvironment().getManifestStore();

      return Arrays.asList(manifestStore.list(new XMLFilenameFilter()));

    } catch (KarmaException e) {
      return manifests;
    }
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

    String contextManifest =
        LocalEnvironment.LAST_USED_MANIFEST_PREFERENCE + "." + LocalEnvironment.getWorkingContextAsString();

    String manifestId = Preferences.userRoot().get(contextManifest, null);
    if (manifestId != null) {

      ManifestFactory manifestFactory = ManifestFactory.getInstance(getLocalEnvironment());

      Manifest manifest = null;
      try {
        manifest = manifestFactory.createManifest(manifestId);
      } catch (ManifestException m) {
        if (m.getErrorCode().equals(ManifestException.MANIFEST_FILE_NOT_FOUND)) {
          Preferences.userRoot().remove(LocalEnvironment.LAST_USED_MANIFEST_PREFERENCE);
          try {
            Preferences.userRoot().flush();
          } catch (BackingStoreException e) {
            logger.warn("Could not write user preferences due to java.util.prefs.BackingStoreException.");
          }
        }
        // Rethrow, the removal from userPrefs has been performed
        //
        throw m;
//        }
      }

      return manifest;
    }

    return null;
  }

  private LocalEnvironment getLocalEnvironment() {
    return env;
  }

  private synchronized void init() {
    manifests = new HashSet();
  }
}
