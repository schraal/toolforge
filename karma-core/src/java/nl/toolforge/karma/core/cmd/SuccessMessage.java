package nl.toolforge.karma.core.cmd;



/**
 * A CommandMessage that implements a success.
 *
 * @version $Id$
 */
public class SuccessMessage extends AbstractCommandMessage {
  //todo make this a localized message
  private String message;

  public SuccessMessage(String message) {
    this(message, null);
  }

  public SuccessMessage(String message, Object[] messageParameters) {
    super(message, messageParameters);
    this.message = message;
  }


//  public String getMessageText() {
//    return message;
//  }

}
