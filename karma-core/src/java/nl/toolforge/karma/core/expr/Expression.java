package nl.toolforge.karma.core.expr;

/**
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public interface Expression {

	/**
	 * Retrieves the pattern string for a module in a manifest (@
	 *
	 * @return The <code>String</code> representation of the pattern for a <code>Module</code> name.
	 */
	public String getPatternString();
}
