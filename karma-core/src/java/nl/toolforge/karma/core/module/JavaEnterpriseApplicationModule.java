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
package nl.toolforge.karma.core.module;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.module.template.EappModuleLayoutTemplate;
import nl.toolforge.karma.core.module.template.ModuleLayoutTemplate;

/**
 * Module type containing libraries. This release only supports the Karma Java Edition, which means that the libs
 * (jar-files) need to be stored Maven-style, as this is how they will be looked up in the module.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class JavaEnterpriseApplicationModule extends BaseModule {

  public JavaEnterpriseApplicationModule(String name, Location location) {
    this(name, location, null);
  }

  /**
   * Returns an {@link EappModuleLayoutTemplate} instance.
   *
   * @return An {@link EappModuleLayoutTemplate} instance.
   */
  public ModuleLayoutTemplate getLayoutTemplate() {
    return new EappModuleLayoutTemplate();
  }

  public JavaEnterpriseApplicationModule(String name, Location location, Version version) {
    super(name, location, version);
  }
}
