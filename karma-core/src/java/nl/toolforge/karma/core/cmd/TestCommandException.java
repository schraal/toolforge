package nl.toolforge.karma.core.cmd;

import junit.framework.TestCase;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestCommandException extends TestCase {

  public void testException1() {

    CommandException e = null;

    e = new CommandException(CommandException.TEST_CODE);

    assertEquals("CMD-00000 : Test message with arguments `{0}` and `{1}`", e.getMessage());

    e = new CommandException(CommandException.TEST_CODE, new Object[]{"1", "2"});
    assertEquals("CMD-00000 : Test message with arguments `1` and `2`", e.getMessage());

    e = new CommandException(CommandException.TEST_CODE, new Object[]{"3", "4"});
    assertEquals("CMD-00000 : Test message with arguments `3` and `4`", e.getMessage());
  }

}
