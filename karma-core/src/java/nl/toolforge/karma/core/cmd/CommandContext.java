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
package nl.toolforge.karma.core.cmd;

import nl.toolforge.core.util.listener.ChangeListener;
import nl.toolforge.core.util.listener.ListenerManager;
import nl.toolforge.core.util.listener.ListenerManagerException;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.ManifestFactory;
import nl.toolforge.karma.core.manifest.ManifestLoader;
import nl.toolforge.karma.core.manifest.ManifestStructure;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.AdminHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

  private static final Log logger = LogFactory.getLog(CommandContext.class);

  private Manifest currentManifest;
  private CommandResponseHandler responseHandler;
  private Map modificationMap = new HashMap();

  private static ListenerManager manager;

  private WorkingContext workingContext = null;

  /**
   * Constructs a <code>CommandContext</code>, in which commands are run.
   */
  public CommandContext(WorkingContext workingContext) {
    this.workingContext = workingContext;
  }

  public WorkingContext getWorkingContext() {
    return workingContext;
  }

  /**
   * Initializes the context to run commands. This method should only be called once on a <code>CommandContext</code>.
   *
   * @param handler The {@link CommandResponseHandler} object that will be passed to all commands run through this
   *   context.
   */
  public synchronized void init(CommandResponseHandler handler)
      throws ManifestException, LocationException {

    if (handler == null) {
      throw new IllegalArgumentException("CommandResponseHandler may not be null, you lazy bitch.");
    }
    responseHandler = handler;

    // Update the manifest-store.
    //

    Location location = workingContext.getManifestStoreLocation();
    Module manifestModule = new SourceModule("manifests", location);

    if (location.ping()) {

      manifestModule.setBaseDir(workingContext.getManifestStore());

      handler.commandResponseChanged(new CommandResponseEvent(new SuccessMessage("Updating manifests from CVS location `" + manifestModule.getLocation().toString() + "`")));

      if (workingContext.getManifestStore().exists()) {

        // Check if the locally existing manifest module has the same location (cvsroot e.g.) as the
        // requested update.
        //
        AdminHandler adminHandler = new AdminHandler();
        if (!adminHandler.isEqualLocation(manifestModule)) {
          throw new LocationException(LocationException.LOCATION_MISMATCH, new Object[]{manifestModule.getName()});
        }

        try {
          Runner runner = RunnerFactory.getRunner(manifestModule.getLocation());
          runner.checkout(manifestModule);
        } catch (VersionControlException e) {
          // todo some sort of notification would be nice ...
          //
          logger.warn(e.getErrorMessage());
          // Nothing serious ...
          //
          handler.commandResponseChanged(new CommandResponseEvent(new ErrorMessage(KarmaException.MANIFEST_STORE_UPDATE_FAILED)));
          handler.commandResponseChanged(new CommandResponseEvent(new ErrorMessage(e.getErrorCode())));
        }
      } else {
        throw new KarmaRuntimeException("Pietje puk exception");
      }

    } else {
      handler.commandResponseChanged(new CommandResponseEvent(new ErrorMessage("Manifest store location unreachable!")));
    }


    // Update the location-store.
    //
    location = workingContext.getLocationStoreLocation();
    if (location.ping()) {

      Module locationModule = new SourceModule("locations", location);
      locationModule.setBaseDir(workingContext.getLocationStore());

      handler.commandResponseChanged(new CommandResponseEvent(new SuccessMessage("Updating locations from CVS location `" + locationModule.getLocation().toString() + "`")));

      if (workingContext.getLocationStore().exists()) {

        AdminHandler adminHandler = new AdminHandler();
        if (!adminHandler.isEqualLocation(locationModule)) {
          throw new LocationException(LocationException.LOCATION_MISMATCH, new Object[]{locationModule.getName()});
        }

        try {
          Runner runner = RunnerFactory.getRunner(locationModule.getLocation());
          runner.checkout(locationModule);
        } catch (VersionControlException e) {
          // todo some sort of notification would be nice ...
          //
          logger.warn(e.getErrorMessage());
          // Nothing serious ...
          //

          handler.commandResponseChanged(new CommandResponseEvent(new ErrorMessage(KarmaException.LOCATION_STORE_UPDATE_FAILED)));
          handler.commandResponseChanged(new CommandResponseEvent(new ErrorMessage(e.getErrorCode())));
        }
      } else {
        throw new KarmaRuntimeException("Pietje puk exception");
      }

    } else {
      handler.commandResponseChanged(new CommandResponseEvent(new ErrorMessage("Location store location unreachable!")));
    }

    // Try reloading the last manifest that was used.
    //
    currentManifest = workingContext.getManifestCollector().loadFromHistory();

    // Register the command context with the listener to allow automaic updates of the manifest.
    //
    if (currentManifest != null) {
      register();
    }
  }


  private synchronized void setFileModificationTimes() {

    Manifest manifest = currentManifest;

    Long lastMod = new Long(new File(workingContext.getManifestStore(), manifest.getName() + ".xml").lastModified());
    modificationMap.put(manifest, lastMod);

    try {
      Collection includes = manifest.getIncludes();
      for (Iterator i = includes.iterator(); i.hasNext();) {

        manifest = (Manifest) i.next();

        lastMod = new Long(new File(workingContext.getManifestStore(), manifest.getName() + ".xml").lastModified());
        modificationMap.put(manifest, lastMod);
      }
    } catch (Exception e) {
      modificationMap.clear();
    }
  }

  /**
   * Implementation of the {@link ChangeListener} interface. This method reloads the
   * current manifest to allow changes to be reflected without having to restart Karma.
   */
  public synchronized void process() {

    boolean reload = false;

    try {

      Collection manifests = new ArrayList();
      manifests.add(currentManifest);
      manifests.addAll(currentManifest.getIncludes());

      for (Iterator i = manifests.iterator(); i.hasNext();) {

        Manifest m = (Manifest) i.next();
        long lastMod = ((Long) modificationMap.get(m)).longValue();

        File f = new File(workingContext.getManifestStore(), m.getName() + ".xml");
        if (!f.exists()) {
          currentManifest = null;
          throw new ManifestException(ManifestException.MANIFEST_FILE_NOT_FOUND, new Object[] {m.getName()});
        }

        if (f.lastModified() > lastMod) {
          reload = true;
          break;
        }
      }

      if (reload) {

        // One of the manifests in the tree has been changed on disk, reload the full structure.
        //
        ManifestStructure reloadedStructure =
            getWorkingContext().getManifestLoader().load(currentManifest.getName());
        currentManifest = new ManifestFactory().create(workingContext, reloadedStructure);

        setFileModificationTimes();

        String message = "\nManifest " + getCurrentManifest().getName() + " has changed on disk. Reloaded automatically.\n";
        logger.info(message);

        responseHandler.commandResponseChanged(new CommandResponseEvent(new SuccessMessage(message)));
        responseHandler.commandResponseFinished(null);

        return;
      }

    } catch (ManifestException m) {

      // Catches the ManifestException in case the manifest file has disappeared as well.
      //
      managed = false;
      manager.suspendListener(this);

      logger.error(new ErrorMessage(m.getErrorCode()).getMessageText());

      // todo in karma-core-1.1 this should be improved. Right now, the probability of this process failing is remote.
      throw new KarmaRuntimeException(m.getErrorCode(), m.getMessageArguments());
    } catch (Exception e) {

      managed = false;
      manager.suspendListener(this);

      logger.error("Error while processing manifests during automatic reload; " + e.getMessage());
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
   * @throws ManifestException When the manifest could not be changed. See {@link ManifestException#MANIFEST_LOAD_ERROR}.
   */
  public void changeCurrentManifest(String manifestName) throws ManifestException, LocationException {

    ManifestFactory manifestFactory = new ManifestFactory();
    ManifestLoader loader = new ManifestLoader(workingContext);
    Manifest newManifest = manifestFactory.create(workingContext, loader.load(manifestName));

    // If we are here, loading the new manifest was succesfull.
    //
    currentManifest = newManifest;

    register();
  }

  private boolean managed = false;

  private synchronized void register() {

    setFileModificationTimes();

    if (!managed) {

      manager = ListenerManager.getInstance();
      try {
        manager.register(this);
      } catch (ListenerManagerException e) {
        throw new KarmaRuntimeException(e.getMessage());
      }

      manager.start();

      managed = true;
    }
  }

  /**
   * Gets all manifests.
   *
   * @return See <code>ManifestLoader.getAllManifests()</code>.
   */
  public Collection getAllManifests() {
    return workingContext.getManifestCollector().getAllManifests();
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

    Command command = CommandFactory.getInstance().getCommand(commandLine);
    execute(command);
  }

  /**
   * Exceutes <code>command</code>.
   *
   * @param command The command to execute.
   * @throws CommandException
   */
  public void execute(Command command) throws CommandException {

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
    return currentManifest != null;
  }

  /**
   * <p>Some module-types (e.g. source modules) have a physical location on disk where the module can be located. This
   * method returns a valid reference to that location. When the module-root is located at
   * <code>/home/jensen/dev/modules/CORE-conversion</code>, <code>getLocalPath()</code> will return a <code>File</code>
   * handle to that directory.
   *
   * @param module The module for which the local path should be retrieved.
   *
   * @return A <code>File</code> handle to the module directory on a local disk.
   *
   * todo consider moving it to Module.
   */
  public File getLocalPath(Module module) {

    File localPath = new File(getBase(), module.getName());
    logger.debug("getLocalPath() = " + localPath.getPath());

    return localPath;
  }

  /**
   * Helper to get the module base for the current manifest.
   */
  private File getBase() {
    return new File(workingContext.getDevelopmentHome(), getCurrentManifest().getName());
  }

}
