package nl.toolforge.core.regexp;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

/**
 * Wrapper class for a regular expression service. This class wraps either the Jakarta <code>org.apache.regexp</code>
 * implementation or the Sun <code>java.util.regex</code> implementation.
 *
 * @author W.M. Oosterom
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class Pattern
{
	//private String regexp = null;
	private RE re = null;

	private Pattern(String regexp) {
		//this.regexp = regexp;

		try {
			re = new RE(regexp);
		} catch (RESyntaxException e) {
			throw new PatternSyntaxException(e.getMessage(), regexp, -1);
		}
	}

	/**
	 * <p>Compiles a regular expression. See {@link org.apache.regexp.RE} or <code>java.util.regex</code> (JDK1.4 or above)
	 * for more information.
	 *
	 * <p>This method might throw a <code>PatternSyntaxException</code>. This exception should only be caught and
	 * dealt with during development.
	 *
	 * @param regexp The regular expression string.
	 * @return A compiled pattern, to which pattern strings can be matched.
	 */
	public static Pattern compile(String regexp) {
		return new Pattern(regexp);
	}

	/**
	 * Jakarta implementation specific stuff.
	 */
	RE getRE() {
		return re;
	}

	/**
	 * Gets a matcher against which input strings can be (pattern) matched.
	 *
	 * @param input The input string that should be matched against the pattern.
	 *
	 * @return A <code>Matcher</code> engine.
	 */
	public Matcher matcher(String input) {
		return new Matcher(this, input);
	}
}

