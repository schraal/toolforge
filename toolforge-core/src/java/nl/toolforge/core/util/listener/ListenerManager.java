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

  // todo implementation should spawn a thread for each listener.

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

  public synchronized void register(ChangeListener listener) throws ListenerManagerException {

    if (listeners.contains(listener)) {
      throw new ListenerManagerException("Listener already registered.");
    }
    listeners.add(listener);
  }

  public void run() {

    logger.info("Listener manager started ...");

    while (isRunning()) {

      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      ((ChangeListener) listeners.get(0)).process();
    }
  }

  private boolean isRunning() {
    return running;
  }

  private synchronized void setRunning(boolean run) {
    running = run;
  }

  public synchronized void suspendListener() {
    setRunning(false);
  }
}
