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

import java.text.MessageFormat;


/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class SimpleMessage implements Message {

  private String message = null;
  private Object[] messageArguments = new Object[0];

  /**
   * Constructs a <code>CommandMessage</code> with a (simple) text message.
   *
   * @param message The message text for the <code>CommandMessage</code>.
   */
  public SimpleMessage(String message) {
    this(message,  null);
  }

  /**
   * Constructs a <code>CommandMessage</code> with a (simple) text message and message parameters as per the
   * <code>java.text.MessageFormat</code> manner.
   *
   * @param message The message text for the <code>CommandMessage</code>.
   */
  public SimpleMessage(String message, Object[] messageArguments) {

    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null.");
    }

    this.message = message;
    this.messageArguments = messageArguments;
  }

  /**
   * The text message for this <code>CommandMessage</code>, with
   *
   * @return The text message for this <code>CommandMessage</code>.
   */
  public String getMessageText() {
    
    if (messageArguments != null && messageArguments.length != 0) {
      MessageFormat messageFormat = new MessageFormat(message);
      message = messageFormat.format(messageArguments);
    }
    return message;
  }
}
