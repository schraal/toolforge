package nl.toolforge.karma.core.vc;

import junit.framework.TestCase;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestDevelopmentLine extends TestCase {

  public void testConstructorWithInvalidValues() {

    DevelopmentLine l = null;

    try {

      l = new DevelopmentLine(null);
      l = new DevelopmentLine("");
      l = new DevelopmentLine("*");
      l = new DevelopmentLine("!");
      l = new DevelopmentLine("~");
      l = new DevelopmentLine(" ");
      l = new DevelopmentLine(" A");

      l = new DevelopmentLine("0A");
      l = new DevelopmentLine("aaa_0-0");

      l = new DevelopmentLine(" :=+';\"<,>.");

      l = new DevelopmentLine("Aaaa_bb-ccc");

      fail("Should have failed.");

    } catch (IllegalArgumentException i) {
      assertTrue(true);
    }
  }

  public void testConstructorWithValidValues() {

    DevelopmentLine l = null;

    try {

      l = new DevelopmentLine("Aaaa");
      l = new DevelopmentLine("DEV");
      l = new DevelopmentLine("Aaaa-bb");
      l = new DevelopmentLine("Aaaa-bb-ccc");
      l = new DevelopmentLine("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

      l = new DevelopmentLine("Aaaa-0");

      assertTrue(true);

    } catch (IllegalArgumentException i) {
      fail(i.getMessage());
    }
  }

  public void testEquals() {

    DevelopmentLine l1 = new DevelopmentLine("Aaaa");
    DevelopmentLine l2 = new DevelopmentLine("Aaaa");
    DevelopmentLine l3 = new DevelopmentLine("AAAA");

    assertEquals(l1, l2);
    assertFalse(l1.equals(l3));
  }

}
