package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.vc.cvs.CVSResponseAdapter;
import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.core.cmd.event.CommandResponseListener;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A CommandResponse object is used to report messages from 
 *
 * @author W.M. Oosterom
 * @author D.A. Smedes
 * @author W.H. Schraal
 * @version $Id$
 */
public abstract class CommandResponse {

  private static Log logger = LogFactory.getLog(CommandResponse.class);

  //todo this has to become a list of listeners.
  private List listeners = new ArrayList();

	// Contains the exception that was thrown during execution of the command
	//
	private Exception commandException = null;

	public CommandResponse() {
	}

  /**
   * Add a message to the command response. When a {@link CommandResponseListener} has been registered with this
   * response, {@link CommandResponseListener#commandResponseChanged(CommandResponseEvent)} will be called. If no
   * listener has been registered, a warning will be written to the log system.
   *
   * @param message The message to add to the response.
   */
	public void addMessage(CommandMessage message) {
    if (listeners.size() > 0) {
      for (Iterator it = listeners.iterator(); it.hasNext(); ) {
        CommandResponseListener listener = (CommandResponseListener) it.next();
        listener.commandResponseChanged(new CommandResponseEvent(message));
      }
    } else {
      logger.warn("No listener registered for command response (messages sent to /dev/null ...)");
    }
  }

  /**
   * Set the CommandResponseListener. This listener is going to give the user feedback
   * about the changes in the command response.
   *
   * @param responseListener
   */
  public final void addCommandResponseListener(CommandResponseListener responseListener) {
    listeners.add(responseListener);
  }

  /**
   * Remove the CommandResponseListener.
   */
  public final void removeCommandReponseListener(CommandResponseListener responseListener) {
    listeners.remove(responseListener);
  }

}
