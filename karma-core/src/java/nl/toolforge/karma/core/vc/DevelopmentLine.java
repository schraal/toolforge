package nl.toolforge.karma.core.vc;

/**
 * A development line is a separate line of development for a module, generally implemented by a version control
 * system through a branch.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class DevelopmentLine {

	private String lineName = null;

	/**
	 * Constructor for a development line.
	 *
	 * @param lineName The name for a development line
	 */
	public DevelopmentLine(String lineName) {
		this.lineName = lineName;
	}

	public String getName() {
		return lineName;
	}
}
