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
package nl.toolforge.karma.core.vc.cvs.threads;

import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.CVSRunner;
import nl.toolforge.karma.core.vc.threads.RunnerThread;

/**
 * Checks if a module has a PatchLine in the version control repository. If the module that is passed to this instance
 * has a PatchLine, it will be updated as such. A call to {@link #getResult()} will always return <code>null</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class PatchLineThread extends RunnerThread {

  public PatchLineThread(Module module) {
    super(module);
  }

  /**
   * Runs a <code>cvs log</code> on the CVS repo.
   */
  public void run() {

    startRunning();

    CVSRunner runner = null;
    try {
      runner = (CVSRunner) RunnerFactory.getRunner(getModule().getLocation());

      if (runner.hasPatchLine(getModule())) {
        getModule().markPatchLine(true);
      }

    } catch (VersionControlException e) {
      exception = e;
    } finally {
      stopRunning();
    }
  }
}
