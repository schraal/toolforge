package nl.toolforge.core.util.net;

import junit.framework.TestCase;

import java.net.ServerSocket;
import java.io.IOException;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestPing extends TestCase {

  public void testPing() {

    // Return results within 1000 ms.
    //

    int port = 55575;

    ServerSocket s = null;

    try {
      s = new ServerSocket(port);

      assertTrue("Could not connect to port " + port, Ping.ping("127.0.0.1", port, 1000));

    } catch (IOException e) {
      fail("Server socket to port " + port + " failed : " + e.getMessage());
    } finally {
      try {
        s.close();
      } catch (IOException e) {
        fail(e.getMessage());
      }
    }

    assertFalse(Ping.ping("127.0.0.1", port, 1000));
  }
}
