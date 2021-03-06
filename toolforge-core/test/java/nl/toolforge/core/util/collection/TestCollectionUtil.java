package nl.toolforge.core.util.collection;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestCollectionUtil extends TestCase {

  public void testConcat() {

    Collection c = new ArrayList();
    c.add("a");
    c.add("b");
    c.add("c");

    assertEquals("a,b,c", CollectionUtil.concat(c, ','));
    assertFalse("a,b,c".equals(CollectionUtil.concat(c, ';')));
    assertFalse("a,b,c,".equals(CollectionUtil.concat(c, ',')));
  }

}
