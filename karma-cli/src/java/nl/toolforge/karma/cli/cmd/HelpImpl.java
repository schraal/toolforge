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

import nl.toolforge.karma.console.CommandRenderer;
import nl.toolforge.karma.core.bundle.BundleCache;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandLoadException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import org.apache.commons.lang.StringUtils;

import java.util.ResourceBundle;

/**
 * @author W.G.Helmantel
 * @version $Id$
 */
public class HelpImpl extends DefaultCommand {

  private static final ResourceBundle FRONTEND_MESSAGES = BundleCache.getInstance().getBundle(BundleCache.FRONTEND_MESSAGES_KEY);

  private CommandResponse response = new CommandResponse();

  public HelpImpl(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   *
   * @throws CommandException Occurs when a command load error occurred.
   */
  public void execute() throws CommandException {

    String commandName = getCommandLine().getOptionValue("c");

    try {
      String renderedStuff = null;
      SimpleMessage message = null;

      if (commandName != null) {
        renderedStuff = CommandRenderer.renderCommand(commandName);

        int usage = FRONTEND_MESSAGES.getString("message.COMMAND_HELP").length() + commandName.length();

        message =
            new SimpleMessage(
                "\n" + FRONTEND_MESSAGES.getString("message.COMMAND_HELP") + "\n" + StringUtils.repeat("-", usage) + "\n" + renderedStuff,
                new Object[]{commandName}
                );
      } else {
        renderedStuff = CommandRenderer.renderedCommands(CommandFactory.getInstance().getCommands());
        renderedStuff += "\n" + FRONTEND_MESSAGES.getString("message.HELP_DETAILS") + "\n";
        renderedStuff += CommandRenderer.renderCommand("help");
        message = new SimpleMessage("\n" + FRONTEND_MESSAGES.getString("message.VALID_COMMANDS") + "\n" + renderedStuff);
      }

      response.addEvent(new MessageEvent(message));

    } catch (CommandLoadException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }
  }

  /**
   * Returns the response object for this command.
   *
   * @return The response object for this command.
   */
  public CommandResponse getCommandResponse() {
    return response;
  }

}
