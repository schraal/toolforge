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
import java.util.Properties;
import java.util.List;

/**
 * @version $Id$
 */
public final class TestWorkingContext extends TestCase {

  private File dotKarma, projectsDir;
  private Properties p;

  public void setUp() {
    try {
      dotKarma = MyFileUtils.createTempDirectory();
      projectsDir = MyFileUtils.createTempDirectory();

      p = new Properties();

      p.put(WorkingContext.MANIFEST_STORE_PROTOCOL, "local");
      p.put(WorkingContext.MANIFEST_STORE_REPOSITORY, "two");
//      p.put(WorkingContext.MANIFEST_STORE_HOST, "one");
//      p.put(WorkingContext.MANIFEST_STORE_PORT, "four");
      p.put(WorkingContext.MANIFEST_STORE_USERNAME, "five");

      p.put(WorkingContext.LOCATION_STORE_PROTOCOL, "pserver");
      p.put(WorkingContext.LOCATION_STORE_HOST, "one");
      p.put(WorkingContext.LOCATION_STORE_REPOSITORY, "two");
      p.put(WorkingContext.LOCATION_STORE_PORT, "four");
      p.put(WorkingContext.LOCATION_STORE_USERNAME, "five");

    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  public void testConstructor() {

    WorkingContext ctx = new WorkingContext("blaat", dotKarma, projectsDir, p);

    assertEquals(2, ctx.getInvalidConfiguration().size());

    assertEquals(0, ((List) ctx.getInvalidConfiguration().get("MANIFEST-STORE")).size());
    assertEquals(0, ((List) ctx.getInvalidConfiguration().get("LOCATION-STORE")).size());
  }

  public void testDirectories() {

    WorkingContext ctx = new WorkingContext("blaat", dotKarma, projectsDir, p);

    assertEquals(dotKarma, ctx.getConfigurationBaseDir());
    assertEquals(new File(dotKarma, "blaat"), ctx.getWorkingContextConfigDir());
    assertEquals(new File(dotKarma, "blaat/workingcontext.properties"), ctx.getConfigurationFile());
    assertEquals(new File(dotKarma, "blaat"), ctx.getWorkingContextConfigDir());

    assertEquals(projectsDir, ctx.getProjectBaseDirectory());
    assertEquals(new File(projectsDir, "blaat"), ctx.getWorkingContextProjectDir());
    assertEquals(new File(projectsDir, "blaat/manifests"), ctx.getManifestStore());
    assertEquals(new File(projectsDir, "blaat/locations"), ctx.getLocationStore());
    assertEquals(new File(projectsDir, "blaat/projects"), ctx.getDevelopmentHome());
  }

  /**
   * Leave as last ...
   */

  public void tearDown() {
    try {
      FileUtils.deleteDirectory(dotKarma);
      FileUtils.deleteDirectory(projectsDir);
    } catch (IOException e) {
      fail(e.getMessage());
    }
  }
}