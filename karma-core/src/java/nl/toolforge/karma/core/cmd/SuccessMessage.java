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

    super(message, null);
    this.message = message;
  }

  public SuccessMessage(String message, Object[] messageParameters) {
    super(message, messageParameters);
  }


//  public String getMessageText() {
//    return message;
//  }

}
