package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.vc.SymbolicName;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.Module;

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

//	/**
//	 * Constructs a CVS tag, based on the <code>version</code> supplied.
//	 *
//	 * @param version A <code>Version</code> instance, which represents the current module version.
//	 */
//	public CVSTag(Module module, Version version) {
//
//		// TODO this implementation is temporary, and should reflect the actual situation in a better way.
//		//
//		symbolicName = module.getName().concat("_").concat(version.getVersionIdentifier());
//	}

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

	public String getSymbolicName() {
		return symbolicName;
	}

	public String toString() {
		return symbolicName;
	}

	public int hashCode() {
		return symbolicName.hashCode();
	}

	public boolean equals(Object o) {

		if (o instanceof CVSTag) {
			return ((CVSTag) o).symbolicName.equals(symbolicName);
		}

		return false;
	}
}