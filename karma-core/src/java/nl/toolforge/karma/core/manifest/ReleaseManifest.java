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

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.vc.cvsimpl.threads.PatchLineThread;
import nl.toolforge.karma.core.vc.threads.ParallelRunner;
import nl.toolforge.karma.core.module.Module;

/**
 * A <code>ReleaseManifest</code> is created when the Release Manager collects all stable versions of modules.
 * Effectively, the latest promoted version of all modules in.
 *
 * @author W.H. Schraal
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ReleaseManifest extends AbstractManifest {

  public ReleaseManifest(WorkingContext context, ManifestStructure structure) throws LocationException {
    super(context, structure);

    // For a release manifest, we need to know if patch lines are available.
    //
    checkForPatchLines();
  }

  /**
   * Checks (in parallel) if modules have a <code>PatchLine</code> associated.
   */
  private void checkForPatchLines() {
    ParallelRunner runner = new ParallelRunner(this, PatchLineThread.class);

    long delay = 100;
    runner.execute(delay);
  }

  public String getType() {
    return Manifest.RELEASE_MANIFEST;
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

    module.markDevelopmentLine(false);

    if (!isLocal(module)) {
      // Module is static by definition when it is not locally available.
      //
      setState(module, Module.STATIC);
    }
  }

}
