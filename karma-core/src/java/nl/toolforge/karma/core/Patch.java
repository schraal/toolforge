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
package nl.toolforge.karma.core;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class Patch extends Version {

  public static final String PATCH_PATTERN_POSTFIX = "-{1}\\d{1,2}";

  /**
   * Override for {@link Version.VERSION_PATTERN_STRING}. A patch can have one more digit.
   *
   * @see #PATCH_PATTERN_POSTFIX
   */
  public static final String VERSION_PATTERN_STRING = Version.VERSION_PATTERN_STRING + PATCH_PATTERN_POSTFIX;

  public static int INITIAL_PATCH = 1;

  /**
   * Patches have the following format : <code>0-0-x</code>, where x is the actual patch number within the
   * <code>0-0</code> version. The full thing has to be provided.
   *
   * @param patchNumber
   */
  public Patch(String patchNumber) {
    super(patchNumber);
  }

  public String getPatternString() {
    return VERSION_PATTERN_STRING;
  }

}
