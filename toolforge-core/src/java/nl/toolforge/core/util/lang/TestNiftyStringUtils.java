package nl.toolforge.core.util.lang;

import junit.framework.TestCase;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestNiftyStringUtils extends TestCase {

  public void testSplit() {

    assertEquals(0, NiftyStringUtils.split(null, null, 1).length);
    assertEquals(0, NiftyStringUtils.split(null, "", 1).length);
    assertEquals(0, NiftyStringUtils.split("", null, 1).length);
    assertEquals(0, NiftyStringUtils.split("", "", 1).length);
    assertEquals(1, NiftyStringUtils.split("aaa", "h", 9).length);

    String str = null;

    str = "aaa bbb ccc ddd eee fff ggg";

    assertEquals(7, NiftyStringUtils.split(str, " ", 4).length);
    assertEquals(7, NiftyStringUtils.split(str, " ", 5).length);

    str = "aaagg bbb ccc";

    assertEquals(3, NiftyStringUtils.split(str, " ", 5).length);
  }

  public void testDeleteWhitespaceExceptOne() {

    String str = "aaa bbb";

    assertEquals(str, NiftyStringUtils.deleteWhiteSpaceExceptOne(str));

    str = "";
    assertEquals(str, NiftyStringUtils.deleteWhiteSpaceExceptOne(str));

    str = "aaa  bbb";
    assertEquals("aaa bbb", NiftyStringUtils.deleteWhiteSpaceExceptOne(str));

    str = "aaa \nbbb";
    assertEquals("aaa bbb", NiftyStringUtils.deleteWhiteSpaceExceptOne(str));
  }
}
