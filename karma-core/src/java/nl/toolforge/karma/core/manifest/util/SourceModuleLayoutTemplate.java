package nl.toolforge.karma.core.manifest.util;

import nl.toolforge.karma.core.manifest.SourceModule;

import java.util.Map;
import java.util.Hashtable;

/**
 * <p>Layout for a source-module. A source-module (implemented by the <code>SourceModule</code> class) has the following
 * directory-structure:
 *
 * <ul>
 * <li/><code>module.info</code>
 * <li/><code>dependencies.xml</code>
 * <li/><code>src/java</code>
 * <li/><code>resources/</code>
 * </ul>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class SourceModuleLayoutTemplate implements ModuleLayoutTemplate {

  // todo constructor met xml file naam waar de layout gevonden kan worden.

  public String[] getFileElements() {
    return new String[] {SourceModule.MODULE_INFO, "dependencies.xml"};
  }

  public String[] getDirectoryElements() {
    return new String[] {"src/java", "resources"};
  }

}
