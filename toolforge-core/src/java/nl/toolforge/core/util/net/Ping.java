package nl.toolforge.core.util.net;

import java.util.Date;

/**
 * The good old <code>ping</code> command in Java. Probably a copy of what exists elsewhere, but since I would have
 * expect this one in the JDK ... Then, it took me 15 minutes to build this.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class Ping {

  private Ping() {}

  /**
   * Check if port <code>port</code> is reachable on host <code>host</code> within the next <code>timeOutInMillis</code>
   * milliseconds, or else the check failed.
   */
  public static boolean ping(String host, int port, int timeOutInMillis) {

    PingThread t = new PingThread(host, port);

    t.start();

    long now = new Date().getTime();
    long start = now;
    boolean success = false;

    while (!success && now < (start + timeOutInMillis)) {

      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        return false;
      }
      success = t.pingOk();
      now = new Date().getTime();
    }
    return success;
  }
}
