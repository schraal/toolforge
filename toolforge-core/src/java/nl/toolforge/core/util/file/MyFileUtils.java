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

import org.apache.tools.ant.DirectoryScanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;


/**
 * File utilities. Stuff that is not implemented by <code>org.apache.commons.io.FileUtils</code>.
 *
 * @author D.A. Smedes
 * @author A. Mooy
 *
 * @version $Id:
 */
public final class MyFileUtils {

  /** Randomizer for use by createTempDirectory() */
  private static Random randomizer = new Random();

  /**
   * Creates a temporary directory with some random positive <code>long</code> as its name.
   *
   * @return A <code>File</code> reference to the temporary directory.
   *
   * @throws IOException When some IO error occurred.
   */
  public static File createTempDirectory() throws IOException {
    File tmp = new File(System.getProperty("java.io.tmpdir") + File.separator + Math.abs(randomizer.nextLong()));

    tmp.mkdirs();

    return tmp;
  }

  /**
   * Makes all files in <code>dir</code> and all its subdirectories writeable.
   *
   * @param dir Starting directory.
   */
  public static void makeWriteable(File dir, boolean recurse) throws IOException, InterruptedException {

    String osName = System.getProperty("os.name");
    String recursive;
    if (recurse) {
      recursive = "-R";
    } else {
      recursive = "";
    }
    String command = "";
    if (osName.toUpperCase().startsWith("WINDOWS")) {
      command = "cmd.exe /c attrib "+recursive+" " + dir + File.separator + "*.* /S /D";
    } else if (osName.toUpperCase().startsWith("LINUX")) {
      command = "chmod "+recursive+" -f u+w " + dir;
    } else {
      //all os-es other then Windows and Linux.
      command = "chmod "+recursive+" -f u+w " + dir;
    }
    Process proc = Runtime.getRuntime().exec(command);
    proc.waitFor();
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

  /**
   * Writes <code>fileRef</code> to <code>dir</code>. <code>fileRef</code> should be available in the classpath of
   * <code>classLoader</code>.
   *
   * @param dir         The directory to write to
   * @param fileRef     The original filename (path)
   * @param classLoader The class loader that contains <code>fileRef</code>
   *
   * @throws IOException
   */
//  public static void writeFile(File dir, File fileRef, ClassLoader classLoader) throws IOException {
//    writeFile(dir, fileRef, fileRef, classLoader);
//  }

  /**
   * Writes <code>fileRef</code> to <code>dir</code> as <code>newFileRef</code>. <code>fileRef</code> should be available in the classpath of
   * <code>classLoader</code>.
   *
   * @param dir         The directory to write to
   * @param fileRef     The original filename (path)
   * @param newFileName The new filename
   * @param classLoader The class loader that contains <code>fileRef</code>
   *
   * @throws IOException
   */
  public static void writeFile(File dir, File fileRef, File newFileName, ClassLoader classLoader) throws IOException {

    if (dir == null || fileRef == null || newFileName == null || "".equals(newFileName) || classLoader == null) {
      throw new NullPointerException("");
    }

    BufferedReader in =
        new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(fileRef.getPath())));

    dir.mkdirs();

    if (newFileName.getPath().lastIndexOf("/") > 0) {
      String subDir = newFileName.getPath().substring(0, newFileName.getPath().lastIndexOf("/"));
      new File(dir, subDir).mkdirs();
    }

    BufferedWriter out =
        new BufferedWriter(new FileWriter(new File(dir, newFileName.getPath())));

    String str;
    while ((str = in.readLine()) != null) {
      out.write(str);
    }
    out.close();
    in.close();
  }

}
