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

import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.vc.AuthenticationException;
import nl.toolforge.karma.core.vc.Authenticator;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.VersionControlSystem;
import org.apache.tools.ant.DirectoryScanner;

import java.io.File;

/**
 * <code>AdminModule</code>s are suitable for module-like structures which are not used by any <code>Manifest</code>. A
 * good example is a module that stores manifests itself.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class AdminModule implements Module {

  private String moduleName = null;
  private Location location = null;

  private File baseDir = null;
  private File checkoutDir = null;

  public AdminModule(String moduleName, Location location) {
    this.moduleName = moduleName;
    this.location = location;
  }

  public final String getName() {
    return moduleName;
  }

  /**
   * Gets all file entries in this module. An entry is relative to a modules' base directory. Directories are ignored.
   *
   * @param includePatterns A String array containing the file patterns to include in the output array.
   */
  public final String[] getEntries(String[] includePatterns) {

    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(getBaseDir());
    scanner.setIncludes(includePatterns);

    return scanner.getIncludedFiles();
  }

  /**
   * Checks is
   * @return
   */
  public final boolean isVersionControlled() {
    return location instanceof VersionControlSystem;
  }

  public void setBaseDir(File baseDir) {
    this.baseDir = baseDir;
  }

  public File getBaseDir() {
    return baseDir;
  }
//
//  public void setCheckoutDir(File checkoutDir) {
//    this.checkoutDir = checkoutDir;
//  }
//
//  public File getCheckoutDir() {
//    return checkoutDir;
//  }

  /**
   * Returns the location for this
   * @return
   */
  public Location getLocation() {
    return location;
  }

  public abstract void createRemote(Authenticator authenticator, String createComment) throws VersionControlException, AuthenticationException;
}
