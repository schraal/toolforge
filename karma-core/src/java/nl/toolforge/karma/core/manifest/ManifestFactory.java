package nl.toolforge.karma.core.manifest;



/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ManifestFactory {

  private static ManifestFactory instance = null;

  private ManifestFactory() {
  }

  public static ManifestFactory getInstance() {

    if (instance == null) {
      instance = new ManifestFactory();
    }
    return instance;
  }

  public Manifest createManifest(String manifestName) throws ManifestException {

    // todo this one should 'read' the manifest and apply its type.

    Manifest manifest = new DevelopmentManifest(manifestName);
    manifest.load();

    return manifest;
  }


}
