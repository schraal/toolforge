package nl.toolforge.karma.core.cmd;

import java.text.MessageFormat;

/**
 * Base implementation of a command message. Provides standard implementations for methods handling messages.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class AbstractCommandMessage implements CommandMessage {

	private String message = null;

	public AbstractCommandMessage(String message) {
		this.message = message;
	}

	/**
	 * Constructs a <code>SimpleCommandMessage</code>. {@link java.text.MessageFormat} is used to convert parameters in
	 * <code>message</code>.
	 *
	 * @param message           The message string (with optional parameters).
	 * @param messageParameters An object array with parameter values.
	 */
	public AbstractCommandMessage(String message, Object[] messageParameters) {

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