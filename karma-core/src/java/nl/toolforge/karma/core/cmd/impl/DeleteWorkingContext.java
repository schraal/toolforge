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

import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.event.ErrorEvent;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.cmd.event.ExceptionEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * Removes the working context configuration, but leaves all projects intact. Removing the current working context
 * is prohibited (with the command at least).
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class DeleteWorkingContext extends DefaultCommand {

  private static Log logger = LogFactory.getLog(DeleteWorkingContext.class);

  protected CommandResponse response = new CommandResponse();

  /**
   *
   * @param descriptor The command descriptor for this command.
   */
  public DeleteWorkingContext(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   *
   *
   * @throws nl.toolforge.karma.core.cmd.CommandException
   */
  public void execute() throws CommandException {

    String workingContextName = getCommandLine().getOptionValue("w");

    WorkingContext workingContext = new WorkingContext(workingContextName);

    // The current cannot be removed.
    //
    if (workingContextName.equals(getWorkingContext().getName())) {
      response.addEvent(new ErrorEvent(this, WorkingContext.CANNOT_REMOVE_ACTIVE_WORKING_CONTEXT));
      return;
    }

    try {
      workingContext.remove();
    } catch (IOException e) {
      response.addEvent(new ExceptionEvent(this, e));
    }

    response.addEvent(new MessageEvent(this, new SimpleMessage("Working context `" + workingContextName  + "` removed.")));
    response.addEvent(new MessageEvent(this, new SimpleMessage("Project data contained in `" + workingContext.getProjectBaseDirectory().getPath() + "`.")));
  }

  public CommandResponse getCommandResponse() {
    return response;
  }
}
