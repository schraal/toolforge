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
}
