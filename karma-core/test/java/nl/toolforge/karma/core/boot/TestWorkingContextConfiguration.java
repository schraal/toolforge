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
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestWorkingContextConfiguration extends TestCase {

  public void testConstructor() {

    try {
      new WorkingContextConfiguration(null);
      fail("Should have thrown an `IllegalArgumentException`");
    } catch (Exception e) { }

    assertNotNull(new WorkingContextConfiguration(new WorkingContext("blaat")));
  }

  /**
   * Tests the following methods implicitly:
   *
   * <ul>
   *   <li>{@link WorkingContextConfiguration#getProperty(String)}</li>
   *   <li>{@link WorkingContextConfiguration#getLocationStore()}</li>
   *   <li>{@link WorkingContextConfiguration#getManifestStore()}</li>
   * </ul>
   */
  public void testLoad() {

    File tmp = null;
    try {
      tmp = MyFileUtils.createTempDirectory();

      WorkingContext ctx = new WorkingContext("blaat", tmp);

      WorkingContextConfiguration config = new WorkingContextConfiguration(ctx);

      MyFileUtils.writeFile(
          WorkingContext.getConfigurationBaseDir(),
          new File("test/authenticators.xml"),
          new File("authenticators.xml"),
          this.getClass().getClassLoader());

      MyFileUtils.writeFile(
          ctx.getWorkingContextConfigurationBaseDir(),
          new File("test/test-working-context.xml"),
          new File("working-context.xml"),
          this.getClass().getClassLoader());

      boolean b = config.load();
      assertTrue(b);

      assertEquals(config.getProperty(WorkingContext.PROJECT_BASE_DIRECTORY_PROPERTY), "/tmp/");
      assertEquals(config.getProperty(WorkingContext.PROJECT_LOCAL_REPOSITORY_PROPERTY), "/home/asmedes/.repository");

      assertNotNull(config.getLocationStore());
      assertNotNull(config.getManifestStore());

      FileUtils.deleteDirectory(tmp);
    } catch (IOException e) {
      fail(e.getMessage());
    } catch (WorkingContextException e) {
      fail(e.getMessage());
    }
  }
}
