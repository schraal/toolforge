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
 * Event thrown when a {@link nl.toolforge.karma.core.cmd.CommandResponse} changes. Classes interested in this event
 * implement the {@link CommandResponseListener} interface.
 *
 * @author W.H.Schraal
 * @author D.A. Smedes
 */
public abstract class CommandResponseEvent implements Event {

  private Command command = null;

  private int priority = LEVEL_INFO;

  /**
   * Constructs a <code>CommandResponseEvent</code> and assumes the default priority to be {@link #LEVEL_INFO}.
   *
   * @param command The <code>Command</code> that generated
   */
  public CommandResponseEvent(Command command) {
    this(command, Event.LEVEL_INFO);
  }

  /**
   * Constructs a <code>CommandResponseEvent</code>.
   *
   * @param command  The <code>Command</code> that generated
   * @param priority The priority of the event.
   */
  public CommandResponseEvent(Command command, int priority) {
    this.command = command;
    this.priority = priority;
  }

  /**
   * Gets the priority level for the event.
   *
   * @return The priority level for the event.
   *
   * @see    {@link #LEVEL_VERBOSE}
   * @see    {@link #LEVEL_INFO}
   * @see    {@link #LEVEL_DEBUG}
   */
  public final int getPriority() {
    return priority;
  }

  /**
   * Implementations should return a specific {@link Message} instance.
   * @return
   */
  public abstract Message getEventMessage();

  /**
   * Returns the command that generated this event.
   *
   * @return The {@link Command} command that generated this event.
   */
  public Command getCommand() {
    return command;
  }
}
