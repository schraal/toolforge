package nl.toolforge.karma.core.cmd;

import java.text.MessageFormat;

/**
 * Simple implementation of a command message.
 *
 * @author D.A. Smedes
 * @version $Id$
 *
 * @deprecated Has been replaced by more specific implementations of CommandMessage.
 */
public class SimpleCommandMessage implements CommandMessage {

	private String message = null;

	public SimpleCommandMessage(String message) {
		this.message = message;
	}

	/**
	 * Constructs a <code>SimpleCommandMessage</code>. {@link MessageFormat} is used to convert parameters in
	 * <code>message</code>.
	 *
	 * @param message           The message string (with optional parameters).
	 * @param messageParameters An object array with parameter values.
	 */
	public SimpleCommandMessage(String message, Object[] messageParameters) {

		if (messageParameters.length != 0) {
			MessageFormat messageFormat = new MessageFormat(message);
			this.message = messageFormat.format(messageParameters);
		} else {
			this.message = message;
		}
	}

	/**
	 * Must be implemented by the specific implementation.
	 *
	 * @return The message text.
	 */
	public String getMessageText() {
		return message;
	}

}