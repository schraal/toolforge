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
import nl.toolforge.karma.core.module.template.ModuleLayoutTemplate;
import nl.toolforge.karma.core.module.template.SourceModuleLayoutTemplate;

/**
 * <p>A <code>SourceModule</code> represents a module for which the developer wants to have the sources available to
 * on the local harddisk.
 *
 * @author D.A. Smedes
 * @version $Id$
 * @see nl.toolforge.karma.core.module.Module
 */
public class SourceModule extends BaseModule {

  /**
   * Constructs a <code>SourceModule</code> with a <code>name</code> and <code>location</code>.
   *
   * @param name Mandatory parameter; name of the module.
   * @param location Mandatory parameter; location of the module.
   */
  public SourceModule(String name, Location location) {
    this(name, location, null);
  }

  public Type getInstanceType() {
    return Module.JAVA_SOURCE_MODULE;
  }

  public ModuleLayoutTemplate getLayoutTemplate() {
    return new SourceModuleLayoutTemplate();
  }

  /**
   * Constructs a <code>SourceModule</code> with a <code>name</code>, <code>location</code> and <code>version</code>.
   */
  public SourceModule(String name, Location location, Version version) {
    super(name, location, version);
  }

}
