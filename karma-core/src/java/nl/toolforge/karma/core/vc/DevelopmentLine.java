package nl.toolforge.karma.core.vc;

import nl.toolforge.karma.core.Version;

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

    if (lineName == null || !lineName.matches(DEVELOPMENT_LINE_PATTERN_STRING)) {
      throw new PatternSyntaxException("Pattern mismatch for version. Should match " + DEVELOPMENT_LINE_PATTERN_STRING, lineName, -1);
    }
		this.lineName = lineName;
	}

	public String getName() {
		return lineName;
	}

  public int hashCode() {
		return lineName.hashCode();
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
