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

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.module.JavaEnterpriseApplicationModule;
import nl.toolforge.karma.core.module.JavaWebApplicationModule;
import nl.toolforge.karma.core.module.UntypedModule;

/**
 * <p>Factory class to create modules based on a {@link ModuleDigester}.</p>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ModuleFactory {

  private WorkingContext workingContext = null;

  public ModuleFactory(WorkingContext workingContext) {
    this.workingContext = workingContext;
  }

  public Module create(ModuleDigester digester, Module.Type moduleType) throws LocationException {

    if (digester == null) {
      throw new IllegalArgumentException("Descriptor cannot be null.");
    }
    if (moduleType == null) {
      throw new IllegalArgumentException("Module type cannot be null.");
    }

    Location location = workingContext.getLocationLoader().get(digester.getLocation());

    Version version = null;
    if (digester.getVersion() != null) {
      version = new Version(digester.getVersion());
    }

    if (moduleType.equals(Module.JAVA_SOURCE_MODULE)) {
      return new SourceModule(digester.getName(), location, version);
    } else if (moduleType.equals(Module.JAVA_ENTERPRISE_APPLICATION)) {
      return new JavaEnterpriseApplicationModule(digester.getName(), location, version);
    } else if (moduleType.equals(Module.JAVA_WEB_APPLICATION)) {
      return new JavaWebApplicationModule(digester.getName(), location, version);
    } else if (moduleType.equals(Module.LIBRARY_MODULE)) {
      return new LibModule(digester.getName(), location, version);
    } else if (moduleType.equals(Module.OTHER_MODULE)) {
      return new OtherModule(digester.getName(), location, version);
    } else if (moduleType.equals(Module.UNKNOWN)) {
      return new UntypedModule(digester.getName(), location, version);
    }

    throw new KarmaRuntimeException("Type mismatch. Could not create module.");
  }
}