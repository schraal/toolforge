/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
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
