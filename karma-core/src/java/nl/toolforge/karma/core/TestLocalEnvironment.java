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
package nl.toolforge.karma.core;

import junit.framework.TestCase;
import nl.toolforge.core.util.file.MyFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

/**
 * @version $Id$
 */
public final class TestLocalEnvironment extends TestCase {

  private static File workingContext;
  private static Properties p;
  private static LocalEnvironment localEnvironment;

  public void setUp() {
    try {
      workingContext = MyFileUtils.createTempDirectory();

      p = new Properties();
      p.put(LocalEnvironment.WORKING_CONTEXT_DIRECTORY, workingContext.getPath());
      p.put(LocalEnvironment.MANIFEST_STORE_HOST, "one");
      p.put(LocalEnvironment.MANIFEST_STORE_REPOSITORY, "two");
      p.put(LocalEnvironment.MANIFEST_STORE_PROTOCOL, "three");
      p.put(LocalEnvironment.LOCATION_STORE_HOST, "one");
      p.put(LocalEnvironment.LOCATION_STORE_REPOSITORY, "two");
      p.put(LocalEnvironment.LOCATION_STORE_PROTOCOL, "three");

      try {
        LocalEnvironment.initialize(p);
      } catch(KarmaException k) {
        fail(k.getMessage());
      }

    } catch (IOException e) {
      fail(e.getMessage());
    }
  }

  public void testGetWorkingContext() {
    assertEquals(workingContext, LocalEnvironment.getWorkingContext());
  }

  public void testGetDevelopmentHome() {
    assertEquals(new File(workingContext, "projects"), LocalEnvironment.getDevelopmentHome());
  }
  
  public void testGetManifestStore() {
    assertEquals(new File(workingContext, "manifests"), LocalEnvironment.getManifestStore());
  }

  public void testGetLocationStore() {
    assertEquals(new File(workingContext, "locations"), LocalEnvironment.getLocationStore());
  }

  /**
   * Leave as last ...
   */

  public void tearDown() {
    try {
      FileUtils.deleteDirectory(workingContext);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
