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

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.module.template.LibModuleLayoutTemplate;
import nl.toolforge.karma.core.module.template.ModuleLayoutTemplate;

import java.util.Set;

/**
 * Module type containing libraries. This release only supports the Karma Java Edition, which means that the libs
 * (jar-files) need to be stored Maven-style, as this is how they will be looked up in the module.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class LibModule extends BaseModule {

  public LibModule(String name, Location location) {
    this(name, location, null);
  }

  /**
   * Returns an {@link LibModuleLayoutTemplate} instance.
   *
   * @return An {@link LibModuleLayoutTemplate} instance.
   */
  public ModuleLayoutTemplate getLayoutTemplate() {
    return new LibModuleLayoutTemplate();
  }

  public LibModule(String name, Location location, Version version) {
    super(name, location, version);
  }

  public Set getLibraries() {
    throw new KarmaRuntimeException("to be implemented ...");
  }

}
