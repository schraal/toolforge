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
package nl.toolforge.karma.core.vc.cvsimpl;

import nl.toolforge.karma.core.test.BaseTest;
import junit.framework.TestCase;

/**
 * Test <code>CVSRepository</code> instances.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class TestCVSRepository extends TestCase {

  public void testGetCVSROOT1() {

    CVSRepository c = new CVSRepository("a");
    c.setProtocol("local");
    c.setRepository("/home/cvsroot");

    try {
      assertEquals(":local:/home/cvsroot", c.getCVSRoot());
    } catch (CVSException e) {
      fail(e.getMessage());
    }
  }

  public void testGetCVSROOT2() {

    CVSRepository c = new CVSRepository("a");
    c.setProtocol("pserver");
    c.setRepository("/home/cvsroot");

    try {
      c.getCVSRoot();

      fail("Would have expected an exception. Pserver requires host and port.");
    } catch (CVSException e) {
      assertTrue(true);
    }

    c.setHost("localhost");
    c.setPort("2409");
    c.setUsername("sinterklaas");

    try {
      assertEquals(":pserver:sinterklaas@localhost:2409/home/cvsroot", c.getCVSRoot());
    } catch (CVSException e) {
      fail(e.getMessage());
    }

  }
}