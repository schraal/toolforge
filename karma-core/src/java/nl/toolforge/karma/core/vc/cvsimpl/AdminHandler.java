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
package nl.toolforge.karma.core.vc.cvsimpl;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.manifest.Module;
import org.apache.tools.ant.DirectoryScanner;
import org.netbeans.lib.cvsclient.CVSRoot;
import org.netbeans.lib.cvsclient.admin.Entry;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Helper class to execute stuff on admin files in <code>CVS</code> directories on behalf of <code>module</code>.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class AdminHandler {

  // todo this class should be made into n interface and Module should get a method to retrieve this sort of info
  // todo subversion impl should be made.

  private Module module = null;

  private List newStuff = new ArrayList();
  private List changedStuff = new ArrayList();
  private List removedStuff = new ArrayList();

  private FilenameFilter filter = new FilenameFilter() {

    public boolean accept(File dir, String name) {
      return !name.matches(".WORKING|.DYNAMIC|.STATIC|.module.info|CVS|.cvsignore") && name.matches("[^\\~]+");
    }
  };

  public AdminHandler(Module module) {
    this.module = module;
  }

  /**
   * This method should be called to administrate the module to which this AdminHandler applies. This method will
   * determine which files and directories are new, changed or removed, but uncommitted.
   */
  public void administrate() {

    // Need all CVS dirs to be able to scan Entries files.
    //
    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(module.getBaseDir());
    scanner.setExcludes(new String[]{"**/CVS"});
    scanner.scan();
    String[] moduleDirectories = scanner.getIncludedDirectories();

    for (int i = 0; i < moduleDirectories.length; i++) {

      StandardAdminHandler h = new StandardAdminHandler();
      try {

        // All of a directories' entries.
        //
        Entry[] dirEntries = h.getEntriesAsArray(new File(module.getBaseDir(), moduleDirectories[i]));
        List entryNames = getEntryNames(dirEntries);

        // All files and directories in this directory, regardless of them being CVS managed.
        //
        List dirFiles = Arrays.asList(new File(module.getBaseDir(), moduleDirectories[i]).list(filter));

        // Pass 1 : locate all new files and directories
        //
        for (Iterator k = dirFiles.iterator(); k.hasNext();) {
          String item = (String) k.next();
          if (!entryNames.contains(item)) {
            if (moduleDirectories[i].equals("")) {
              newStuff.add(item);
            } else {
              newStuff.add(new File(moduleDirectories[i], item).getPath());
            }
          }
        }

        // Pass 2 : locate all changed or removed files
        //
        for (int j = 0; j < dirEntries.length; j++) {

          Entry entry = dirEntries[j];
          File fileEntry = new File(module.getBaseDir(), entry.getName());
          if (entry.isUserFileToBeRemoved()) {
            if (moduleDirectories[i].equals("")) {
              removedStuff.add(entry.getName());
            } else {
              removedStuff.add(new File(moduleDirectories[i], entry.getName()).getPath());
            }
          } else if (fileEntry.isFile() && (entry.getLastModified() == null ? 0 : entry.getLastModified().getTime()) != fileEntry.lastModified()) {
            if (moduleDirectories[i].equals("")) {
              changedStuff.add(entry.getName());
            } else {
              changedStuff.add(new File(moduleDirectories[i], entry.getName()).getPath());
            }
          }
//          String s = (moduleDirectories[i].equals("") ? entry.getName() : new File(moduleDirectories[i], entry.getName()).getPath());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private List getEntryNames(Entry[] entries) {
    List entryList = new ArrayList();
    for (int j = 0; j < entries.length; j++) {
      entryList.add(entries[j].getName());
    }
    return entryList;
  }

  /**
   * Returns a <code>List</code> of <code>File</code> instances indicating files and directories that are not yet
   * version managed by CVS.
   *
   * @see #administrate()
   */
  public List getNewStuff() {
    return newStuff;
  }

  public boolean hasNewStuff() {
    return newStuff.size() > 0;
  }

  /**
   * Returns a <code>List</code> of <code>File</code> instances indicating files that have been changed, but not yet
   * committed.
   *
   * @see #administrate()
   */
  public List getChangedStuff() {
    return changedStuff;
  }

  public boolean hasChangedStuff() {
    return changedStuff.size() > 0;
  }

  /**
   * Returns a <code>List</code> of <code>File</code> instances indicating files that have been removed, but not yet
   * committed.
   *
   * @see #administrate()
   */
  public List getRemovedStuff() {
    return removedStuff;
  }

  public boolean hasRemovedStuff() {
    return removedStuff.size() > 0;
  }


  /**
   * Checks if the module was previously checked out from the same location as is stored in <code>CVS/Root</code>.
   *
   * @return A check is done of the CVSROOT. If they are equal, this method returns <code>true</code>.
   */
  public boolean isEqualLocation() {

    if (!module.getBaseDir().exists()) {
      // if the module has not been checked out, it is OK!
      //
      return true;
    }

    File rootFile = new File(module.getBaseDir(), "CVS/Root");

    String cvsRootString = null;
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(rootFile)));

      cvsRootString = in.readLine();
      in.close();

    } catch (FileNotFoundException e) {
      // We guess the user has created a module and not stored in a version control repository.
      //
      return true;
    } catch (IOException e) {
      throw new KarmaRuntimeException(e.getMessage());
    }

    CVSRoot cvsRoot = CVSRoot.parse(cvsRootString);

    CVSRepository loc = (CVSRepository) module.getLocation();
    try {
      return cvsRoot.toString().equals(loc.getCVSRoot());
    } catch (CVSException e) {
      return true;
    }
  }
}
