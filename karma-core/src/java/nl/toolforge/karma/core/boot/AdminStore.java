package nl.toolforge.karma.core.boot;

import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvsimpl.Utils;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class AdminStore implements Store {

  public static final ErrorCode STORE_CONNECTION_FAILURE = new ErrorCode("WCC-00002");
  public static final ErrorCode AUTHENTICATOR_NOT_FOUND = new ErrorCode("WCC-00003");
  public static final ErrorCode STORE_MODULE_NO_IN_REPOSITORY = new ErrorCode("WCC-00004");

  private WorkingContext workingContext = null;
  private Location location = null;

  private String moduleName = null;

  protected Module module = null;

  public AdminStore(WorkingContext workingContext) {

    if (workingContext == null) {
      throw new IllegalArgumentException("Working context cannot be null.");
    }

    this.workingContext = workingContext;
  }

  public AdminStore(WorkingContext workingContext, String moduleName, Location location) {
    this(workingContext);
    this.moduleName = moduleName;
    this.location = location;
  }

  protected final WorkingContext getWorkingContext() {
    return workingContext;
  }

  public final void setLocation(Location location) {

    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null.");
    }

    this.location = location;
  }

  public final Location getLocation() {
    return location;
  }

  public final void update() throws AuthenticationException, VersionControlException {

    if (moduleName == null || "".equals(moduleName)) {
      throw new KarmaRuntimeException("Module name for manifest store has not been set (correctly).");
    }

    Runner runner = RunnerFactory.getRunner(location);

    // todo this is a good place to check if the existing directory is from the same cvs location.
    //
    if (getModule().getBaseDir().exists()) {
      runner.update(getModule());
    } else {
      runner.checkout(getModule());
    }
  }

  public final void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public final String getModuleName() {
    return moduleName;
  }

  public final boolean exists() {
      return Utils.existsInRepository(getModule());
  }

  //
  // inherit-doc
  //
  public ErrorCode checkConfiguration() {

    if (getLocation() == null) {
      return STORE_CONNECTION_FAILURE;
    }

    try {
      getLocation().connect();
    } catch (AuthenticationException e) {
      return STORE_CONNECTION_FAILURE;
    } catch (LocationException e) {
      return STORE_CONNECTION_FAILURE;
    }

    if (!Utils.existsInRepository(getModule())) {
      return STORE_MODULE_NO_IN_REPOSITORY;
    }

    return null;
  }
}
