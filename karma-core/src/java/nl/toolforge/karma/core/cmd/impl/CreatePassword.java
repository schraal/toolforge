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

import net.sf.sillyexceptions.OutOfTheBlueException;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.PasswordScrambler;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Authenticator;
import nl.toolforge.karma.core.vc.AuthenticatorKey;
import nl.toolforge.karma.core.vc.Authenticators;
import nl.toolforge.karma.core.vc.VersionControlSystem;

/**
 * Creates or changes authentication information for a location.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class CreatePassword extends DefaultCommand {

  protected CommandResponse response = new CommandResponse();

  /**
   *
   * @param descriptor The command descriptor for this command.
   */
  public CreatePassword(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   * @throws CommandException
   */
  public void execute() throws CommandException {

    String locationAlias = getCommandLine().getOptionValue("l");
    String username = getCommandLine().getOptionValue("u");
    String password = getCommandLine().getOptionValue("p");

    VersionControlSystem location = null;
    try {
      location = (VersionControlSystem) getWorkingContext().getLocationLoader().get(locationAlias);
    } catch (LocationException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    } catch (ClassCastException e) {
      response.addEvent(new MessageEvent(this, new SimpleMessage("Location type incorrect. Cannot proceed.")));
      return;
    }

    boolean changed = false;
    Authenticator authenticator = null;
    try {
      authenticator = Authenticators.getAuthenticator(new AuthenticatorKey(getWorkingContext().getName(), location.getId()));
      changed = true;
    } catch (AuthenticationException e) {

      if (username == null) {
        throw new CommandException(AuthenticationException.MISSING_USERNAME);
      }
      response.addEvent(new MessageEvent(this, new SimpleMessage("Location `" + locationAlias + "` does not exist. It will be created.")));
    }

    if (username == null) {
      // Only password change.
      //
      try {
        Authenticators.changePassword(new AuthenticatorKey(getWorkingContext().getName(), location.getId()), password);
      } catch (AuthenticationException e) {
        throw new OutOfTheBlueException("Impossible, we have just checked the location. It should be there.");
      }

    } else {

      if (changed) {
        try {
          Authenticators.deleteAuthenticator(authenticator);
        } catch (AuthenticationException e) {
          throw new OutOfTheBlueException("Impossible, we have just selected the correct authenticator. It should be there.");
        }
      }

      authenticator = new Authenticator(locationAlias);
      authenticator.setWorkingContext(getWorkingContext().getName());
      authenticator.setUsername(username);
      authenticator.setPassword(PasswordScrambler.scramble(password));

      try {
        Authenticators.addAuthenticator(authenticator);
      } catch (AuthenticationException e) {
        throw new CommandException(e, e.getErrorCode(), e.getMessageArguments());
      }
    }

    if (changed) {
      response.addEvent(new MessageEvent(this, new SimpleMessage("Authenticator changed for location '" + locationAlias + "'")));
    } else {
      response.addEvent(new MessageEvent(this, new SimpleMessage("Authenticator created for location '" + locationAlias + "'")));
    }
  }

  public CommandResponse getCommandResponse() {
    return response;
  }
}
