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

import nl.toolforge.karma.core.boot.LocationStore;
import nl.toolforge.karma.core.boot.ManifestStore;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.bundle.BundleCache;
import nl.toolforge.karma.core.cmd.event.CommandResponseListener;
import nl.toolforge.karma.core.cmd.event.ErrorEvent;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.VersionControlException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

        try {
          
          ManifestStore mStore = commandContext.getWorkingContext().getConfiguration().getManifestStore();

          if (!mStore.getLocation().isAvailable()) {
            commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Manifest store location unreachable!")));
          } else {
            commandResponse.addEvent(new MessageEvent(this, new SimpleMessage(("Updating manifests ..."))));
            mStore.update();
          }

          LocationStore lStore = commandContext.getWorkingContext().getConfiguration().getLocationStore();

          if (!lStore.getLocation().isAvailable()) {
            commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Location store location unreachable!")));
          } else {
            commandResponse.addEvent(new MessageEvent(this, new SimpleMessage(("Updating locations ..."))));
            lStore.update();
          }

        } catch (VersionControlException e) {
          logger.warn(new ErrorEvent(this, e.getErrorCode(), e.getMessageArguments()));
          commandResponse.addEvent(new ErrorEvent(this, e.getErrorCode(), e.getMessageArguments()));
        } catch (AuthenticationException e) {
          logger.warn(new ErrorEvent(this, e.getErrorCode(), e.getMessageArguments()));
          commandResponse.addEvent(new ErrorEvent(this, e.getErrorCode(), e.getMessageArguments()));
        }
      }

      commandResponse.addEvent(new MessageEvent(this, new SimpleMessage(getFrontendMessages().getString("message.LOADING_MANIFEST_FROM_HISTORY"))));

      // Try reloading the last manifest that was used.
      //
      Manifest currentManifest = commandContext.getWorkingContext().getManifestCollector().loadManifestFromHistory();
      if (currentManifest != null) {
        SimpleMessage message =
            new SimpleMessage(getFrontendMessages().getString("message.MANIFEST_ACTIVATED"), new Object[]{currentManifest});
        commandResponse.addEvent(new MessageEvent(this, message));

        // Register the command context with the listener to allow automaic updates of the manifest.
        //
        commandContext.changeCurrentManifest(currentManifest);
        commandContext.register();
      } else {
        commandResponse.addEvent(new MessageEvent(this, new SimpleMessage(getFrontendMessages().getString("message.NO_MANIFEST_IN_HISTORY"))));
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
