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
