package nl.toolforge.karma.core.test;

import nl.toolforge.karma.core.test.BaseTest;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.prefs.Preferences;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import nl.toolforge.karma.core.vc.cvs.InitializationException;
import nl.toolforge.karma.core.vc.cvs.CVSRunner;
import nl.toolforge.karma.core.vc.cvs.CVSException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.SourceModule;

import java.util.Properties;
import java.util.Random;
import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import junit.framework.Assert;

/**
 * <p>Initializes tests that need a local CVS repository. This local repository can be found as a <code>zip</code>-file
 * and/or <code>tar</code> file in the <code>resources/test</code> directory of this packages' sources. It contains a
 * CVS repository with predefined modules that can be used for testing purposes. If the CVS repository cannot be found
 * (i.e. the user has not prepared his/her local environment with the CVSROOT), and you don't want your CVS stuff to
 * be tested, make sure you ignore these testcases in your test-configuration.
 *
 * <p>Note the dependency of this class with {@link nl.toolforge.karma.core.location.Location} and {@link nl.toolforge.karma.core.vc.cvs.CVSLocationImpl}.
 *
 * <p>When performing operations on managed files, a randomize function is used to be able to repeatedly perform
 * tests. Filenames and modulenames in the repository could be named like <code>bla_036548290.56437</code>.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class LocalCVSInitializer extends BaseTest {

	private static Log logger = LogFactory.getLog(LocalCVSInitializer.class);

	/** The name of the a test module in the test repository */
	protected static final String DEFAULT_MODULE_1 = "CORE-test-module-1";

	private static CVSLocationImpl location = null;
	private File tempDevelopmentHome = null;

	/** Can be used to access random <code>int</code>s. */
	protected static Random randomizer = new Random();

	public void setUp() throws InitializationException {

		String localPath = null; // Reference to test CVSROOT directory.

		try {
			Properties props = new Properties();
			props.load(getClass().getClassLoader().getResourceAsStream("test-cvs.properties"));
			localPath = props.getProperty("cvs.local.path");

			location = new CVSLocationImpl("local");

			File localRepository = new File(localPath);

			if (localRepository.exists()) {
				location.configureLocalRepository(new File(localPath));
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			throw new InitializationException(
				"Local CVS repository could not be initialized. Trying to initialize repository at : ".concat(localPath));
		}

		// Create a temporary directory in '/tmp' or 'C:\Temp' where the tests can checkout stuff. This directory gets a
		// random name.
		//
		try {

			tempDevelopmentHome = File.createTempFile("" + randomizer.nextInt(), null);
			tempDevelopmentHome.delete();
			tempDevelopmentHome.mkdir();

			logger.info("Temporary directory " + tempDevelopmentHome.getPath() + " created.");

		} catch (IOException e) {
			throw new InitializationException("Directory in " + System.getProperty("TMP_DIR") + " could be created.");
		}
	}

	/**
	 * Deletes the temporary directory.
	 */
	public void tearDown() {
		if (tempDevelopmentHome.delete()) {
			logger.info("Temporary directory " + tempDevelopmentHome.getPath() + " deleted.");
		} else {
			logger.info("Temporary directory " + tempDevelopmentHome.getPath() + " could not be deleted.");
		}
	}

	/**
	 * Gets the <code>CVSLocationImpl</code> that can be used for junit testing.
	 */
	protected CVSLocationImpl getTestLocation() {
		return location;
	}

	/**
	 * Creates a randomly named test filename in the module directory (<code>testModule</code>) for the test cvs
	 * repository. Names are of the form <code>test_&lt;random-int&gt;</code>.
	 */
	protected File getTestFileName(String testModule) throws InitializationException {

		File file = new File("test_" + randomizer.nextInt());

		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new InitializationException("Initialization failure >> " + e.getMessage());
		}

		return file;
	}

	/**
	 * Gets the temporary development home for the testrun. In a normal runtime environment, this would be equivalent
	 * with <code>new File({@link nl.toolforge.karma.core.prefs.Preferences#getDevelopmentHome})</code>.
	 * @return
	 */
	protected File getDevelopmentHome() {
		return tempDevelopmentHome;
	}

	/**
	 * <p>Tests if a checkout can be performed on {@link #DEFAULT_MODULE_1}, which is both a test as well as preparation
	 * for methods in extension classes.
	 */
	public final void testCheckoutDefaultModule1() {

		try {
			Runner runner = new CVSRunner(getTestLocation());

			FakeModule module = new FakeModule(DEFAULT_MODULE_1, location);

			CommandResponse response = runner.checkout(module, tempDevelopmentHome);

			Assert.assertEquals(new CVSException(CVSException.INVALID_CVSROOT), response.getException());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}