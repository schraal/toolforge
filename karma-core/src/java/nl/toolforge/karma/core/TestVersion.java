package nl.toolforge.karma.core;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * @version $Id$
 */
public class TestVersion extends TestCase {

  public void testConstuctorWithValidVersions() {

    Version v = null;

    try {
      v = new Version("0-1");
      v = new Version("0000-1");
      v = new Version("0000-1111");
      v = new Version("0000-1111-1");
      v = new Version("0000-1111-1222");

      v = new Version("1-0");
      v = new Version("0-1-4");

      assertTrue(true);

    } catch (PatternSyntaxException i) {
      fail(i.getMessage());
    }
  }

  public void testConstuctorWithInvalidVersions() {

    Version v = null;

    try {
      v = new Version("");
      v = new Version(" ");
      v = new Version(" 0");
      v = new Version(" 0-");

      v = new Version("0");
      v = new Version("0-");
      v = new Version("0-1-");
      v = new Version("0-1-1-");
      v = new Version("0-1-1-1");

      v = new Version("00000-1");
      v = new Version("0000--1");
      v = new Version("0--1");
      v = new Version("0--1-0");

      v = new Version("0-1a");
      v = new Version("A-1");
      v = new Version("1-A");

      fail("Should have failed; invalid pattern.");

    } catch (IllegalArgumentException i) {
      assertTrue(true);
    }
  }

  public void testConstuctorWithInts() {

    Version v = new Version(new int[]{0, 1, 3});
    assertEquals("0-1-3", v.getVersionNumber());
  }

  public void testCompare() {

    Version v1 = new Version("0-2-6");
    Version v2 = new Version("1-1-1");
    Version v3 = new Version("0-2-8");

    List s = new ArrayList();
    s.add(v1);
    s.add(v2);
    s.add(v3);

    Collections.sort(s);

    assertEquals(v1, (Version) s.get(0));
    assertEquals(v2, (Version) s.get(2));
    assertEquals(v3, (Version) s.get(1));
  }

}
