package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.LocalEnvironment;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ManifestFactory {

  private static ManifestFactory instance = null;
  private LocalEnvironment env = null;

  private ManifestFactory(LocalEnvironment env) {
    this.env = env;
  }

  public static ManifestFactory getInstance(LocalEnvironment env) {

    if (instance == null) {
      instance = new ManifestFactory(env);
    }
    return instance;
  }

  public Manifest createManifest(String manifestName) throws ManifestException {

    // todo this one should 'read' the manifest and apply its type.

    Manifest manifest = new DevelopmentManifest(manifestName);
    manifest.load(env);

    return manifest;
  }


}
