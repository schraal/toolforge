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
package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;

/**
 * <p>Factory class to create modules based on a {@link ModuleDescriptor}.</p>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ModuleFactory {

  private WorkingContext workingContext = null;

  public ModuleFactory(WorkingContext workingContext) {
    this.workingContext = workingContext;
  }

  public Module create(ModuleDescriptor descriptor) throws LocationException {

    if (descriptor == null) {
      throw new IllegalArgumentException("Descriptor cannot be null.");
    }

    Module module = null;

    Location location = workingContext.getLocationLoader().get(descriptor.getLocation());

    Version version = null;
    if (descriptor.getVersion() != null) {
      version = new Version(descriptor.getVersion());
    }

    //
    // Create a SourceModule instance.
    //
    if (version != null) {

      //
      // <module name="" location="" version="">
      //
      switch (descriptor.getType()) {
        case ModuleDescriptor.SOURCE_MODULE :
          module = new SourceModule(descriptor.getName(), location, version);
          break;
        case ModuleDescriptor.MAVEN_MODULE :
          module = new MavenModule(descriptor.getName(), location, version);
          break;
      }
    } else {
      //
      // <module name="" location="">
      //
      switch (descriptor.getType()) {
        case ModuleDescriptor.SOURCE_MODULE :
          module = new SourceModule(descriptor.getName(), location);
          break;
        case ModuleDescriptor.MAVEN_MODULE :
          module = new MavenModule(descriptor.getName(), location);
          break;
      }
    }

    return module;
  }

}