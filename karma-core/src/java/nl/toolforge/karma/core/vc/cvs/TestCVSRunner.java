package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.test.LocalCVSInitializer;
import nl.toolforge.karma.core.test.FakeModule;

import java.io.File;

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
			Runner runner = new CVSRunner(getTestLocation());

			assertNotNull(runner);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testAdd1() {

		try {
			Runner runner = new CVSRunner(getTestLocation());

			// Prepare a fake module. All set to work for JUnit testing.
			//
			FakeModule module = new FakeModule(DEFAULT_MODULE_1, getTestLocation());
      module.setLocalPath(getDevelopmentHome());

			File newFile = new File(module.getLocalPath().getPath() + "test-file.txt");

			CommandResponse response = runner.add(module, newFile);

			assertEquals(new CVSException(CVSException.INVALID_CVSROOT), response.getException());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}