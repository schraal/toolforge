/*
Toolforge core - Core of the Toolforge application suite
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
package nl.toolforge.core.util.file;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;


/**
 * File utilities. Stuff that is not implemented by <code>org.apache.commons.io.FileUtils</code>.
 *
 * @author D.A. Smedes
 *
 * @version $Id:
 */
public final class MyFileUtils {

  /**
   * Creates a temporary directory with some random positive <code>int</code> as its name.
   *
   * @return A <code>File</code> reference to the temporary directory.
   *
   * @throws IOException When some IO error occurred.
   */
  public static File createTempDirectory() throws IOException {

    Random randomizer = new Random();

    int someInt = randomizer.nextInt();
    someInt = (someInt< 0 ? someInt * -1 : someInt); // > 0

    File tmp = File.createTempFile("" + someInt, null);
    tmp.delete();
    tmp.mkdir();

    return tmp;
  }

  /**
   * Makes all files in <code>dir</code> and all its subdirectories writeable.
   *
   * @param dir Starting directory.
   */
  public static void makeWriteable(File dir) {

    String osName = System.getProperty("os.name");
    try {
      if (osName.equalsIgnoreCase("WINDOWS NT") || osName.equals("Windows 2000")) {
        Runtime.getRuntime().exec("cmd.exe /c attrib -r *.*");
      } else {
        Process Proc = Runtime.getRuntime().exec("chmod -R u+w " + dir + File.separator);
      }
//      Thread.currentThread().sleep(100);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Makes all files in <code>dir</code> and all its subdirectories read-only. Uses the Ant
   * <code>DirectoryScanner</code>.
   *
   * @param dir Starting directory.
   */
  public static void makeReadOnly(File dir) {

    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(dir);
    scanner.setIncludes(new String[]{"**/*"});
    scanner.scan();

    String[] files = scanner.getIncludedFiles();
    String[] dirs = scanner.getIncludedDirectories();

    for (int i = 0; i < files.length; i++) {
      new File(dir, files[i]).setReadOnly();
    }
    for (int i = 0; i < dirs.length; i++) {
      new File(dir, dirs[i]).setReadOnly();
    }
  }
}
