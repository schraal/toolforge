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
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.threads.PatchLineThread;
import nl.toolforge.karma.core.vc.threads.ParallelRunner;

import java.io.File;

/**
 * A <code>ReleaseManifest</code> is created when the Release Manager collects all stable versions of modules.
 * Effectively, the latest promoted version of all modules in.
 *
 *
 * @author W.H. Schraal, D.A. Smedes
 * @version $Id$
 */
public final class ReleaseManifest extends AbstractManifest {

  public ReleaseManifest(String name) {
    super(name);
  }

  public String getType() {
    return Manifest.RELEASE_MANIFEST;
  }

  /**
   * <p>Adds a module to a <code>ReleaseManifest</code>. This method is called by
   * <a href="http://jakarta.apache.org/commons/digester">Digester</a> during the
   * {@link #load()}-process.
   *
   * <p>This method also checks if the module is available locally. If so, the module will be matched with the module
   * on disk to check if they are equal. This is to ensure that a changed manifest-definition is reflected on disk. If
   * the manifest shows another module (which is in fact determined by its location), the version on disk will be
   * removed.
   *
   * @param descriptor The object representing a &lt;module&gt;-elemeent from a manifest XML file.
   * @throws nl.toolforge.karma.core.location.LocationException When an invalid location was passed with <code>descriptor</code>. This occurs when no
   *   location-id has been identified in the <code>locations.xml</code>-file in the manifest-store.
   * @throws ManifestException
   */
  public synchronized final void addModule(ModuleDescriptor descriptor) throws LocationException, ManifestException {

    // todo duidelijk beschrijven hoe het state mechanisme wordt aangestuurd door dit ding.
    //

    if (descriptor.getVersion() == null) {
      throw new ManifestException(ManifestException.MODULE_WITHOUT_VERSION, new Object[]{descriptor.getName()});
    }

    Module module = moduleFactory.create(descriptor);

    try {
      File manifestDirectory = new File(LocalEnvironment.getDevelopmentHome(), getName());
      module.setBaseDir(new File(manifestDirectory, module.getName()));
    } catch(Exception e) {
      // Basically, if we can't do this, we have nothing ... really a RuntimeException
      //
      throw new KarmaRuntimeException("Could not set base directory for module " + module.getName());
    }

    module.markDevelopmentLine(false);

    if (!isLocal(module)) {
      // Module is static by definition when it is not locally available.
      //
      setState(module, Module.STATIC);
    }

    if (getModulesForManifest().containsKey(module.getName())) {
      throw new ManifestException(ManifestException.DUPLICATE_MODULE, new Object[]{module.getName(), getName()});
    }
    getModulesForManifest().put(module.getName(), module);

    // As a last check, determine if an 'old' local version is available.
    //
    removeLocal(module);
  }

}
