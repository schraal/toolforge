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

import java.util.StringTokenizer;
import java.util.regex.PatternSyntaxException;

/**
 * A <code>Version</code> is the container object for the <code>version</code> attribute of a module. The implementation
 * is independent of the version control system.
 *
 * @author D.A. Smedes
 * @version $Id$ 
 */
public class Version implements Comparable {

  public static String VERSION_PATTERN_STRING = "\\d{1,2}-\\d{1,2}";

  /** Separator for version digits. */
  public static final String VERSION_SEPARATOR_CHAR = "-";

  /** The initial version for a module. */
  public static Version INITIAL_VERSION = new Version("0-0");

  public static final int FIRST_DIGIT = 0;
  public static final int SECOND_DIGIT = 1;
  public static final int THIRD_DIGIT = 2;

  private String versionNumber = null;

  private int[] versionDigits = null;

  /**
   * Constructs a version number using the <code>versionIdentifier</code> parameter.
   *
   * @param versionIdentifier
   */
  public Version(String versionIdentifier) {

    if (!versionIdentifier.matches(getPatternString())) {
      throw new PatternSyntaxException(
          "Pattern mismatch for version. Should match " + getPatternString(), versionIdentifier, -1);
    }


    StringTokenizer tokenizer = new StringTokenizer(versionIdentifier, VERSION_SEPARATOR_CHAR);

    versionDigits = new int[tokenizer.countTokens()];
    int i = 0;
    while (tokenizer.hasMoreTokens()) {
      versionDigits[i] = new Integer(tokenizer.nextToken()).intValue();
      i++;
    }

    createVersionNumber();
  }

  public String getPatternString() {
    return VERSION_PATTERN_STRING;
  }

  /**
   * Returns the initial version for a module. Right now, this is implemented as <code>new Version("0-0")</code>.
   *
   * @return
   */
  protected static Version getInitialVersion() {
    return new Version("0-0");
  }

  /**
   * Creates a <code>Patch</code> based on this version.
   *
   * @param patchNumber
   * @return
   */
  public Patch createPatch(String patchNumber) {
    return new Patch(getVersionNumber() + VERSION_SEPARATOR_CHAR + patchNumber);
  }

  /**
   * Constructs a version number concatenating each item in <code>versionDigits</code>, using the <code>'-'</code>
   * separator. <code>{1, 0, 9}</code> translates into a version number <code>1-0-9</code>.
   *
   * @param versionDigits An array, containing all version components (maximum of three is allowed).
   */
  public Version(int[] versionDigits) {

    if (versionDigits.length < 2) {
      throw new IllegalArgumentException(
          "Pattern mismatch for version. Should match " + getPatternString() +
          "; provide 'int'-array");
    }

    StringBuffer versionStringBuffer = new StringBuffer();
    for (int i = 0; i < versionDigits.length; i++) {
      versionStringBuffer.append(versionDigits[i]);
      if (i < versionDigits.length - 1);
    }

    this.versionDigits = versionDigits;

    createVersionNumber();
  }

  private void createVersionNumber() {

    // For the time being, let it go ... if a runtime occurs, fine, then I know I have to do something.
    //
    this.versionNumber = "" + versionDigits[0];
    for (int i = 1; i < versionDigits.length; i++) {
      this.versionNumber += VERSION_SEPARATOR_CHAR + versionDigits[i];
    }
  }

  public String getVersionNumber() {
    return versionNumber;
  }

  private int getLastDigit() {
    return versionDigits[versionDigits.length - 1];
  }

  private int getLastDigitIndex() {
    return versionDigits.length - 1;
  }

  /**
   * Gets the string representation of this version.
   *
   * @return A string representation of this version.
   */
  public String toString() {
    return versionNumber;
  }

  public final int hashCode() {
    return versionNumber.hashCode();
  }

  public final boolean equals(Object o) {
    if (!(o instanceof Version)) {
      return false;
    }

    return ((Version) o).versionNumber.equals(this.versionNumber);
  }

  /**
   * Compares two <code>Version</code> instances.
   *
   * @param o The other <code>Version</code> instance to match against.
   * @return Returns <code>-1</code> when <code>this < o</code>, <code>0</code> when
   *         <code>this == o</code> or <code>1</code> when <code>this > o</code>.
   */
  public final int compareTo(Object o) {

    int[] zis = versionDigits;
    int[] zat = ((Version) o).versionDigits;

    if (zis[0] < zat[0]) {
      return -1;
    } else if (zis[0] == zat[0]) {
      if (zis[1] < zat[1]) {
        return -1;
      } else if (zis[1] == zat[1]) {
        if (o instanceof  Patch) {
          // We also have a third digit to take into account.
          //
          if (zis[2] < zat[2]) {
            return -1;
          } else if (zis[2] == zat[2]) {
            return 0;
          } else {
            return 1;
          }
        }
        return 0;
      } else {
        return 1;
      }
    } else {
      return 1;
    }
  }

  public void setDigit(int index, int nextDigit) {
    versionDigits[index] = nextDigit;
    createVersionNumber();
  }

  public final boolean isLowerThan(Version version) {
    return this.compareTo(version) == -1;
  }

  public final boolean isHigherThan(Version version) {
    return this.compareTo(version) == 1;
  }

  /**
   * Increases this versions' last digit by 1.
   */
  public final void increase() {
    setDigit(getLastDigitIndex(), getLastDigit() + 1);
  }
}
