package nl.toolforge.karma.core.vc;

/**
 * Class representing a branch in a version control system.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public interface Branch extends SymbolicName {

	/**
	 * <p>Checks if a branch name is valid within the context of the CVS server and the rules applied by means of the
	 * release management / configuration management procedures. Within this release, rules cannot be configured, but
	 * later on, configurable branch patterns can be configured.
	 *
	 * @return <code>true</code> when the branch name
	 */
	// TODO Implement configurable stuff in karma-core-3.0, not now, too much testing effort.
	public boolean isValid();

}