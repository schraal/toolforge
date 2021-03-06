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
package nl.toolforge.karma.core.vc.cvsimpl.threads;

import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.ModuleStatus;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvsimpl.CVSException;
import nl.toolforge.karma.core.vc.cvsimpl.CVSModuleStatus;
import nl.toolforge.karma.core.vc.cvsimpl.CVSRunner;
import nl.toolforge.karma.core.vc.threads.RunnerThread;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A <code>CVSLogThread</code> runs a <code>cvs log</code> command on a modules' <code>.module.info</code> file and
 * fills a {@link nl.toolforge.karma.core.vc.ModuleStatus} instance with the full status overview of a module.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class CVSLogThread extends RunnerThread {

  private static Log logger = LogFactory.getLog(CVSLogThread.class);

  public CVSLogThread(Module module) {
    super(module);
  }

  /**
   * Runs a <code>cvs log</code> on the CVS repo.
   */
  public void run() {

    ModuleStatus moduleStatus = null;

    startRunning();

    try {
      moduleStatus = new CVSModuleStatus(getModule());

      CVSRunner runner = (CVSRunner) RunnerFactory.getRunner(getModule().getLocation());

      moduleStatus.setLogInformation(runner.log(getModule()));

      // The log method would have thrown a CVSException ..
      //
      moduleStatus.setExistsInRepository(true);

    } catch (VersionControlException e) {
      logger.error(e.getMessage(), e);
      ErrorCode code = e.getErrorCode();
      if (code.equals(LocationException.CONNECTION_EXCEPTION) ||
          code.equals(CVSException.INVALID_CVSROOT)) {
        ((CVSModuleStatus) moduleStatus).setConnnectionFailure();
      }
      if (code.equals(VersionControlException.MODULE_NOT_IN_REPOSITORY)) {
        moduleStatus.setExistsInRepository(false);
      }
      if (code.equals(CVSException.AUTHENTICATION_ERROR)) {
        ((CVSModuleStatus) moduleStatus).setAuthenticationFailure();
      }
      if (code.equals(CVSException.INTERNAL_ERROR)) {
        ((CVSModuleStatus) moduleStatus).setInternalError();
      }
    } catch (AuthenticationException e) {
      // todo duplicate with the above. mechanism to be refactored.
      //
      logger.error(e.getMessage(), e);
      ((CVSModuleStatus) moduleStatus).setAuthenticationFailure();
    }

    result = moduleStatus;

    stopRunning();
  }
}
