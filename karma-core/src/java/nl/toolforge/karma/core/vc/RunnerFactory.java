package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import nl.toolforge.karma.core.vc.cvs.CVSRunner;
import nl.toolforge.karma.core.vc.subversion.SubversionLocationImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory producing {@link Runner} instances.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class RunnerFactory {

  private static Log logger = LogFactory.getLog(RunnerFactory.class);

  private RunnerFactory() {}

  /**
   * A <code>Runner</code> might be required for a command to execute something on a version control system. A module
   * can determine which implementation of a runner it requires through the
   * {@link nl.toolforge.karma.core.manifest.Module#getLocation} method.
   *
   * @param location The location for which a runner is required.
   * @return A version control system specific <code>Runner</code>.
   */
  public static Runner getRunner(Location location) throws VersionControlException {

    if (location instanceof CVSLocationImpl) {
      logger.debug("Getting new CVSRunner instance.");

      CVSRunner runner = new CVSRunner(location);

      return runner;
    }

    if (location instanceof SubversionLocationImpl) {
      logger.debug("Getting new CVSRunner instance.");
      return null;
    }
    throw new KarmaRuntimeException("Location instance invalid.");
  }

}
