package nl.toolforge.karma.core.boot;

import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.VersionControlException;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public interface Store {
  /**
   * Updates the local checkout of the manifest store.
   *
   * @throws AuthenticationException When the location for this <code>Store</code> cannot be authenticated.
   * @throws VersionControlException When the update failed.
   */
  void update() throws AuthenticationException, VersionControlException, WorkingContextException;

  /**
   * Assigns this <code>Store</code> a <code>Location</code>. The <code>Location</code> contains a module with
   * administrative files for Karma to run.
   *
   * @param location The location for this store.
   */
  void setLocation(Location location);

  /**
   * Sets the module name for this <code>ManifestStore</code>. This name is assumed to be the name of the module in a version
   * control repository. The moduleName should be including any offset-directory in the version control system.
   *
   * @param moduleName The module name for the manifest store.
   */
  void setModuleName(String moduleName);

  Module getModule();

  String getModuleName();

  /**
   * Checks if the configuration of the store is ok.
   *
   * @return An ErrorCode instance with the first encountered error, or <code>null</code> if none happened.
   */
  ErrorCode checkConfiguration();
}
