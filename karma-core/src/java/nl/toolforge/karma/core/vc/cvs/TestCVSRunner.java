package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.SourceModule;
import nl.toolforge.karma.core.SourceModuleDescriptor;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.test.LocalCVSInitializer;
import nl.toolforge.karma.core.vc.Runner;

/**
 * <p>This class tests all stuff in the <code>cvs</code> package. For this to work properly, you should unpack the
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
    }

    try {

      checkoutDefaultModule1();

      Module module =
        new SourceModule(new SourceModuleDescriptor(DEFAULT_MODULE_1, getTestLocation()), getDevelopmentHome());

      runner.add(module, getTestFileName());

      assertTrue(response.isOK());

    } catch (KarmaException e) {
      fail(e.getMessage());
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
    }

    try {

      checkoutDefaultModule1();

      Module module =
        new SourceModule(new SourceModuleDescriptor(DEFAULT_MODULE_1, getTestLocation()), getDevelopmentHome());

      runner.update(module, new Version("99-99"));

      fail("Expected a CVSException.");

    } catch (CVSException c) {
      assertEquals(CVSException.VERSION_NOT_FOUND.getErrorCodeString(), c.getErrorCode().getErrorCodeString());
    } catch (KarmaException e) {
      fail(e.getMessage());
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
    }

    try {

      checkoutDefaultModule1();

      Module module =
        new SourceModule(new SourceModuleDescriptor(DEFAULT_MODULE_1, getTestLocation()), getDevelopmentHome());

      runner.update(module, new Version("0-1")); // On the mainline (HEAD)

      assertTrue(response.isOK());

    } catch (CVSException c) {
      fail(c.getMessage());
    } catch (KarmaException e) {
      fail(e.getMessage());
    }
  }

  /**
   *
   */
  class ResponseFaker extends CommandResponse {

    private boolean ok = true;

    /**
     * Success is reported back to this method, otherwise an exception would have been thrown.
     *
     * @param message Some message sent by CVS.
     */
    public void addMessage(CommandMessage message) {
      ok = true;
    }

    public boolean isOK() {
      return ok;
    }
  }

}