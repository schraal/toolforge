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

import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.event.Message;
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.module.SourceModule;
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.test.LocalCVSInitializer;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.VersionControlException;

import java.io.File;

/**
 * <p>This class tests all stuff in the <code>cvsimpl</code> package. For this to work properly, you should unpack the
 * test cvs repository and install on your local system.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class TestCVSRunner extends LocalCVSInitializer {

  public void testConstructor() {

    try {
      Runner runner = getTestRunner();

      assertNotNull(runner);

    } catch (CVSException e) {
      fail(e.getMessage());
    } catch (AuthenticationException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Tests if a module can be added to a CVS repository.
   */
  public void testAdd1() {

    Runner runner = null;
    ResponseFaker response = new ResponseFaker();
    try {
      runner = getTestRunner();
    } catch (CVSException e) {
      fail(e.getMessage());
    } catch (AuthenticationException e) {
      fail(e.getMessage());
    }

    try {

      Module module = checkoutDefaultModule1();

      runner.add(module, new String[]{getTestFileName()}, new String[]{"src/java/nl/test", "resources"});

      assertTrue(response.isOK());

    } catch (VersionControlException c) {
      fail(c.getMessage());
    }
  }

  /**
   * Tests if an added module actually exists in the repository.
   */
  public void testAddAndExistsInRepository() {

    Runner runner = null;
    try {
      runner = getTestRunner();
    } catch (CVSException e) {
      fail(e.getMessage());
    } catch (AuthenticationException e) {
      fail(e.getMessage());
    }

    try {

      Module module = checkoutDefaultModule1();

      runner.add(module, new String[]{getTestFileName()}, new String[]{});

      assertTrue(runner.existsInRepository(module));

    } catch (VersionControlException c) {
      fail(c.getMessage());
    }

    // The following module does not exist.
    //
    Module module = new SourceModule("BLAAT", getTestLocation());
    module.setBaseDir(new File(""));
    assertFalse(runner.existsInRepository(module));
  }

  public void testCheckoutAndAdd() {

    Runner runner = null;
    try {
      runner = getTestRunner();
    } catch (CVSException e) {
      fail(e.getMessage());
    } catch (AuthenticationException e) {
      fail(e.getMessage());
    }

    try {

      Module module = checkoutDefaultModule1();

      assertTrue(new File(getWorkingContext().getProjectBaseDirectory(), module.getName()).exists());

      ((CVSRunner) runner).add(module, new String[]{"blaat-file"}, new String[]{"blaat-dir"});

      assertTrue(true);

    } catch (VersionControlException c) {
      fail(c.getMessage());
    }
  }

  /**
   * Tests if an update of a module with a non-existing version is handled correctly.
   */
  public void testUpdateWithInvalidVersion1() {

    Runner runner = null;
    try {
      runner = getTestRunner();
    } catch (CVSException e) {
      fail(e.getMessage());
    } catch (AuthenticationException e) {
      fail(e.getMessage());
    }

    try {

      Module module = checkoutDefaultModule1();

      runner.update(module, new Version("99-99"));

      fail("Expected a CVSException.");

    } catch (VersionControlException c) {
      assertTrue(true);
    }
  }

  /**
   * Tests if an update of a module with an existing version from a CVS repository is handled correctly.
   */
  public void testUpdateWithCorrectVersion() {

    Runner runner = null;
    ResponseFaker response = new ResponseFaker();
    try {
      runner = getTestRunner(response);
    } catch (CVSException e) {
      fail(e.getMessage());
    } catch (AuthenticationException e) {
      fail(e.getMessage());
    }

    try {

      Module module = checkoutDefaultModule1();

      runner.update(module, new Version("0-1")); // On the mainline (HEAD)

      assertTrue(response.isOK());
      assertTrue("Should be readonly.", !new File(module.getBaseDir(), Module.MODULE_DESCRIPTOR).canWrite());

    } catch (VersionControlException c) {
      fail(c.getMessage());
    }
  }

  public void testPatchLine() {

    Runner runner = null;
    ResponseFaker response = new ResponseFaker();
    try {
      runner = getTestRunner(response);
    } catch (CVSException e) {
      fail(e.getMessage());
    } catch (AuthenticationException e) {
      fail(e.getMessage());
    }

    try {

      Module module = checkoutDefaultModuleWithVersion();
      module.setBaseDir(new File(getWorkingContext().getProjectBaseDirectory(), module.getName()));
      module.markPatchLine(true);

      runner.createPatchLine(module);
      assertTrue(runner.hasPatchLine(module));

    } catch (VersionControlException c) {
      fail(c.getMessage());
    }
  }

  /**
   *
   */
  class ResponseFaker extends CommandResponse {

    private boolean ok = true;

    public ResponseFaker() {
      super();
    }

    /**
     * Success is reported back to this method, otherwise an exception would have been thrown.
     *
     * @param message Some message sent by CVS.
     */
    public void addEvent(Message message) {
      ok = true;
    }

    public boolean isOK() {
      return ok;
    }
  }

}