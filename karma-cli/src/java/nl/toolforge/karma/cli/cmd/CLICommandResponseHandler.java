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

import nl.toolforge.karma.core.cmd.CommandResponseHandler;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.cli.ConsoleWriter;

/**
 * This class is responsible for handling CommandResponses in an interactive way.
 * Each time a CommandResponse changes, the changes are logged through the writer.
 *
 * @author W.H. Schraal
 */
public class CLICommandResponseHandler implements CommandResponseHandler {

  private ConsoleWriter writer = null;

  public CLICommandResponseHandler(ConsoleWriter writer) {
    this.writer = writer;
  }

  public void commandHeartBeat() {
    writer.showProgress();
  }

  public void commandResponseChanged(CommandResponseEvent event) {
    writer.writeln(event.getEventMessage().getMessageText());
  }

  public void commandResponseFinished(CommandResponseEvent event) {
    writer.prompt();
  }

}
