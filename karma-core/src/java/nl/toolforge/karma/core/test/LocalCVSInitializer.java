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
package nl.toolforge.karma.core.test;

import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.cvsimpl.CVSException;
import nl.toolforge.karma.core.vc.cvsimpl.CVSRepository;
import nl.toolforge.karma.core.vc.cvsimpl.CVSRunner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

/**
 * <p>Initializes tests that need a local CVS repository. This local repository can be found as a <code>zip</code>-file
 * and/or <code>tar</code> file in the <code>resources/test</code> directory of this packages' sources. It contains a
 * CVS repository with predefined modules that can be used for testing purposes. If the CVS repository cannot be found
 * (i.e. the user has not prepared his/her local environment with the CVSROOT), and you don't want your CVS stuff to
 * be tested, make sure you ignore these testcases in your test-configuration.
 *
 * <p>Note the dependency of this class with {@link nl.toolforge.karma.core.location.Location} and
 * {@link nl.toolforge.karma.core.vc.cvsimpl.CVSRepository}.
 *
 * <p>When performing operations on managed files, a randomize function is used to be able to repeatedly perform
 * tests. Filenames and modulenames in the repository could be named like <code>bla_036548290.56437</code>.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class LocalCVSInitializer extends BaseTest {

  private static Log logger = LogFactory.getLog(LocalCVSInitializer.class);

  /**
   * The name of the a test module in the test repository
   */
  protected static final String DEFAULT_MODULE_1 = "CORE-test-module-1";

  private static CVSRepository location = null;
  private File localCVSRepository = null;

  /**
   * Can be used to access random <code>int</code>s.
   */
  protected static Random randomizer = new Random();

  public void setUp() throws InitializationException {

    super.setUp(); // ...

    String localPath = null; // Reference to test CVSROOT directory.

    try {
      Properties props = new Properties();
      InputStream stream = getClass().getClassLoader().getResourceAsStream("test/test-cvs.properties");
      if (stream == null) {
        //properties file does not exist
        //log an error. Nullpointer will follow... ;)
        logger.error("could not find properties file: test-cvs.properties");
      }
      props.load(stream);
      localPath = props.getProperty("cvs.local.path");

      // To always have a clean set of a cvs repository, copy the structure in test-CVSROOT.tgz to
      // the same directory, but with "-tmp" added to it.

      Copy copy = new Copy();
      copy.setProject(new Project());

      FileSet set = new FileSet();
      set.setDir(new File(localPath));
      set.setIncludes("**/*");

      copy.addFileset(set);

      localCVSRepository = MyFileUtils.createTempDirectory();

      copy.setTodir(localCVSRepository);
      copy.execute();

      logger.debug("cvs.local.path = " + localPath);

      location = new CVSRepository("test-id-1");
      location.setProtocol(CVSRepository.LOCAL);
      location.setRepository(localCVSRepository.getPath());

      location.setWorkingContext(getWorkingContext());

    } catch (Exception e) {
      throw new InitializationException("Local CVS repository could not be initialized. Trying to initialize repository at : ".concat(localPath));
    }

  }

  /**
   * Deletes the temporary directory.
   */
  public void tearDown() {

    super.tearDown();

    try {
      FileUtils.deleteDirectory(localCVSRepository);
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Gets the <code>CVSRepository</code> that can be used for junit testing.
   */
  protected CVSRepository getTestLocation() {
    return location;
  }

  /**
   * Creates a randomly named test filename in the module directory (<code>TestModule</code>) for the test cvs
   * repository. Names are of the form <code>test_&lt;random-int&gt;</code>.
   */
  protected String getTestFileName() throws InitializationException {

    int someInt = randomizer.nextInt();
    someInt = (someInt < 0 ? someInt * -1 : someInt); // > 0

    return "test_" + someInt + ".nobody.txt";
  }

  /**
   * <p>Checks out {@link #DEFAULT_MODULE_1}, which can then be used to test against.
   */
  public final Module checkoutDefaultModule1() {

    try {
      Runner runner = getTestRunner();

      Module module = new SourceModule(DEFAULT_MODULE_1, location);
      module.setBaseDir(new File(getWorkingContext().getProjectBaseDirectory(), module.getName()));
      module.setCheckoutDir(getWorkingContext().getProjectBaseDirectory());

      runner.checkout(module);

      return module;

    } catch (Exception e) {
      fail(e.getMessage());
    }
    return null;
  }

  public final Module checkoutDefaultModuleWithVersion() {

    try {
      Runner runner = getTestRunner();

      Module module = new SourceModule(DEFAULT_MODULE_1, location, new Version("0-0"));
      module.setBaseDir(new File(getWorkingContext().getProjectBaseDirectory(), module.getName()));
      module.setCheckoutDir(getWorkingContext().getProjectBaseDirectory());

      runner.checkout(module);

      return module;

    } catch (Exception e) {
      fail(e.getMessage());
    }
    return null;
  }

  /**
   * Initializes a Runner for test purposes. 
   *
   * @return A Runner instance.
   * @throws CVSException When initializing the runner failed.
   */
  protected final Runner getTestRunner() throws CVSException, AuthenticationException {
    return getTestRunner(new CommandResponseFaker(null));
  }

  /**
   * Initializes a Runner for test purposes, with an optional CommandResponse.
   *
   * @param response
   * @return
   * @throws CVSException When initializing the runner failed.
   */
  protected final Runner getTestRunner(CommandResponse response) throws CVSException, AuthenticationException {

    CVSRunner runner = new CVSRunner(getTestLocation());

    if (response != null) {
      runner.setCommandResponse(response);
    }

    return runner;
  }

  /**
   * When this class is run (it is a test class), it won't bother you with 'no tests found'.
   */
  public void testNothing() {
    assertTrue(true);
  }
}