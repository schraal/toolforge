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
import nl.toolforge.karma.core.module.template.ModuleLayoutTemplate;

import java.io.IOException;

/**
 * This type of module is used when a Module instance is required for remote modules. At this point, the type is not yet
 * known.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class UntypedModule extends BaseModule {

  public UntypedModule(String name, Location location) {
    this(name, location, null);
  }

  /**
   * Creates a <code>JavaEnterpriseApplicationModule</code> using a {@link nl.toolforge.karma.core.module.template.EappModuleLayoutTemplate} as the layout
   * template.
   *
   * @throws java.io.IOException When the module (layout) could not be created.
   */
  public void create() throws IOException {
    throw new KarmaRuntimeException("Untyped modules cannot be created.");
  }

  /**
   * Throws a <code>KarmaRuntimeException</code>, because these modules cannot be created.
   */
  public ModuleLayoutTemplate getLayoutTemplate() {
    throw new KarmaRuntimeException("Untyped modules have no layout template.");
  }

  public UntypedModule(String name, Location location, Version version) {
    super(name, location, version);
  }
}
