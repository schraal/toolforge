/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core.manifest.util;

import java.io.File;

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

  public FileTemplate[] getFileElements() {
    return new FileTemplate[] {
      new FileTemplate(new File("/templates/cvsignore.template"), new File(".cvsignore")),
      new FileTemplate(new File("/templates/module.info.template"), new File(Module.MODULE_INFO)),
      new FileTemplate(new File("/templates/dependencies.xml_template"), new File("dependencies.xml")),
      new FileTemplate(new File("/templates/web.xml_template"), new File("WEB-INF","web.xml"))
    };
  }

  public String[] getDirectoryElements() {
    return new String[] {
      "src/java",                              // Java sources
      "test/resources",                        // Test meuk
      "resources",
      "web",                                   // Web-resources such as html, images and the like
      "WEB-INF/lib",
      "WEB-INF/resources"};                    // Runtime libraries
  }
}
