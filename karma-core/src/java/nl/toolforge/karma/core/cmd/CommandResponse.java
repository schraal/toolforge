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
import nl.toolforge.karma.core.cmd.event.CommandStartedEvent;
import nl.toolforge.karma.core.cmd.event.CommandFinishedEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A <code>CommandResponse</code> object is used to dispatch {@link CommandResponseEvent} events to listeners that are
 * interested in those events. These objects can be fed with <code>n</code> {@link CommandResponseListener}s.
 *
 * @author W.M. Oosterom
 * @author D.A. Smedes
 * @author W.H. Schraal
 * @version $Id$
 */
public class CommandResponse {

  private static Log logger = LogFactory.getLog(CommandResponse.class);

  private List listeners = new ArrayList();

  /**
   * Constructs a command response object.
   */
  public CommandResponse() {}

  /**
   * Dispatches the event to all {@link CommandResponseListener}s. If no listener has been registered, a warning will
   * be written to the log system.
   *
   * @param event The event that should be dispatched.
   */
  public synchronized void addEvent(CommandResponseEvent event) {
    if (listeners.size() > 0) {
      for (Iterator it = listeners.iterator(); it.hasNext(); ) {
        CommandResponseListener listener = (CommandResponseListener) it.next();

        if (event instanceof CommandStartedEvent) {
          listener.commandStarted(event);
        } else if (event instanceof CommandFinishedEvent) {
          listener.commandFinished(event);
        } else {
          listener.messageLogged(event);
        }
        
      }
    } else {
      logger.warn("No listener registered for command response (messages sent to /dev/null ...)");
    }
  }

  /**
   * Adds a {@link CommandResponseListener} that is interested in events added to this <code>CommandResponse</code>.
   *
   * @param responseListener
   */
  public final synchronized void addCommandResponseListener(CommandResponseListener responseListener) {
    listeners.add(responseListener);
  }

  /**
   * Removes <code>responseListener</code> from this <code>CommandResponse</code>.
   */
  public final synchronized void removeCommandReponseListener(CommandResponseListener responseListener) {
    listeners.remove(responseListener);
  }

}
