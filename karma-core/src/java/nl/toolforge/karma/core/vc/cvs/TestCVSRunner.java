package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.SourceModule;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.SourceModuleDescriptor;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.SourceModuleDescriptor;
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

  public void testAdd1() {

    Runner runner = null;
    try {
      runner = getTestRunner();
    } catch (CVSException e) {
      fail(e.getMessage());
    }

    try {

      checkoutDefaultModule1();

//      // Prepare a fake module. All set to work for JUnit testing.
//      //
//      FakeModule module = new FakeModule(DEFAULT_MODULE_1, getTestLocation());
//      module.setLocalPath(getModuleHome(DEFAULT_MODULE_1));

      Module module =
        new SourceModule(new SourceModuleDescriptor(DEFAULT_MODULE_1, getTestLocation()), getDevelopmentHome());

      CommandResponse response = runner.add(module, getTestFileName());

      assertTrue(response.hasStatus(CVSResponseAdapter.FILE_ADDED_OK));

    } catch (KarmaException e) {
      fail(e.getMessage());
    }
  }

  public void testUpdateWithInvalidVersion1() {

//		class SourceModuleFaker extends SourceModule {
//			public SourceModuleFaker() throws KarmaException {
//				super(DEFAULT_MODULE_1, getTestLocation());
//			}
//		}

    Runner runner = null;
    try {
      runner = getTestRunner();
    } catch (CVSException e) {
      fail(e.getMessage());
    }

    CommandResponse response = null;
    try {

      checkoutDefaultModule1();

      Module module =
        new SourceModule(new SourceModuleDescriptor(DEFAULT_MODULE_1, getTestLocation()), getDevelopmentHome());

      response = runner.update(module, new Version("99-99"));

      fail("Excepted a CVSException.");

      assertTrue(response.hasStatus(CVSResponseAdapter.FILE_ADDED_OK));

    } catch (CVSException c) {
      assertTrue(c.getErrorCode().equals(CVSException.VERSION_NOT_FOUND));
    } catch (KarmaException e) {
      fail(e.getMessage());
    }
  }

  public void testUpdateWithCorrectVersion() {

//    class SourceModuleFaker extends SourceModule {
//      public SourceModuleFaker() throws KarmaException {
//        super(DEFAULT_MODULE_1, getTestLocation());
//      }
//    }

    Runner runner = null;
    try {
      runner = getTestRunner();
    } catch (CVSException e) {
      fail(e.getMessage());
    }

    CommandResponse response = null;
    try {

      checkoutDefaultModule1();
//
//			// Prepare a fake module. All set to work for JUnit testing.
//			//
//			SourceModuleFaker module = new SourceModuleFaker();

      Module module =
        new SourceModule(new SourceModuleDescriptor(DEFAULT_MODULE_1, getTestLocation()), getDevelopmentHome());

      response = runner.update(module, new Version("0-1")); // On the mainline (HEAD)

      assertTrue(response.hasStatus(CVSResponseAdapter.MODULE_UPDATED_OK));

    } catch (CVSException c) {
      fail(c.getMessage());
    } catch (KarmaException e) {
      fail(e.getMessage());
    }
  }
}