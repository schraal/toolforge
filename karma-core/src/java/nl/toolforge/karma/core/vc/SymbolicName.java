package nl.toolforge.karma.core.vc;

/**
 *
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public interface SymbolicName {

	/**
	 * Checks if the symbolic name is sticky, i.e.
	 *
	 * @return
	 */
	public boolean isSticky();

	public boolean isBranch();

	/**
	 * Returns a <code>String</code> representation of this symbolic name.
	 * @return
	 */
	public String getSymbolicName();
}
