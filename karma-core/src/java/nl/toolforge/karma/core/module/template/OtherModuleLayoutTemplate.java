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
package nl.toolforge.karma.core.module.template;

import java.io.File;

/**
 * <p>Layout for an other-module. An other-module (implemented by the
 * {@link nl.toolforge.karma.core.module.OtherModule} class) has no fixed
 * directory-structure. You are free to choose the structure of your likings.
 * </p>
 * <p>
 * However, Karma will not do anything with the file in these directories. When
 * packaging, Karma will copy all files as is into an archive.
 *
 * @author W.H. Schraal
 * @version $Id$
 */
public final class OtherModuleLayoutTemplate extends BaseModuleLayoutTemplate {

  // todo constructor met xml file naam waar de layout gevonden kan worden.

  public FileTemplate[] getFileElements() {
    return new FileTemplate[] {
      new FileTemplate(new File("/templates/cvsignore.template"), new File(".cvsignore")),
    };
  }

  public String[] getDirectoryElements() {
    return new String[] {
        "contents"
      };
  }

}
