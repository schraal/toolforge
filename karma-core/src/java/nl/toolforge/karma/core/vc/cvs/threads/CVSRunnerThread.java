package nl.toolforge.karma.core.vc.cvs.threads;

import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.CVSException;
import nl.toolforge.karma.core.vc.cvs.CVSModuleStatus;
import nl.toolforge.karma.core.vc.cvs.CVSRunner;
import nl.toolforge.karma.core.vc.threads.RunnerThread;
import org.netbeans.lib.cvsclient.command.log.LogInformation;

import java.io.File;

/**
 * A <code>CVSRunnerThread</code> runs a <code>cvs log</code> command on a modules' <code>.module.info</code> file and
 * fills a {@link nl.toolforge.karma.core.vc.ModuleStatus} instance with the full status overview of a module.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class CVSRunnerThread extends RunnerThread {

  public CVSRunnerThread(Module module) {
    setModule(module);
  }

  /**
   * Runs a <code>cvs log</code> on the CVS repo.
   */
  public void run() {

    try {

      startRunning();

      CVSRunner runner = (CVSRunner) RunnerFactory.getRunner(getModule().getLocation());

      LogInformation logInfo = null;
      try {
        logInfo = runner.log(getModule());

        moduleStatus = new CVSModuleStatus(getModule(), logInfo);

        // The log method would have thrown a CVSException ..
        //
        moduleStatus.setExistsInRepository(true);

      } catch (CVSException e) {
        if (e.getErrorCode().equals(VersionControlException.MODULE_NOT_IN_REPOSITORY)) {
          moduleStatus.setExistsInRepository(false);
        } else {
          // Must rethrow ...
          //
          throw e;
        }
      }

    } catch (VersionControlException v) {
      v.printStackTrace();
    } finally {
      stopRunning();
    }
  }

}
