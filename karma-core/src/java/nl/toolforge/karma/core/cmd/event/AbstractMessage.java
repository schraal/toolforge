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

import nl.toolforge.karma.core.cmd.event.Message;

import java.text.MessageFormat;

/**
 * Base implementation of a {@link Message}. Provides standard implementations for methods handling messages.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class AbstractMessage implements Message {

	private String message = null;

	public AbstractMessage(String message) {
		this.message = message;
	}

	/**
	 * Constructs a message. {@link java.text.MessageFormat} is used to convert parameters in the <code>message</code>
   * text.
	 *
	 * @param message           The message string (with optional parameters).
	 * @param messageParameters An object array with parameter values.
	 */
	public AbstractMessage(String message, Object[] messageParameters) {

		if (messageParameters != null && messageParameters.length != 0) {
			MessageFormat messageFormat = new MessageFormat(message);
			this.message = messageFormat.format(messageParameters);
		} else {
			this.message = message;
		}
	}

	/**
	 * Returns the message as a String, having replaced all parameters with the actual content.
	 *
	 * @return The message text.
	 */
	public String getMessageText() {
		return message;
	}

}