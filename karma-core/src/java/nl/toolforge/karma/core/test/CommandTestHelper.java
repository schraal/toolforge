package nl.toolforge.karma.core.test;

/**
 * Base test class to test <code>Command</code> instances (including the <code>execute</code> method).
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class CommandTestHelper extends LocalCVSInitializer {

  /**
   * Te ensure that this class itself can be tested.
   */
  public void testBogus() {
    assertTrue(true);
  }

  /**
   * Checks if a CVS <code>Entries</code> file contains the correct sticky tag.
   */
  protected final boolean hasStickyTag() {

    return true;
  }
}
