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
package nl.toolforge.karma.core.boot;

import junit.framework.TestCase;
import nl.toolforge.core.util.file.MyFileUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @version $Id$
 */
public final class TestWorkingContext extends TestCase {

  private File dotKarma;

  public void setUp() {
    try {
      dotKarma = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  public void testConstructor() {
    WorkingContext ctx = new WorkingContext("blaat", dotKarma);
    assertNotNull(ctx);
  }

  public void testConstructor2() {
    WorkingContext ctx = new WorkingContext("bb00*", dotKarma);
    assertTrue(WorkingContext.DEFAULT.equals(ctx.getName()));

    ctx = new WorkingContext("b", dotKarma);
    assertFalse(WorkingContext.DEFAULT.equals(ctx.getName()));

    ctx = new WorkingContext("", dotKarma);
    assertTrue(WorkingContext.DEFAULT.equals(ctx.getName()));
  }

  public void testStaticStuff() {
    System.setProperty("karma.home", "/tmp");
    assertEquals(new File("/tmp"), WorkingContext.getKarmaHome());
  }

  public void testDefaultDirectories() {

    WorkingContext ctx = new WorkingContext("aaa", dotKarma);
    assertEquals(new File(dotKarma, "working-contexts" + File.separator + "aaa"), ctx.getWorkingContextConfigurationBaseDir());
    assertTrue(ctx.getWorkingContextConfigurationBaseDir().exists());
  }

  public void testConfigure() {

    File tmp = null;
    try {
      tmp = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      fail(e.getMessage());
    }

    WorkingContext workingContext = new WorkingContext("bbb", dotKarma);

    try {
      MyFileUtils.writeFile(
          tmp,
          new File("test/test-working-context.xml"),
          new File(workingContext.getName() + File.separator + "working-context.xml"),
          this.getClass().getClassLoader());
    } catch (IOException e) {
      fail(e.getMessage());
    }

    WorkingContextConfiguration config = new WorkingContextConfiguration(workingContext);
    workingContext.configure(config);

    // Retrieve the configuration and change it a bit for testing purposes.
    //
    config.setProperty(WorkingContext.PROJECT_BASE_DIRECTORY_PROPERTY, tmp.getPath());

    assertEquals(config.getProperty(WorkingContext.PROJECT_BASE_DIRECTORY_PROPERTY), tmp.getPath());
    assertEquals(tmp, workingContext.getProjectBaseDirectory());
    assertEquals(new File(tmp, ".admin"), workingContext.getAdminDir());
    assertEquals(new File(dotKarma, "working-contexts" + File.separator + "bbb"), workingContext.getWorkingContextConfigurationBaseDir());

    try {
      FileUtils.deleteDirectory(tmp);
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Leave as last ...
   */

  public void tearDown() {
    try {
      FileUtils.deleteDirectory(dotKarma);
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
