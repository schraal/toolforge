package nl.toolforge.core.regexp;

/**
 * <p>An engine to match strings against patterns. See {@link Pattern} for more information.
 *
 * <p><b>Usage:</b>
 *
 * <pre>
 *   Pattern p = Pattern.compile("a*b");
 *   Matcher m = p.matcher(p, "aaaaab");
 *   boolean b = m.matches();
 * </pre>
 *
 * @author D.A. Smedes
 */
public final class Matcher
{
    private Pattern pattern = null;
    private String input = null;

    Matcher(Pattern pattern, String input) {
		this.pattern = pattern;
		this.input = input;
    }

	/**
	 * Checks if an input string matches a pattern.
	 *
	 * @return <code>true</code> if the input string matched the pattern, or <code>false</code> if it didn't.
	 */
    public boolean matches() {
		return pattern.getRE().match(input);
    }
}
