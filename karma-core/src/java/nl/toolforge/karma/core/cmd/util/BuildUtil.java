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
package nl.toolforge.karma.core.cmd.util;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Validates a modules' dependencies by checking if the actual artifacts already exists on a local disk.
 *
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class BuildUtil {

  // todo MOVE TO AbstractBuildCommand after ample consideration ...

//  private static final String DEFAULT_BUILD_DIR = "build";

  private Manifest currentManifest = null;

  public BuildUtil(Manifest currentManifest) {
    this.currentManifest = currentManifest;
  }

  /**
   * Cleans a modules' dependencies, by (recursively) traversing all modules that depend on <code>module</code> and
   * cleaning their <code>build</code>-directories.
   *
   * @param module The (root)-module for which dependencies should be cleaned.
   * //@param modules All modules that have a dependency on <code>module</code>.
   */
  public void cleanDependencies(Module module) throws ManifestException {

    // todo proper exception handling --> CommandException ????

    // Get all modules that depend on this module.
    //
    Collection interDeps = currentManifest.getModuleInterdependencies(module);

    for (Iterator i = interDeps.iterator(); i.hasNext();) {

      Module dep = (Module) i.next();

      if (currentManifest.getInterdependencies().containsKey(dep.getName())) {
        cleanDependencies(dep);
      }
      // No interdependencies found for the dependency, so we remove its build-directory.
      //
      try {
        File buildDir = new File(currentManifest.getBuildBaseDirectory(), dep.getName());

        FileUtils.deleteDirectory(buildDir);

      } catch (IOException e) {
        throw new KarmaRuntimeException(e.getMessage());
      }
    }
  }

}
