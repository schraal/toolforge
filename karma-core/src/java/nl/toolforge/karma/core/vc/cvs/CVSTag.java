package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.vc.SymbolicName;

/**
 * Symbolic names in CVS are implemented in two ways: 'sticky-tag' and 'branch'.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class CVSTag implements SymbolicName {

  private String symbolicName = null;

	/**
	 * Creates a CVS tag with <code>symbolicName</code>.
	 */
	public CVSTag(String symbolicName) {

		// TODO parse through the SymbolicNameParser
		//
		this.symbolicName = symbolicName;
	}

	public CVSTag(String symbolicName, boolean isBranch) {
		this.symbolicName = symbolicName;
	}

	public boolean isBranch() {
		return false;
	}

	/**
	 * Negation of {@link #isBranch}.
	 *
	 * @return <code>true</code> if this symbolic name is 'sticky' (not a branch tag).
	 */
	public boolean isSticky() {
		return !isBranch();
	}
}