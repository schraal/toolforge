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
package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * <p>This command activates a manifest, which is a general requirement for most other commands. The newly activated
 * manifest is stored for the Karma session in the {@link nl.toolforge.karma.core.cmd.CommandContext} that is associated
 * with the Karma session.
 *
 * @author W.H. Schraal
 * @author D.A. Smedes
 * @version $Id$
 */
public class SelectManifest extends DefaultCommand {

  private static Log logger = LogFactory.getLog(SelectManifest.class);

  private CommandResponse commandResponse = new CommandResponse();
  private Manifest selectedManifest = null;

  public SelectManifest(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   * Activates a manifest.
   *	 */
  public void execute() throws CommandException {

    // Select a manifest and store it in the command context
    //
    try {
      getContext().changeCurrentManifest(getCommandLine().getOptionValue("m"));
      selectedManifest = getContext().getCurrentManifest();
    } catch (ManifestException me) {
      throw new CommandException(me.getErrorCode(), me.getMessageArguments());
    } catch (LocationException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    // Store this manifest as the last used manifest.
    //
    String contextManifest = getWorkingContext().getContextManifestPreference();

    Preferences.userRoot().put(contextManifest, getContext().getCurrentManifest().getName());
    try {
      Preferences.userRoot().flush();
    } catch (BackingStoreException e) {
      logger.warn("Could not write user preferences due to java.template.prefs.BackingStoreException.");
    }
  }

  /**
   * Gets the commands' response object.
   *
   * @return The commands' response object.
   */
  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

  protected Manifest getSelectedManifest() {
    return selectedManifest;
  }
}
