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

/**
 * Generated before a command is executed. This event has the default priority {@link CommandResponseEvent#LEVEL_DEBUG}.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CommandStartedEvent extends CommandResponseEvent {

  private long start = 0L;

  /**
   * Creates the event for <code>command</code>.
   *
   * @param command The command that has started.
   */
  public CommandStartedEvent(Command command) {
    super(command, LEVEL_DEBUG);
    start = System.currentTimeMillis();;
  }

  /**
   * Returns the time this event was generated, effectively, the time the command was started.
   *
   * @return Start time of the command.
   */
  public long getTime() {
    return start;
  }

  /**
   * Returns a SimpleMessage formatted as <code>[ &lt;command-name&gt; ] Started.</code>.
   */
  public Message getEventMessage() {
    return new SimpleMessage(MessageHelper.format(getCommand().getName(), "Started."));
  }
}
