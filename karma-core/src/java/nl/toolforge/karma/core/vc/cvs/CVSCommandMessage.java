package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.cmd.DefaultCommandMessage;

import java.util.Collection;

/**
 * Message implementation for CVS messages.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public final class CVSCommandMessage extends DefaultCommandMessage {

  public Collection events = null;

  public CVSCommandMessage(String message) {
    super(message);
  }

  public String getMessageText(int index) {
    return null;
  }
}