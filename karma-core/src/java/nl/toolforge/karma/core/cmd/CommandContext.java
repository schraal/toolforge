package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ManifestCollector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.Set;
import java.util.Collection;

/**
 * <p>The command context is the class that provides a runtime for commands to run in. The command context maintains
 * access to the current manifest and all commands that are valid. A <code>CommandContext</code> must be initialized
 * through its {@link #init} method so it can initialize all resources it requires to properly run commands. The
 * <code>init</code> method can only be run once.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CommandContext {

  private static Log logger = LogFactory.getLog(CommandContext.class);

//	private static ManifestLoader manifestLoader = null;

  private Manifest currentManifest = null;
  private LocalEnvironment env = null;
  public CommandResponseHandler responseHandler = null;

  private boolean initialized = false;

  /**
   * <p>Checks if this <code>CommandContext</code> has been initialized. A non-initialized context cannot be used and
   * methods in this class will throw a <code>KarmaException</code> with error code
   * <code>KarmaException.COMMAND_CONTEXT_NOT_INITIALIZED</code> if a non-initialized context is encountered.
   *
   * @return <code>true</code> if this command context has been initialized, false if it isn't
   */
  public boolean isInitialized() {
    return initialized;
  }

  /**
   * Initializes the context to run commands. This method can only be called once.
   *
   * @param env The users' {@link nl.toolforge.karma.core.LocalEnvironment}.
   */
  public synchronized void init(LocalEnvironment env, CommandResponseHandler handler) throws LocationException {

    if (!initialized) {

      if (handler == null) {
        throw new IllegalArgumentException("CommandResponseHandler may not be null, you lazy bitch.");
      }
      this.responseHandler = handler;
      this.env = env;

      // Try updating the location-store.
      //
//      try {
//
//        CVSLocationImpl location = new CVSLocationImpl("manifest-store");
//        location.setHost(getLocalEnvironment().getConfigurationDirectory());
//
//        Runner runner = RunnerFactory.getRunner();
//
//      } catch (LocationException l) {
//
//      }
//
//      // Try updating the manifest-store.
//      //
//      try {
//
//
//      } catch (ManifestException m) {
//
//      }


      // Read in all location data
      //
      LocationFactory.getInstance(env).load();

      // Try reloading the last manifest that was used.
      //
      ManifestCollector collector = ManifestCollector.getInstance(this.env);
      try {
        currentManifest = collector.loadFromHistory();
      } catch (ManifestException e) {
        // Fine, continue.
      }
    }
    initialized = true;
  }

  /**
   * Gets the currently active manifest.
   *
   * @return The currently active manifest, or <code>null</code> when no manifest is current.
   */
  public Manifest getCurrent() {
    return currentManifest;
  }

  /**
   * Changes the current manifest for this context.
   *
   * @param manifestName
   * @throws nl.toolforge.karma.core.manifest.ManifestException When the manifest could not be changed. See {@link nl.toolforge.karma.core.manifest.ManifestException#MANIFEST_LOAD_ERROR}.
   */
  public void changeCurrent(String manifestName) throws LocationException, ManifestException {
    currentManifest = new Manifest(manifestName);
    currentManifest.load(getLocalEnvironment());
  }


  /**
   * Gets all manifests.
   *
   * @return See <code>ManifestLoader.getAllManifests()</code>.
   */
  public Collection getAllManifests() throws ManifestException {
    return ManifestCollector.getInstance().getAllManifests();
  }

  /**
   * <p>Executes a command. Interface applications should use this method to actually execute a command. When a
   * <code>KarmaException</code> is thrown an interface applications should <b>*** NOT ***</b> quit program execution as
   * a result of this exception. It should be handled nicely.
   *
   * @param commandLine The command to execute. A full command line is passed as a parameter.
   * @throws CommandException A whole lot. Interface applications should <b>*** NOT ***</b> quit program execution as a
   *                        result of this exception. It should be handled nicely.
   */
  public void execute(String commandLine) throws CommandException {

    if (!isInitialized()) {
      throw new CommandException(KarmaException.COMMAND_CONTEXT_NOT_INITIALIZED);
    }

    Command command = CommandFactory.getInstance().getCommand(commandLine);
    execute(command);
  }

  /**
   * @param command The command to execute.
   * @throws CommandException
   */
  public void execute(Command command) throws CommandException {

    if (!isInitialized()) {
      throw new CommandException(KarmaException.COMMAND_CONTEXT_NOT_INITIALIZED);
    }
    if (command == null) {
      throw new CommandException(KarmaException.INVALID_COMMAND);
    }

    // Store a reference to this context in the command
    //
    command.setContext(this);
    command.registerCommandResponseListener(responseHandler);
    // Register the response handler with this context, so commands have a reference to it.
    //
    //todo what happens when an exception occurs in the execute wrt deregister?
    command.execute();
    command.deregisterCommandResponseListener(responseHandler);
  }

  /**
   * Checks if a manifest is active for this context.
   *
   * @return <code>true</code> if a manifest is active for the context, or <code>false</code> if no manifest is active.
   */
  public boolean isManifestLoaded() {
    return (currentManifest != null);
  }

  public LocalEnvironment getLocalEnvironment() {
    return this.env;
  }


  /**
   * <p>Some module-types (e.g. source modules) have a physical location on disk where the module can be located. This
   * method returns a valid reference to that location. When the module-root is located at
   * <code>/home/jensen/dev/modules/CORE-conversion</code>, <code>getLocalPath()</code> will return a <code>File</code>
   * handle to that directory.
   *
   * @return A <code>File</code> handle to the module directory on a local disk.
   *
   * todo consider moving it to Module.
   */
  public final File getLocalPath(Module module) throws KarmaException {

    File localPath = new File(getBase(), module.getName());
    logger.debug("getLocalPath() = " + localPath.getPath());

    return localPath;
  }

  /**
   * <p>Gets the build target directory for <code>module</code>, creating it when non existing. The current default
   * location for a build target directory is determined as follows :
   *
   * <pre>{@link LocalEnvironment#DEVELOPMENT_STORE_DIRECTORY} + File.separator + {@link #getCurrent()} + File.separator
   * + build + module.getName()</pre>
   *
   * todo consider moving it to Module.
   */
  public final File getBuildTarget(Module module) throws KarmaException {

    File localPath = new File(new File(getBase(), "build"), module.getName());

//    if (localPath.exists()) {
//      try {
//        FileUtils.deleteDirectory(new File(localPath.getParent()));
//      } catch (IOException e) {
////       todo implement this properly
//      }
//    }

    if (localPath.exists() || localPath.mkdirs()) {
      return localPath;
    }
    logger.error("Could not create build directory for module " + module.getName());
    throw new KarmaException(KarmaException.LAZY_BASTARD); // todo proper exception
  }

  /**
   * Helper to get the module base for the current manifest.
   */
  // todo what to do with the throws clause ???
  //
  private File getBase() throws KarmaException {
    return new File(getLocalEnvironment().getDevelopmentHome(), getCurrent().getName());
  }


}
