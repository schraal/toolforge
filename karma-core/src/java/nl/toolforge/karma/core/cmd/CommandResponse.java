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
package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.cmd.event.CommandResponseEvent;
import nl.toolforge.karma.core.cmd.event.CommandResponseListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;

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


	public CommandResponse() {
	}

  /**
   * Add a message to the command response. When a {@link CommandResponseListener} has been registered with this
   * response, {@link CommandResponseListener#commandResponseChanged(CommandResponseEvent)} will be called. If no
   * listener has been registered, a warning will be written to the log system.
   *
   * @param message The message to add to the response.
   */
	public synchronized void addMessage(CommandMessage message) {
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
  public final synchronized void addCommandResponseListener(CommandResponseListener responseListener) {
    listeners.add(responseListener);
  }

  /**
   * Remove the CommandResponseListener.
   */
  public final synchronized void removeCommandReponseListener(CommandResponseListener responseListener) {
    listeners.remove(responseListener);
  }

}
