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
 * An event generated as a result of something ordinary. Commands can generate these events, but
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class MessageEvent extends CommandResponseEvent {

  private SimpleMessage message = null;

  /**
   * Creates a <code>MessageEvent</code> not linked to any command with a priority of <code>priority</code>.
   *
   * @param command  The command generated this event.
   * @param priority The priority of the event.
   */
  public MessageEvent(Command command, int priority) {
    super(command, priority);
  }

  /**
   * Creates the event for <code>command</code>.
   *
   * @param command  The command generated this event.
   * @param priority The priority of the event.
   * @param message  The message for the event.
   */
  public MessageEvent(Command command, int priority, SimpleMessage message) {
    super(command, priority);
    this.message = message;
  }

  /**
   * Creates the event for <code>command</code>.
   *
   * @param command The command generated this event.
   */
  public MessageEvent(Command command, SimpleMessage message) {
    super(command);
    this.message = message;
  }

  /**
   * Creates a <code>MessageEvent</code> not linked to any command.
   *
   * @param message Some message.
   */
  public MessageEvent(SimpleMessage message) {
    super(null);
    this.message = message;
  }

  /**
   * Returns a <code>SimpleMessage</code> formatted as <code>[ &lt;command-name&gt; ] &lt;message-text&gt;</code> if
   * this message was constructed with a <code>Command</code> object, otherwise it returns the
   * <code>SimpleMessgae</code> as-is.
   *
   * @return A <code>SimpleMessage</code> object optionally prefixed with the <code>Command</code> name.
   */
  public Message getEventMessage() {
    if (getCommand() == null) {
      return message;
    } else {
      return new SimpleMessage(MessageHelper.format(getCommand().getName(), message.getMessageText()));
    }
  }
}
