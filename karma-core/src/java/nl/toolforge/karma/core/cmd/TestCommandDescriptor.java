package nl.toolforge.karma.core.cmd;

import junit.framework.TestCase;

/**
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestCommandDescriptor extends TestCase {

  public void testEquals1() {

    CommandDescriptor d1 = new CommandDescriptor("1", "b bb bbb");
    CommandDescriptor d2 = new CommandDescriptor("1", "b bb bbb");

    assertEquals(d1, d2);
  }

  public void testEquals2() {

    CommandDescriptor d1 = new CommandDescriptor("2a", "b bb bbb");
    CommandDescriptor d2 = new CommandDescriptor("2b", "b bb bbb");

    assertEquals(d1, d2);
  }

  public void testEquals3() {

    CommandDescriptor d1 = new CommandDescriptor("3a", "b");
    CommandDescriptor d2 = new CommandDescriptor("3b", "b bb bbb");

    assertEquals(d1, d2);
  }

  public void testEquals4() {

    CommandDescriptor d1 = new CommandDescriptor("4a", "b");
    CommandDescriptor d2 = new CommandDescriptor("4b", "c cc b");

    assertEquals(d1, d2);
  }

  public void testEquals5() {

    CommandDescriptor d1 = new CommandDescriptor("4a", "b");
    CommandDescriptor d2 = new CommandDescriptor("4b", "c cc ccc");

    assertFalse(d1.equals(d2));
  }

  public void testEquals6() {

    CommandDescriptor d1 = new CommandDescriptor("create-password", "passwd");
    CommandDescriptor d2 = new CommandDescriptor("delete-working-context", "passwd");

    assertEquals(d1, d2);
  }

}
