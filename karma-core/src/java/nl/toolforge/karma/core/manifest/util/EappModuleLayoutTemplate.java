package nl.toolforge.karma.core.manifest.util;

import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.manifest.Module;

import java.util.Map;
import java.util.Hashtable;

/**
 * <p>Layout for an eapp-module. A eapp-module has the following directory-structure:
 *
 * <ul>
 * <li/><code>module.info</code>
 * <li/><code>resources/</code>
 * <li/><code>META-INF/application.xml/</code>
 * </ul>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class EappModuleLayoutTemplate implements ModuleLayoutTemplate {

  // todo constructor met xml file naam waar de layout gevonden kan worden.

  public String[] getFileElements() {
    return new String[] {
      Module.MODULE_INFO,
      "META-INF/application.xml"};
  }

  public String[] getDirectoryElements() {
    return new String[] {
      "resources"};
  }
}
