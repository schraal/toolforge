package nl.toolforge.karma.core.vc.model;

/**
 * Modelled against <a href="http://www.cmcrossroads.com/bradapp/acme/branching/branch-structs.html#MainLine">ACME</a>
 * principles, a <code>MainLine</code> is the home branch for development.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public interface MainLine {

	/**
	 * Name prefix for the mainline development line
	 */
	public static String NAME_PREFIX = "MAINLINE";
}
