package nl.toolforge.core.util.net;

import junit.framework.TestCase;

import java.util.Date;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestPing extends TestCase {

  public void testPing() {

    // Return results within 100 ms.
    //
    assertTrue(Ping.ping("www.sourceforge.net", 80, 1000));
    
    assertFalse(Ping.ping("blaat", 80, 1000));
  }
}
