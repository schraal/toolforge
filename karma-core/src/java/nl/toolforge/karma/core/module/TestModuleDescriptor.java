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
package nl.toolforge.karma.core.module;

import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationLoader;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.test.BaseTest;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class TestModuleDescriptor extends BaseTest {

  private LocationLoader locationFactory = null;

  public void setUp() {
    super.setUp();
    try {
      locationFactory = getWorkingContext().getLocationLoader();
    } catch (LocationException e) {
      fail(e.getMessage());
    }
  }

  public void testCreateFile() {

    File tmp = null;
    try {
      tmp = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      fail();
    }

    ModuleDescriptor d = null;
    try {
      SourceModule s = new SourceModule("A", locationFactory.get("test-id-1"));
      d = new ModuleDescriptor(s);

      d.createFile(tmp);

    } catch (LocationException e) {
      fail();
    } catch (IOException e) {
      fail();
    }

    try {
      FileUtils.deleteDirectory(tmp);
    } catch (IOException e) {
      fail();
    }

    if (tmp != null && tmp.exists()) {
      tmp.delete();
    }
  }
}
