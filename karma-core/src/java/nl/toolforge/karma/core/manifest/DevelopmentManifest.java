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
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.location.LocationException;

import java.io.File;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public final class DevelopmentManifest extends AbstractManifest {

  public String getType() {
    return Manifest.DEVELOPMENT_MANIFEST;
  }

  public DevelopmentManifest(WorkingContext workingContext, String name) throws ManifestException, LocationException {
    super(workingContext, name);
  }

  public DevelopmentManifest(WorkingContext context, ManifestStructure structure) throws LocationException {
    super(context, structure);
  }

  /**
   * <p>Applies the current working context to a module in this release manifest.
   *
   * <p>This method also checks if the module is available locally. If so, the module will be matched with the module
   * on disk to check if they are equal. This is to ensure that a changed manifest-definition is reflected on disk. If
   * the manifest shows another module (which is in fact determined by its location), the version on disk will be
   * removed.
   */
  protected void applyWorkingContext(WorkingContext context, Module module) {

    try {
      if (module instanceof SourceModule) {
        File manifestDirectory = new File(context.getDevelopmentHome(), getName());
        module.setBaseDir(new File(manifestDirectory, module.getName()));
      }
    } catch(Exception e) {
      // Basically, if we can't do this, we have nothing ... really a RuntimeException
      //
      throw new KarmaRuntimeException("Could not set base directory for module " + module.getName());
    }

    if (module.hasVersion()) {
      setState(module, Module.STATIC);
    } else {
      if (isLocal(module)) {
        setState(module, getState(module));
      } else {
        setState(module, Module.DYNAMIC);
      }
    }

    removeLocal((SourceModule) module);
  }

}
