package nl.toolforge.core.util.net;

import java.net.Socket;
import java.io.IOException;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class PingThread extends Thread {

  public final static int DEFAULT_TIMEOUT = 1000;

  private int port = -1;
  private String host = null;

  private boolean success = false;

  public PingThread(String host, int port) {

    this.port = port;
    this.host = host;
  }

  public void run() {

    try {
      new Socket(host, port);
      success = true;
    } catch (IOException e) {
      success = false;
    }
  }

  public boolean pingOk() {
    return success;
  }

}
