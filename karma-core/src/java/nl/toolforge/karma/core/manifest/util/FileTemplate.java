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

/**
 * Specifies the location of a template and the file where it should be copied to. These
 * locations need to be absolute paths.
 *
 * @author W.H. Schraal
 */
public class FileTemplate {

  static final String SOURCE_IS_NULL = "The source location of a template may not be null.";
  static final String TARGET_IS_NULL = "The target location of a template may not be null.";

  /** The relative location (including the file name) of the template. */
  private File source;
  /** The absolute target location (including file name) of the template. */
  private File target;

  /**
   * Create a FileTemplate object that specifies the source and target location
   * of a template. This information can be used to copy a template to a target location.
   *
   * @param source  Location of the template. May not be null.
   * @param target  Target location of the template, relative. May not be null.
   */
  public FileTemplate(File source, File target) {
    if (source == null) {
      throw new IllegalArgumentException(SOURCE_IS_NULL);
    }
    if (target == null) {
      throw new IllegalArgumentException(TARGET_IS_NULL);
    }
    this.source = source;
    this.target = target;
  }

  /**
   * Retrieve the source location of the template.
   * @return  Non-null and existing File
   */
  public File getSource() {
    return this.source;
  }

  /**
   * Retrieve the target location of the template.
   * @return  non-null, but not necessarily existing File.
   */
  public File getTarget() {
    return this.target;
  }

}
