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
