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
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.location.LocationLoader;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.vc.Authenticator;
import nl.toolforge.karma.core.vc.VersionControlSystem;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;

/**
 * Creates a password for a location.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class CreatePassword extends DefaultCommand {

  private static Log logger = LogFactory.getLog(CreatePassword.class);

  protected CommandResponse response = new CommandResponse();

  /**
   *
   * @param descriptor The command descriptor for this command.
   */
  public CreatePassword(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    String locationAlias = getCommandLine().getOptionValue("l");
    String password = getCommandLine().getOptionValue("p");

    VersionControlSystem location = null;
    try {
      location = (VersionControlSystem) getWorkingContext().getLocationLoader().get(locationAlias);
    } catch (LocationException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    } catch (ClassCastException c) {
      throw new CommandException(CommandException.INVALID_LOCATION_TYPE, new Object[]{});
    }

    Authenticator authenticator = new Authenticator();
    try {
      authenticator.changePassword(location, password);
    } catch (AuthenticationException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }
  }

  public CommandResponse getCommandResponse() {
    return response;
  }
}
