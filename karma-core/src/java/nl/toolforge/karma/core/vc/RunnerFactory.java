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
package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.cvsimpl.CVSRepository;
import nl.toolforge.karma.core.vc.cvsimpl.CVSRunner;
import nl.toolforge.karma.core.vc.svnimpl.SubversionLocationImpl;
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

    if (location instanceof CVSRepository) {
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
