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
package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.KarmaRuntimeException;

import java.util.regex.PatternSyntaxException;

/**
 * A development line is a separate line of development for a module, generally implemented by a version control
 * system through a branch.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class DevelopmentLine {

  public static final String DEVELOPMENT_LINE_PATTERN_STRING = "[A-Za-z-]+[A-Z0-9a-z-]*";

  private String lineName = null;

  /**
   * Constructor for a development line. <code>lineName</code> should match {@link DEVELOPMENT_LINE_PATTERN_STRING}.
   *
   * @param lineName The name for a development line
   */
  public DevelopmentLine(String lineName) {

    if (lineName == null || !lineName.matches(getPatternString())) {
      throw new PatternSyntaxException("Pattern mismatch for version. Should match " + getPatternString(), lineName, -1);
    }
    this.lineName = lineName;
  }

  public String getName() {

    if (lineName == null) {
      throw new KarmaRuntimeException("Line name has not been set.");
    }
    return lineName;
  }

  public int hashCode() {
    return lineName.hashCode();
  }

  public String getPatternString() {
    return DEVELOPMENT_LINE_PATTERN_STRING;
  }

  /**
   * Compares two DevelopmentLine instance for equality. Two instances are equal when their names are the same.
   *
   * @param o Object (of type DevelopmentLine)
   */
  public boolean equals(Object o) {
    if (!(o instanceof DevelopmentLine)) {
      return false;
    }
    return ((DevelopmentLine) o).lineName.equals(this.lineName);
  }

}
