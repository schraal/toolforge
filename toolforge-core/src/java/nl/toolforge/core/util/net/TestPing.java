package nl.toolforge.core.util.net;

import junit.framework.TestCase;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestPing extends TestCase {

  public void testPing() {

    // Return results within 1000 ms.
    //
    //todo: make this test work always. E.g. by starting a socket listener
    //before starting the test. This way we can garantee that te test will work.
    //pinging to www.sourceforge.net:80 does not work when you're behind a proxy.
    assertTrue(Ping.ping("127.0.0.1", 22, 1000));
    
    assertFalse(Ping.ping("blaat", 80, 1000));
  }
}
