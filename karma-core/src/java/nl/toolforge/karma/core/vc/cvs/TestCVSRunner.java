package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.test.LocalCVSInitializer;
import nl.toolforge.karma.core.test.FakeModule;
import nl.toolforge.karma.core.KarmaException;

import java.io.File;
import java.io.IOException;

/**
 * <p>This class tests all stuff in the <code>cvs</code> package. For this to work properly, you should unpack the
 * test cvs repository and install on your local system.
 *
 * @author D.A. Smedes 
 * 
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

  public void testAdd1() {

    Runner runner = null;
    try {
      runner = getTestRunner();
    } catch (CVSException e) {
      fail(e.getMessage());
    }

    try {

      checkoutDefaultModule1();

      // Prepare a fake module. All set to work for JUnit testing.
      //
      FakeModule module = new FakeModule(DEFAULT_MODULE_1, getTestLocation());
      module.setLocalPath(getModuleHome(DEFAULT_MODULE_1));

      CommandResponse response = runner.add(module, getTestFileName());

      assertTrue(response.hasStatus(CVSResponseAdapter.FILE_ADDED_OK));

    } catch (KarmaException e) {
      fail(e.getMessage());
    }
  }
}