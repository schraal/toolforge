package nl.toolforge.karma.core;


/**
 *
 * @author D.A. Smedes
 */
public abstract class DefaultModule implements Module {

	private State state = null;
    private nl.toolforge.karma.core.location.Location location = null;
	private String name = null;

	/**
	 * Gets the modules' name.
	 *
	 * @see {@link Module#getName}
	 */
    public final String getName() {
        return name;
    }

	protected void setName(String name) {
		this.name = name;
	}

	public nl.toolforge.karma.core.location.Location getRepository() {
		return null;
	}

	/**
	 * A <code>SourceModule</code> can be in the three different states as defined in {@link Module}.
	 *
	 * @param state
	 */
	public final void setState(State state) {

		// TODO : this one should probably update the file on disk
		//
		this.state = state;
	}
}
