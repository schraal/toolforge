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

import nl.toolforge.karma.core.manifest.Module;

import java.io.File;
import java.io.IOException;

/**
 * <p>Layout for a lib-module. A lib-module (implemented by the
 * {@link nl.toolforge.karma.core.manifest.LibModule} class) has the following
 * directory-structure:
 *
 * <ul>
 * </ul>
 *
 * @author W.H. Schraal
 * @version $Id$
 */
public final class LibModuleLayoutTemplate extends BaseModuleLayoutTemplate {

  // todo constructor met xml file naam waar de layout gevonden kan worden.

  public FileTemplate[] getFileElements() {
    return new FileTemplate[] {
      new FileTemplate(new File("/templates/cvsignore.template"), new File(".cvsignore")),
//      new FileTemplate(new File("/templates/module-descriptor.xml_template"), new File(Module.MODULE_DESCRIPTOR)),
    };
  }

  public String[] getDirectoryElements() {
    return new String[] {};
  }

}
