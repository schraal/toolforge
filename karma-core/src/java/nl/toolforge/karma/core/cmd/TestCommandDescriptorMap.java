package nl.toolforge.karma.core.cmd;

import junit.framework.TestCase;

/**
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestCommandDescriptorMap extends TestCase {

  public void testAdd1() {

    CommandDescriptor d1 = new CommandDescriptor("1", "b bb bbb");
    CommandDescriptor d2 = new CommandDescriptor("1", "b bb bbb");

    CommandDescriptorMap map = new CommandDescriptorMap();
    map.add(d1);
    map.add(d2);

    assertEquals(4, map.size());
  }

  public void testGet() {

    CommandDescriptor d1 = new CommandDescriptor("1", "b bb bbb");
    CommandDescriptorMap map = new CommandDescriptorMap();
    map.add(d1);

    assertEquals(d1, map.get("1"));
    assertEquals(d1, map.get("b"));
    assertEquals(d1, map.get("bb"));
    assertEquals(d1, map.get("bbb"));
  }
}
