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

import nl.toolforge.karma.core.boot.ManifestStore;
import nl.toolforge.karma.core.boot.LocationStore;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.event.ErrorEvent;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.VersionControlException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Updates administrative data for a working context. The manifest-store and the location-store are updated.
 *
 * @author W.H. Schraal
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class UpdateAdmin extends DefaultCommand {

  private static final Log logger = LogFactory.getLog(UpdateAdmin.class);

  private CommandResponse commandResponse = new CommandResponse();

  public UpdateAdmin(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    try {
      ManifestStore mStore = getContext().getWorkingContext().getConfiguration().getManifestStore();

      if (!mStore.getLocation().isAvailable()) {
        commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Manifest store location unreachable!")));
      } else {
        commandResponse.addEvent(new MessageEvent(this, new SimpleMessage(("Updating manifests ..."))));
        mStore.update();
      }

      // todo locations are not yet reloaded automatically.

      LocationStore lStore = getContext().getWorkingContext().getConfiguration().getLocationStore();

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

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }
}
