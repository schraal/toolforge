package nl.toolforge.karma.core.manifest.util;

import nl.toolforge.karma.core.manifest.Module;

/**
 * <p>Layout for a webapp-module. A web-module has the following directory-structure:
 *
 * <ul>
 * <li/><code>module.info</code>
 * <li/><code>dependencies.xml</code>
 * <li/><code>src/java</code>
 * <li/><code>resources/</code>
 * <li/><code>WEB-INF/web.xml/</code>
 * </ul>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class WebappModuleLayoutTemplate implements ModuleLayoutTemplate {

  // todo constructor met xml file naam waar de layout gevonden kan worden.

  public String[] getFileElements() {
    return new String[] {
      Module.MODULE_INFO,
      "dependencies.xml",
      "WEB-INF/web.xml"};
  }

  public String[] getDirectoryElements() {
    return new String[] {
      "src/java",
      "resources",
      "web"}; // for web-resources; appears as the web-root in the war file.
  }
}
