package nl.toolforge.karma.core.manifest;

/**
 * Simple descriptor for a manifest.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ManifestDescriptor {

  private String name = null;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
