package nl.toolforge.karma.core;

import junit.framework.TestCase;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestErrorCode extends TestCase {

  public void testConstructor() {

    ErrorCode e = null;
    try {
      e = new ErrorCode("");
    } catch (IllegalArgumentException i) {
      assertTrue(true);
    }

    e = new ErrorCode("AAA-00000");
    assertNotNull(e);

    try {
      e = new ErrorCode("AAAA-00000");
      fail("Should have failed; pattern mismatch.");
      e = new ErrorCode("AA-00000");
      fail("Should have failed; pattern mismatch.");
    } catch (IllegalArgumentException i) {
      assertTrue("Pattern mismatch", true);
    }
  }

  public void testGetErrorMessage() {
    ErrorCode e = new ErrorCode("AAA-00000");
    assertEquals("AAA-00000", e.getErrorMessage());
  }

  public void testGetErrorCodeString() {
    ErrorCode e = new ErrorCode("AAA-00000");
    assertEquals("AAA-00000", e.getErrorCodeString());
  }
}
