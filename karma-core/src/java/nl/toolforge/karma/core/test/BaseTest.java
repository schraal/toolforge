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

import junit.framework.TestCase;
import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.boot.WorkingContextConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * This testclass is highly recommended when writing JUnit testclasses for Karma. It initializes some basic stuff. Just
 * check this implementation to see how it may help.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class BaseTest extends TestCase {

  private File wcBaseDir = null;
  private File projectBaseDir = null;
  private File localRepo = null;
  private WorkingContext ctx = null;

  public void setUp() {

    // The following is required to allow the Preferences class to use the test-classpath
    //
    System.setProperty("TESTMODE", "true"); //??
    System.setProperty("locale", "en");

    try {
      wcBaseDir = MyFileUtils.createTempDirectory();
      projectBaseDir = MyFileUtils.createTempDirectory();
      localRepo = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      fail(e.getMessage());
    }

    try {

      ctx = new WorkingContext("test", wcBaseDir);

      try {
        MyFileUtils.writeFile(
            ctx.getWorkingContextConfigurationBaseDir(),
            new File("test/test-working-context.xml"),
            "working-context.xml",
            this.getClass().getClassLoader());
        ctx.init();
      } catch (IOException e) {
        fail(e.getMessage());
      }

      ctx.init();

      WorkingContextConfiguration config = ctx.getConfiguration();

      config.setProperty(WorkingContext.PROJECT_BASE_DIRECTORY_PROPERTY, projectBaseDir.getPath());

      MyFileUtils.writeFile(WorkingContext.getConfigurationBaseDir(), new File("test/authenticators.xml"), this.getClassLoader());

      MyFileUtils.writeFile(ctx.getLocationStore(), new File("test/test-locations.xml"), this.getClassLoader());
      MyFileUtils.writeFile(ctx.getLocationStore(), new File("test/test-locations-2.xml"), this.getClassLoader());

      MyFileUtils.writeFile(ctx.getManifestStore(), new File("test/test-manifest-1.xml"), this.getClassLoader());
      MyFileUtils.writeFile(ctx.getManifestStore(), new File("test/test-manifest-2.xml"), this.getClassLoader());
      MyFileUtils.writeFile(ctx.getManifestStore(), new File("test/test-manifest-3.xml"), this.getClassLoader());

      MyFileUtils.writeFile(ctx.getManifestStore(), new File("test/included-test-manifest-1.xml"), this.getClassLoader());
      MyFileUtils.writeFile(ctx.getManifestStore(), new File("test/included-test-manifest-2.xml"), this.getClassLoader());


    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  public WorkingContext getWorkingContext() {
    return ctx;
  }

  public void tearDown() {
    try {
      MyFileUtils.makeWriteable(wcBaseDir);
      MyFileUtils.makeWriteable(projectBaseDir);
      MyFileUtils.makeWriteable(localRepo);

      FileUtils.deleteDirectory(wcBaseDir);
      FileUtils.deleteDirectory(projectBaseDir);
      FileUtils.deleteDirectory(localRepo);
    } catch (IOException e) {
      fail(e.getMessage());
    }
    catch (InterruptedException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Helper method to retrieve the this class' classloader.
   */
  public ClassLoader getClassLoader() {
    return this.getClass().getClassLoader();
  }

  /**
   * When this class is run (it is a test class), it won't bother you with 'no tests found'.
   */
  public void testNothing() {
    assertTrue(true);
  }
}
