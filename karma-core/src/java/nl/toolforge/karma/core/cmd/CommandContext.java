package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationLoader;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestCollector;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.ManifestFactory;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.core.util.listener.ListenerManager;
import nl.toolforge.core.util.listener.ListenerManagerException;
import nl.toolforge.core.util.listener.ListenerManager;
import nl.toolforge.core.util.listener.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Hashtable;
import java.util.List;
import java.util.Iterator;

/**
 * <p>The command context is the class that provides a runtime for commands to run in. The command context maintains
 * access to the current manifest and all commands that are valid. A <code>CommandContext</code> must be initialized
 * through its {@link #init} method so it can initialize all resources it requires to properly run commands. The
 * <code>init</code> method can only be run once.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CommandContext implements ChangeListener {

  private static Log logger = LogFactory.getLog(CommandContext.class);

  private Manifest currentManifest = null;
  public CommandResponseHandler responseHandler = null;

  private static ListenerManager manager = null;

  /**
   * Initializes the context to run commands.
   *
   */
  public synchronized void init(CommandResponseHandler handler)
      throws ManifestException, LocationException {

    if (handler == null) {
      throw new IllegalArgumentException("CommandResponseHandler may not be null, you lazy bitch.");
    }
    this.responseHandler = handler;

    // Update the manifest-store.
    //

    Module manifestStore = new SourceModule("manifests", LocalEnvironment.getManifestStoreLocation());

    if (LocalEnvironment.getWorkingContext().exists()) {
      try {
        Runner runner = RunnerFactory.getRunner(manifestStore.getLocation(), LocalEnvironment.getWorkingContext());
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
    Module locationStore = new SourceModule("locations", LocalEnvironment.getLocationStoreLocation());

    if (LocalEnvironment.getWorkingContext().exists()) {
      try {
        Runner runner = RunnerFactory.getRunner(locationStore.getLocation(), LocalEnvironment.getWorkingContext());
        runner.checkout(locationStore);
      } catch (VersionControlException e) {
        // todo some sort of notification would be nice ...
        //
        logger.warn(e.getErrorMessage());
        // Nothing serious ...
        //

        handler.commandResponseChanged(new CommandResponseEvent(new ErrorMessage(KarmaException.LOCATION_STORE_UPDATE_FAILED)));
      }
    } else {
      throw new KarmaRuntimeException("Pietje puk exception");
    }

    // Read in all location data
    //
    LocationLoader.getInstance().load();

    // Try reloading the last manifest that was used.
    //
    ManifestCollector collector = ManifestCollector.getInstance();
    currentManifest = collector.loadFromHistory();

    setFileModificationTimes();

    // Register the command context with the listener to allow automaic updates of the manifest.
    //

    manager = ListenerManager.getInstance();
    try {
      manager.register(this);
    } catch (ListenerManagerException e) {
      e.printStackTrace();
    }

    manager.start();
  }

//  private static Map modificationMap = new Hashtable();

  private long lastmodified = 0;

  private synchronized void setFileModificationTimes() {

    // todo verder uitwerken voor included manifests
    //

//    File manifestStore = LocalEnvironment.getManifestStore();
//
//    List includes = currentManifest.getIncludedManifests();
//    for (Iterator i = includes.iterator(); i.hasNext();) {
//
//      Manifest m = (Manifest) i.next();
//
//      Long lastModified = new Long(new File(manifestStore, m.getName() + ".xml").lastModified());
//      modificationMap.put(m.getName(), lastModified);
//    }

    lastmodified = new File(LocalEnvironment.getManifestStore(), currentManifest.getName() + ".xml").lastModified();
  }

  /**
   * Implementation of the {@link nl.toolforge.core.util.listener.ChangeListener} interface. This method reloads the
   * current manifest to allow changes to be reflected without having to restart.
   */
  public synchronized void process() {

    try {

      File f = new File(LocalEnvironment.getManifestStore(), currentManifest.getName() + ".xml");

      if (!f.exists()) {
        throw new ManifestException(ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[] {currentManifest.getName()});
      }

      if (f.lastModified() > lastmodified) {

        setFileModificationTimes();
//        lastmodified = f.lastModified(); // Reset

        currentManifest.load();

        String message = "Manifest " + getCurrentManifest().getName() + " has changed on disk. Reloaded automaitcally.";
        logger.info(message);

        responseHandler.commandResponseChanged(new CommandResponseEvent(new SuccessMessage(message)));

        return;
      }

    } catch (ManifestException m) {

      manager.suspendListener();

      logger.error(new ErrorMessage(m.getErrorCode()).getMessageText());
      throw new KarmaRuntimeException(m.getErrorCode(), m.getMessageArguments());

      // todo in karma-core-1.1 this should be improved. Right now, the probability of this process failing is remote.
    }
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
  public void changeCurrentManifest(String manifestName) throws ManifestException {

    ManifestFactory manifestFactory = ManifestFactory.getInstance();
    Manifest newManifest = manifestFactory.createManifest(manifestName);

    // If we are here, loading the new manifest was succesfull.
    //
    currentManifest = newManifest;

    setFileModificationTimes();
  }


  /**
   * Gets all manifests.
   *
   * @return See <code>ManifestLoader.getAllManifests()</code>.
   */
  public Collection getAllManifests() {
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
    command.cleanUp();
  }

  /**
   * Checks if a manifest is active for this context.
   *
   * @return <code>true</code> if a manifest is active for the context, or <code>false</code> if no manifest is active.
   */
  public boolean isManifestLoaded() {
    return (currentManifest != null);
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
  public final File getLocalPath(Module module) {

    File localPath = new File(getBase(), module.getName());
    logger.debug("getLocalPath() = " + localPath.getPath());

    return localPath;
  }

  /**
   * Helper to get the module base for the current manifest.
   */
  // todo what to do with the throws clause ???
  //
  private File getBase() {
    return new File(LocalEnvironment.getDevelopmentHome(), getCurrentManifest().getName());
  }


}
