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
