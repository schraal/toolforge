package nl.toolforge.karma.core.manifest;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class ManifestCollector {

  private static ManifestCollector instance = new ManifestCollector();
  private static Collection manifests = null;

  public static ManifestCollector getInstance() {
    return instance;
  }

  private ManifestCollector() {
    init();
  }

  public Collection getAllManifests() {
    return manifests;
  }

  public synchronized void refresh() {
    init();
  }

  private synchronized void init() {
    manifests = new HashSet();
  }
}
