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
package nl.toolforge.karma.core.manifest.util;

import java.io.File;
import java.io.IOException;

import nl.toolforge.karma.core.test.BaseTest;
import nl.toolforge.core.util.file.MyFileUtils;

/**
 * @author W.H.Schraal
 */
public class TestFileTemplate extends BaseTest {

  public void testFileTemplateConstructor() {
    File tmpDir = null;
    try {
      tmpDir = MyFileUtils.createTempDirectory();
    } catch (IOException ioe) {
      fail("Could not create temporary directory needed for testing");
    }

    //test source dir is null
    FileTemplate fileTemplate;
    try {
      fileTemplate = new FileTemplate(null, null);
      fail("Expected an IllegalArgumentException, since the source was null.");
    } catch (IllegalArgumentException iae) {
      assertEquals(iae.getMessage(), FileTemplate.SOURCE_IS_NULL);
    }
    try {
      fileTemplate = new FileTemplate(null, new File("."));
      fail("Expected an IllegalArgumentException, since the source was null.");
    } catch (IllegalArgumentException iae) {
      assertEquals(iae.getMessage(), FileTemplate.SOURCE_IS_NULL);
    }

    //test target is null
    File file = new File(tmpDir, "file");
    try {
      file.createNewFile();
      fileTemplate = new FileTemplate(new File(tmpDir, "file"), null);
      fail("Expected an IllegalArgumentException, since the target is null.");
    } catch (IllegalArgumentException iae) {
      assertEquals(iae.getMessage(), FileTemplate.TARGET_IS_NULL);
    } catch (IOException ioe) {
      fail("Could not create temporary, existing file.");
    }

    //test all ok
    try {
      file.createNewFile();
      fileTemplate = new FileTemplate(new File(tmpDir, "file"), new File("."));
      assertTrue(true);
    } catch (IllegalArgumentException iae) {
      fail("Parameters should be ok.");
    } catch (IOException ioe) {
      fail("Could not create temporary, existing file.");
    }

    if (tmpDir != null && tmpDir.exists()) {
      tmpDir.delete();
    }
  }

}
