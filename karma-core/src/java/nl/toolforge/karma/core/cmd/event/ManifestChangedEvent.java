package nl.toolforge.karma.core.cmd.event;

import nl.toolforge.karma.core.manifest.Manifest;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ManifestChangedEvent {

  private Manifest manifest = null;

  public ManifestChangedEvent(Manifest manifest) {
    this.manifest = manifest;
  }

  public Manifest getManifest() {
    return manifest;
  }
}
