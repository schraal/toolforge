/*
Karma CLI - Command Line Interface for the Karma application
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
package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.impl.PromoteCommand;

public class PromoteModuleImpl extends PromoteCommand {

  public PromoteModuleImpl(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    CommandMessage message = new SuccessMessage(getFrontendMessages().getString("message.PROMOTE_MODULE_STARTED"), new Object[]{getCommandLine().getOptionValue("m")});
    commandResponse.addMessage(message);


    super.execute();

    message =
        new SuccessMessage(
            getFrontendMessages().getString("message.MODULE_PROMOTED"),
            new Object[]{getCommandLine().getOptionValue("m"), getNewVersion()}
        );
    commandResponse.addMessage(message);
  }

}
