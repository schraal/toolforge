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

import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.cmd.Command;

/**
 * Indicates some error. This event takes an ErrorCode as its constructor argument, to allow for handling of those
 * error codes as an event.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class ErrorEvent extends CommandResponseEvent {

  private ErrorCode code = null;

  /**
   * Creates the event for <code>command</code>.
   *
   * @param command The command that resulted in the error.
   * @param code    The <code>ErrorCode</code> for this event.
   */
  public ErrorEvent(Command command, ErrorCode code) {
    super(command);
    this.code = code;
  }

  /**
   * Creates a <code>MessageEvent</code> not linked to any command.
   *
   * @param code The <code>ErrorCode</code> for this event.
   */
  public ErrorEvent(ErrorCode code) {
    this(null, code);
  }

  /**
   * Creates the event for <code>command</code>.
   *
   * @param command          The command that resulted in the error.
   * @param code             The <code>ErrorCode</code> for this event.
   * @param messageArguments Message parameters that can be filled in the (localized) message for the errorcode.
   */
  public ErrorEvent(Command command, ErrorCode code, Object[] messageArguments) {
    super(command);
    this.code = code;
    code.setMessageArguments(messageArguments);
  }

  /**
   * Creates a <code>MessageEvent</code> not linked to any command.
   *
   * @param code             The <code>ErrorCode</code> for this event.
   * @param messageArguments Message parameters that can be filled in the (localized) message for the errorcode.
   */
  public ErrorEvent(ErrorCode code, Object[] messageArguments) {
    this(null, code, messageArguments);
  }

  /**
   * Returns a <code>SimpleMessage</code> formatted as <code>[ &lt;command-name&gt; ]
   * &lt;error-message-from-errorcode&gt;</code>.
   *
   * @return A {@link SimpleMessage} object prefixed with the command, if the command for this event is not
   *         <code>null</code>.
   */
  public Message getEventMessage() {
    if (getCommand() == null) {
      return new SimpleMessage(code.getErrorMessage());
    } else {
      return new SimpleMessage(MessageHelper.format(getCommand().getName(), code.getErrorMessage()));
    }
  }

  /**
   * The exception that generated this event.
   *
   * @return The <code>ErrorCode</code> that caused this event to occur.
   */
  public ErrorCode getErrorCode() {
    return code;
  }
}
