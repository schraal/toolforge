package nl.toolforge.core.regexp;

/**
 *
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class PatternSyntaxException
    extends IllegalArgumentException
{
    private String description = null;
    private String regex = null;
    private int index = -1;

    PatternSyntaxException(String description, String regex, int index) {
		this.description = description;
		this.regex = regex;
		this.index = index;
    }

    public String getMessage() {
		return description + "," + regex + ", at " + index;
    }
}
