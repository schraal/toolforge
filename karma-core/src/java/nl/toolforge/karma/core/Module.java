package nl.toolforge.karma.core;


/**
 * <p>A module is an important concept for the Karma tool. It represents a main building block of an application system.
 * An application system consists of one or more (generally more) modules.
 *
 * <p><code>Module</code> instances can be created in two ways:
 * <ul>
 * <li/>through {@link nl.toolforge.karma.core.ModuleFactory#createModule}
 * <li/>through {@link nl.toolforge.karma.core.Manifest#createModule}
 * </ul>
 *
 * @author D.A. Smedes
 */
public interface Module {

	public static final State WORKING = new State("WORKING");
    public static final State DYNAMIC = new State("DYNAMIC");
	public static final State STATIC = new State("STATIC");

	/** The <code>name</code>-attribute for a module. */
	public static final String NAME_ATTRIBUTE = "name";

	/** The <code>location</code>-attribute for a module. */
	public static final String LOCATION_ATTRIBUTE = "location";

    /**
     * Retrieves a modules' name, the <code>artifact-id</code> attribute of the a module.
     *
     * @return The modules' name.
     */
    public String getName();

	/**
	 * If the module element in the manifest contains a <code>version</code> attribute, this method will return the
	 * value of that attribute.
	 *
	 * @return The module version, if that exists.
	 *
	 * @throws KarmaException When a <code>version</code> attribute is not available for the module.
	 */
	public String getVersion() throws KarmaException;

	public void setVersion(String version) throws KarmaException;

	public nl.toolforge.karma.core.location.Location getRepository();

	/**
	 * Provides a reference to a modules' {@link nl.toolforge.karma.core.ModuleController} instance.
	 *
	 * @return A reference to the modules' <code>ModuleController</code>.
	 */
	public ModuleController getController();

	/**
	 * Sets the {@link State} of this module.
	 *
	 * @param state A <code>State</code> object.
	 */
	public void setState(State state);

    //public File getPath();

	/**
	 * <p>Inner class representing the 'state' of a module. Three states exist at the moment : <code>WORKING</code>,
	 * <code>STATIC</code> and <code>DYNAMIC</code>.
	 *
	 * <ul>
	 *   <li/><code>WORKING</code> means that a developer wants to develop on the module; add code, remove code etc. The
	 *        local copy of the module will be updated to the reflect the latest versions of files in a particular
	 *        branch. <code>WORKING</code> state also implies that a developer can promote a module so that manifests
	 *        that have the module in a <code>DYNAMIC</code> state, can
	 *
	 */
	class State {

		private String state = null;

		/**
		 * Constructor. Initializes the <code>State</code> instance with the correct state string.
		 *
		 * @param stateString
		 */
		State(String stateString) {

			if ((stateString == null) || (stateString.length() == 0)) {
				throw new KarmaRuntimeException("A State instance should be initialized with a correct string.");
			}
			this.state = stateString;
		}

		/**
		 * Gets the string representation of this state object.
		 *
		 * @return A <code>String</code> representation of this state object.
		 */
		public String toString() {
			return state;
		}

		public int hashCode() {
			return state.hashCode();
		}

		/**
		 * Checks equality of one <code>State</code> instance to this <code>State</code> instance. Instances are equal
		 * when their state strings are equal.
		 *
		 * @param o An object instance that must be checked for equality with this <code>State</code> instance.
		 *
		 * @return <code>true</code> if this <code>State</code> instance equals <code>o</code>, otherwise
		 *         <code>false</code>.
		 */
		public boolean equals(Object o) {

			if (o instanceof State) {
				if (o.toString().equals(this.toString())) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		}
	}
}
