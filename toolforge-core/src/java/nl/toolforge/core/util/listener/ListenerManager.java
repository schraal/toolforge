/*
Toolforge core - Core of the Toolforge application suite
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
package nl.toolforge.core.util.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of a file listener. Kicks some object implementing the <code>ChangeListener</code> to do
 * something every x seconds.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class ListenerManager extends Thread {

  private static Log logger = LogFactory.getLog(ListenerManager.class);

  private static List listeners = null;

  private static ListenerManager instance = null;

  private static boolean running = true;

  public static ListenerManager getInstance() {

    if (instance == null) {
      instance = new ListenerManager();
    }
    return instance;
  }

  private ListenerManager() {
    listeners = new ArrayList();
  }

  /**
   * Registers a listener. At this moment, only the first listener is actually handled by this manager.
   *
   * @param listener
   * @throws ListenerManagerException
   */
  public synchronized void register(ChangeListener listener) throws ListenerManagerException {

    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void run() {

    logger.info("Listener manager started ...");

    while (isRunning()) {

      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      // todo should manage the list of listeners.
      //

      ((ChangeListener) listeners.get(0)).process();
    }
  }

  private boolean isRunning() {
    return running;
  }

  private synchronized void setRunning(boolean run) {
    running = run;
  }

  public synchronized void suspendListener(ChangeListener listener) {

    if (!listeners.contains(listener)) {
      listeners.remove(listener);
    }

    setRunning(false);
  }
}
