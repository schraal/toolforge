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

import nl.toolforge.karma.core.Patch;
import nl.toolforge.karma.core.Version;

/**
 * A <code>PatchLine</code> is a special type of <code>DevelopmentLine</code>, used when a module has been released to
 * (for example) the test department. NIET_WEG.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class PatchLine extends DevelopmentLine {

  /**
   * Name prefix for the symbolic name that is applied to the module when a patchline is created. A symbolic name
   * would look like this : <code>PATCHLINE|v_0-2</code>, indicating that a patchline is created for
   * version <code>0-2</code>.
   */
  public static final String NAME_PREFIX = "PATCHLINE";

  public static final String VERSION_SEPARATOR_PATTERN = "\\|v_";
  public static final String VERSION_SEPARATOR = "|v_";

  public static final String PATCH_SEPARATOR_PATTERN = "\\|p_";
  public static final String PATCH_SEPARATOR = "|p_";

  private Version version = null;

  /**
   * Creates a PatchLine for version <code>version</code>.
   *
   * @param version The version for which a PatchLine must be created.
   */
  public PatchLine(Version version) {
    super(NAME_PREFIX + VERSION_SEPARATOR + version.getVersionNumber());

    this.version = version;
  }

  public String getPatternString() {
    return NAME_PREFIX + VERSION_SEPARATOR_PATTERN + Version.VERSION_PATTERN_STRING;
  }

//  public String getSymbolicName() {
//    return NAME_PREFIX + PATCH_SEPARATOR + version.getVersionNumber();
//  }

  /**
   * Given the patch line for a module, a matching pattern is required to select the corresponding patch versions. A
   * patch line <code>PATCHLINE|v_0-0</code> will generate patch versions like <code>PATCHLINE|p_0-0-0</code>,
   * <code>PATCHLINE|p_0-0-1</code> etc. This method will then return the following pattern string :
   * <code>PATCHLINE|p_0-0-\d{1,4}</code>
   *
   * @return See the method description.
   */
  public String getMatchingPattern() {
    // Something like should be returned, where 0-0 is the version to which the patch applies: PATCHLINE|p_0-0-{1}\\d{1,2}
    return NAME_PREFIX + PATCH_SEPARATOR_PATTERN + version.getVersionNumber() + Patch.PATCH_PATTERN_POSTFIX;
  }
}
