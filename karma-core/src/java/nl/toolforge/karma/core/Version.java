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

  public static final String VERSION_PATTERN_STRING = "\\d{1,4}-\\d{1,4}";

  /** Separator for version digits. */
  public static final String VERSION_SEPARATOR_CHAR = "-";

  /** The initial version for a module. */
  public static final Version INITIAL_VERSION = new Version("0-0");

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

		this.versionNumber = versionIdentifier;

		StringTokenizer tokenizer = new StringTokenizer(versionIdentifier, "-");

		this.versionDigits = new int[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			this.versionDigits[i] = new Integer(tokenizer.nextToken()).intValue();
			i++;
		}
	}


  public String getPatternString() {
    return "\\d{1,4}-{1}\\d{1,4}";
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
			this.versionNumber += "-" + versionDigits[i];
		}
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public int getDigit(int index) {

		if (index > 2) {
			throw new IllegalArgumentException("Only three digits are supported.");
		}
		return versionDigits[index];
	}

	public int getLastDigit() {
		return versionDigits[versionDigits.length - 1];
	}

	public int getLastDigitIndex() {
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

	public int hashCode() {
		return versionNumber.hashCode();
	}

	public boolean equals(Object o) {
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
	public int compareTo(Object o) {

		Version that = (Version) o;

		double firstDigit = that.versionDigits[0] * 1000000000;
		double secondDigit = that.versionDigits[1] * 1000000;
		double thirdDigit = that.versionDigits[1] * 1000;

		double thatDouble = firstDigit + secondDigit + thirdDigit;

		firstDigit = this.versionDigits[0] * 1000000000;
		secondDigit = this.versionDigits[1] * 1000000;
		thirdDigit = this.versionDigits[1] * 1000;

		double thisDouble = firstDigit + secondDigit + thirdDigit;

		if (thisDouble < thatDouble) {
			return -1;
		} else if (thisDouble == thatDouble) {
			return 0;
		} else {
			return 1;
		}
	}

	public void setDigit(int index, int nextDigit) {
		versionDigits[index] = nextDigit;
		createVersionNumber();
	}

}
