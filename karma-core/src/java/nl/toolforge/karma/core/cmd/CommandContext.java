package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestCollector;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.manifest.ManifestFactory;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
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

  private Manifest currentManifest = null;
  private LocalEnvironment env = null;
  public CommandResponseHandler responseHandler = null;

  /**
   * Initializes the context to run commands.
   *
   * @param env The users' {@link nl.toolforge.karma.core.LocalEnvironment}.
   */
  public synchronized void init(LocalEnvironment env, CommandResponseHandler handler)
      throws KarmaException, ManifestException, LocationException {

    if (handler == null) {
      throw new IllegalArgumentException("CommandResponseHandler may not be null, you lazy bitch.");
    }
    this.responseHandler = handler;
    this.env = env;

    // Update the manifest-store.
    //
    File karmaDirectory = env.getManifestStore().getParentFile();

    Module manifestStore = new SourceModule("manifests", env.getManifestStoreLocation());

    if (karmaDirectory.exists()) {
      try {
        Runner runner = RunnerFactory.getRunner(manifestStore.getLocation(), karmaDirectory);
        runner.checkout(manifestStore);
      } catch (VersionControlException e) {
        // todo some sort of notification would be nice ...
        //
        logger.warn(e.getErrorMessage());
        // Nothing serious ...
        //
        handler.commandResponseChanged(new CommandResponseEvent(new ErrorMessage(KarmaException.MANIFEST_STORE_UPDATE_FAILED)));
      }
    } else {
      throw new KarmaRuntimeException("Pietje puk exception");
    }

    // Update the location-store.
    //
    Module locationStore = new SourceModule("locations", env.getLocationStoreLocation());

    if (karmaDirectory.exists()) {
      try {
        Runner runner = RunnerFactory.getRunner(locationStore.getLocation(), karmaDirectory);
        runner.checkout(locationStore);
      } catch (VersionControlException e) {
        // todo some sort of notification would be nice ...
        //
        logger.warn(e.getErrorMessage());
        // Nothing serious ...
        //

        handler.commandResponseChanged(new CommandResponseEvent(new ErrorMessage(KarmaException.MANIFEST_STORE_UPDATE_FAILED)));
      }
    } else {
      throw new KarmaRuntimeException("Pietje puk exception");
    }

    // Read in all location data
    //
    LocationFactory.getInstance(env).load();

    // Try reloading the last manifest that was used.
    //
    ManifestCollector collector = ManifestCollector.getInstance(this.env);
    currentManifest = collector.loadFromHistory();
  }

  /**
   * Gets the currently active manifest.
   *
   * @return The currently active manifest, or <code>null</code> when no manifest is current.
   */
  public Manifest getCurrentManifest() {
    return currentManifest;
  }

  /**
   * Changes the current manifest for this context.
   *
   * @param manifestName
   * @throws nl.toolforge.karma.core.manifest.ManifestException When the manifest could not be changed. See {@link nl.toolforge.karma.core.manifest.ManifestException#MANIFEST_LOAD_ERROR}.
   */
  public void changeCurrentManifest(String manifestName) throws LocationException, ManifestException {

    ManifestFactory manifestFactory = ManifestFactory.getInstance(getLocalEnvironment());

    Manifest newManifest = manifestFactory.createManifest(manifestName);

//
//    Manifest newManifest = new Manifest(manifestName);
//    newManifest.load(getLocalEnvironment());

    // If we are here, loading the new manifest was succesfull.
    //
    currentManifest = newManifest;
  }


  /**
   * Gets all manifests.
   *
   * @return See <code>ManifestLoader.getAllManifests()</code>.
   */
  public Collection getAllManifests() {
    return ManifestCollector.getInstance(getLocalEnvironment()).getAllManifests();
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
  public synchronized void execute(String commandLine) throws CommandException {

    Command command = CommandFactory.getInstance().getCommand(commandLine);
    execute(command);
  }

  /**
   * @param command The command to execute.
   * @throws CommandException
   */
  public synchronized void execute(Command command) throws CommandException {

    if (command == null) {
      throw new IllegalArgumentException("Invalid command; command cannot be null.");
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
   * Helper to get the module base for the current manifest.
   */
  // todo what to do with the throws clause ???
  //
  private File getBase() throws KarmaException {
    return new File(getLocalEnvironment().getDevelopmentHome(), getCurrentManifest().getName());
  }


}
