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
package nl.toolforge.karma.core.cmd.util;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents the path to a dependency. Consists of two parts, a relative part
 * and the part that forms the prefix of this relative part. Together they form
 * the absolute path to the dependency.
 *
 * @author W.H. Schraal
 */
public class DependencyPath {

  private File pathPrefix;
  private File relativePath;

  /**
   * Create a new DependencyPath
   *
   * @param pathPrefix    Prefix for the dependency path.
   * @param relativePath  Relative path to the dependency.
   */
  DependencyPath(File pathPrefix, File relativePath) {
    this.pathPrefix = pathPrefix;
    this.relativePath = relativePath;
  }

  public File getPathPrefix() {
    return pathPrefix;
  }

  public File getRelativePath() {
    return relativePath;
  }

  /**
   * Retrieve the full absolute path to the dependency by concatenating the
   * prefix and the relative path.
   *
   * @return  The full path to the dependency.
   */
  public File getFullPath() {
    return new File(getPathPrefix(), getRelativePath().getPath());
  }

  /**
   * Does the dependency exist?
   *
   * @return  Whether or not the dependency exists.
   */
  public boolean exists() {
    return getFullPath().exists();
  }

  /**
   * Concatenates all DependencyPaths in the given set to a separated String.
   *
   * @param dependencyPaths  Set of DependencyPath Objects.
   * @param relative         Use relative paths?
   * @param separator        Separator char to use.
   * @return Separated String.
   */
  public static String concat(Set dependencyPaths, boolean relative, char separator) {
    String s = "";
    Iterator it = dependencyPaths.iterator();
    while (it.hasNext()) {
      DependencyPath path = (DependencyPath) it.next();
      if (relative) {
        s += path.getRelativePath().getPath();
      } else {
        s += path.getFullPath().getPath();
      }
      if (it.hasNext()) {
        s += separator;
      }
    }
    return s;
  }
  
}
