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
package nl.toolforge.karma.core.cmd.event;

import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandException;

/**
 * Generated when the command execution failed with a <code>CommandException</code>. This event is - by default -
 * generated when the {@link nl.toolforge.karma.core.cmd.CommandContext#execute(Command)}-method caught a
 * CommandException.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CommandFailedEvent extends ExceptionEvent {

  /**
   * Creates the event for <code>command</code>.
   *
   * @param command The command that has started.
   */
  public CommandFailedEvent(Command command, CommandException exception) {
    super(command, exception);
  }

  /**
   * Returns a SimpleMessage formatted as <code>[ &lt;command-name&gt; ] Started.</code>.
   */
  public Message getEventMessage() {
    return new SimpleMessage(MessageHelper.format(getCommand().getName(), "Command FAILED!"));
  }
}
