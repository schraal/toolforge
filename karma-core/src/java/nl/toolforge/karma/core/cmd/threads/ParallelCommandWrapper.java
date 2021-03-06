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
package nl.toolforge.karma.core.cmd.threads;

import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.event.CommandResponseListener;


/**
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class ParallelCommandWrapper extends Thread {

  private boolean running = true;

  private Command command = null;
  private CommandResponseListener listener;
  private CommandException exception = null;

  public ParallelCommandWrapper(Command command, CommandResponseListener listener) {
    this.command = command;
    this.listener = listener;
  }

  public CommandResponse getCommandResponse() {
    return null;
  }

  public void run() {

    try {

      startRunning();

      command.execute();

    } catch (CommandException c) {
      exception = c;
    } finally {
      stopRunning();
    }
  }

  private synchronized void startRunning() {

    running = true;
    command.registerCommandResponseListener(listener);
  }

  private synchronized void stopRunning() {

    command.deregisterCommandResponseListener(listener);
    command.cleanUp();

    running = false;
  }

  public synchronized boolean isRunning() {
    return running;
  }

  /**
   * Returns the <code>CommandException</code> when the <code>run</code>-method had thrown one. This method can thus be
   * used to check the actual results (if an error occurred).
   *
   * @return A <code>CommandException</code> or <code>null</code> if no exception was thrown.
   */
  public CommandException getException() {
    return exception;
  }

}
