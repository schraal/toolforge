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
package nl.toolforge.karma.core.vc.cvs;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>An object that can be passed to the CVSResponseAdapter that captures specific events during a
 * <code>cvs update</code> operation. This class can then be queried for specific entries matching specific criteria
 * (such as new files ('?'), etc.).
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class UpdateParser {

  private Set newFiles = new HashSet();

  public UpdateParser() {}

  /**
   * Returns a set of <code>File</code>s, mapping to new files (not yet in the CVS repository). These are
   * identified by <code>?</code>s at the beginning of an CVS output line after a <code>cvs update</code>.
   *
   * @return
   */
  public Set getNewFiles() {
    return newFiles;
  }

  public void addNewFile(String fileRef) {
    newFiles.add(new File(fileRef));
  }

  public void addNewFileByResponseLine(String responseLine) {
    // Parse the response line
    //
    addNewFile(responseLine);
  }
}
