package nl.toolforge.karma.cli;

import junit.framework.TestCase;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class HistoryManagerTest extends TestCase {

  public void testConstructor() {

    HistoryManager manager = new HistoryManager(10);
    assertNotNull(manager);
  }

  public void testAddHistoryItem1() {
    HistoryManager manager = new HistoryManager(-1);
    manager.addHistoryItem("A");

    assertEquals("A", manager.getCurrentHistoryItem());

    manager.addHistoryItem("B");

    assertEquals("B", manager.getCurrentHistoryItem());
  }
}
