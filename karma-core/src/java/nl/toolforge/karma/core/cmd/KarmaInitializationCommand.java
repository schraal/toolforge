package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.bundle.BundleCache;
import nl.toolforge.karma.core.cmd.event.CommandResponseListener;
import nl.toolforge.karma.core.cmd.event.ErrorEvent;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.module.LocationModule;
import nl.toolforge.karma.core.module.ManifestModule;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvsimpl.AdminHandler;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * <p>Command implementation to initialize a command context. This is implemented as a command to allow for better and
 * more consistent event handling and messaging.
 *
 * <p>Although not impossible, this command should not be used in a user interface layer.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
//public final class KarmaInitializationCommand implements Command {
public final class KarmaInitializationCommand implements Command {

  private static final Log logger = LogFactory.getLog(KarmaInitializationCommand.class);

  private CommandResponse commandResponse = new CommandResponse();

  private CommandContext commandContext = null;
  private boolean updateStores = false;

  private CommandResponseListener responseListener = null;

  /**
   * Constructs a <code>KarmaInitializationCommand</code>.
   */
  KarmaInitializationCommand(boolean updateStores) {
    this.updateStores = updateStores;
  }

  /**
   * Initializes a command context.
   *
   * @throws CommandException
   */
  public void execute() throws CommandException {

    try {

      if (updateStores) {

        // The location is read from property files.
        //
        Location location = commandContext.getWorkingContext().getManifestStoreLocation();
        ManifestModule manifestModule = new ManifestModule(commandContext.getWorkingContext().getManifestStoreModule(), location);

        // Relative the location, which is handy for the future when more than one ManifestModule can be checked
        // out.
        manifestModule.setBaseDir(new File(commandContext.getWorkingContext().getAdminDir(), location.getId()));
        manifestModule.setCheckoutDir(new File(commandContext.getWorkingContext().getAdminDir(), location.getId()));

        if (location.isAvailable()) {

          commandResponse.addEvent(new MessageEvent(this, new SimpleMessage(("Updating manifests ..."))));

          // Check if the locally existing manifest module has the same location (cvsroot e.g.) as the
          // requested update.
          //
          AdminHandler adminHandler = new AdminHandler(manifestModule);
          if (!adminHandler.isEqualLocation()) {
            throw new LocationException(LocationException.LOCATION_MISMATCH, new Object[]{manifestModule.getName()});
          }

          try {
            Runner runner = RunnerFactory.getRunner(manifestModule.getLocation());
            runner.checkout(manifestModule);
          } catch (VersionControlException e) {
            // todo some sort of notification would be nice ...
            //
            logger.warn(e.getMessage());
            // Nothing serious ...
            //
            commandResponse.addEvent(new ErrorEvent(this, e.getErrorCode(), e.getMessageArguments()));
          } catch (AuthenticationException e) {
            // todo moet anders.
            //
            commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Authentication failed.")));
          }
        } else {
          commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Manifest store location unreachable!")));
        }

        // The location is read from property files.
        //
        location = commandContext.getWorkingContext().getLocationStoreLocation();
        LocationModule locationModule = new LocationModule(commandContext.getWorkingContext().getLocationStoreModule(), location);

        // Relative the location, which is handy for the future when more than one ManifestModule can be checked
        // out.
        locationModule.setBaseDir(new File(commandContext.getWorkingContext().getAdminDir(), location.getId()));
        locationModule.setCheckoutDir(new File(commandContext.getWorkingContext().getAdminDir(), location.getId()));

        if (location.isAvailable()) {

          commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Updating locations ...")));

          // Check if the locally existing manifest module has the same location (cvsroot e.g.) as the
          // requested update.
          //
          AdminHandler adminHandler = new AdminHandler(locationModule);
          if (!adminHandler.isEqualLocation()) {
            throw new LocationException(LocationException.LOCATION_MISMATCH, new Object[]{locationModule.getName()});
          }

          try {
            Runner runner = RunnerFactory.getRunner(locationModule.getLocation());
            runner.checkout(locationModule);
          } catch (VersionControlException e) {
            // todo some sort of notification would be nice ...
            //
            logger.warn(e.getMessage());
            // Nothing serious ...
            //
            commandResponse.addEvent(new ErrorEvent(this, e.getErrorCode(), e.getMessageArguments()));
          } catch (AuthenticationException e) {
            // todo moet anders.
            //
            commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Authentication failed.")));
          }
        } else {
          commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Location store location unreachable!")));
        }
      }

      commandResponse.addEvent(new MessageEvent(this, new SimpleMessage(getFrontendMessages().getString("message.LOADING_MANIFEST_FROM_HISTORY"))));

      // Try reloading the last manifest that was used.
      //
      Manifest currentManifest = commandContext.getWorkingContext().getManifestCollector().loadFromHistory();

      SimpleMessage message =
          new SimpleMessage(getFrontendMessages().getString("message.MANIFEST_ACTIVATED"), new Object[]{currentManifest});
      commandResponse.addEvent(new MessageEvent(this, message));

      // Register the command context with the listener to allow automaic updates of the manifest.
      //
      if (currentManifest != null) {
        commandContext.changeCurrentManifest(currentManifest);
        commandContext.register();
      }

      try {
        Preferences.userRoot().put(WorkingContext.WORKING_CONTEXT_PREFERENCE, commandContext.getWorkingContext().getName());
        Preferences.userRoot().flush();
      } catch (BackingStoreException e) {
        // Too bad ...
      }
    } catch (ManifestException e) {
      throw new CommandException(e, e.getErrorCode(), e.getMessageArguments());
    } catch (LocationException e) {
      throw new CommandException(e, e.getErrorCode(), e.getMessageArguments());
    }
  }

  /**
   * Returns 'init' as the name of this command.
   *
   * @return 'init'
   */
  public String getName() {
    return "init";
  }

  /**
   * Returns 'init' as the alias of this command.
   *
   * @return 'init'
   */
  public String getAlias() {
    return "init";
  }

  /**
   * Returns the description for this command.
   *
   * @return The description for this command.
   */
  public String getDescription() {
    return "This command initializes a command context.";
  }

  /**
   * Returns the help text for this command.
   *
   * @return Same as {@link #getDescription}.
   * @see    {@link #getDescription}
   */
  public String getHelp() {
    return getDescription();
  }

  /**
   * Empty implementation.
   */
  public void cleanUp() { }

  public void setContext(CommandContext context) {
    this.commandContext = context;
  }

  /**
   * Empty implementation.
   *
   * @param commandLine Unused.
   */
  public void setCommandLine(CommandLine commandLine) {}

  /**
   * Returns <code>null</code>
   *
   * @return <code>null</code>
   */
  public CommandLine getCommandLine() {
    return null;
  }

  public final void registerCommandResponseListener(CommandResponseListener responseListener) {
    this.responseListener = responseListener;
    getCommandResponse().addCommandResponseListener(responseListener);
  }

  public final void deregisterCommandResponseListener(CommandResponseListener responseListener) {
    getCommandResponse().removeCommandReponseListener(responseListener);
  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }

  private ResourceBundle getFrontendMessages() {
    return BundleCache.getInstance().getBundle(BundleCache.FRONTEND_MESSAGES_KEY);
  }
}
