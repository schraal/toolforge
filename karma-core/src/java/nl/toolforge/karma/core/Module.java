package nl.toolforge.karma.core;

import nl.toolforge.karma.core.location.Location;

import java.io.File;


/**
 * <p>A module is a collection of files, representing some block of functionality. This definition is probably highly
 * subjective, but for Karma, that's what it is. A module is part of a container, called a
 * <code>Manifest</code>. System's theory tells us that a system is separated into subsystems. Well, that's what we
 * do in the Karma context as well. An application system consists of one or more (generally more) modules.
 * <p/>
 * <p>Karma <code>Module</code>s are maintained in a version management system and grouped together in a
 * <code>Manifest</code>. The manifest is managing the modules. New modules can be created in two ways:
 * <p/>
 * <ul>
 * <li/>Manually, in which case their structure should comply to
 * <li/>through {@link nl.toolforge.karma.core.ModuleFactory#createModule}
 * <li/>through {@link nl.toolforge.karma.core.Manifest#createModule}
 * </ul>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public interface Module {

  //
  // todo check documentation relating to the creation of modules.

	public static final int SOURCE_MODULE = 0;
	public static final int JAR_MODULE = 1;

	public static final State WORKING = new State("WORKING");
	public static final State DYNAMIC = new State("DYNAMIC");
	public static final State STATIC = new State("STATIC");

	/**
	 * ;
	 * Retrieves a modules' name, the <code>artifact-id</code> attribute of the a module.
	 *
	 * @return The modules' name.
	 */
	public String getName();

	/**
	 * Returns the <code>Location</code> instance, which is derived from the <code>location</code>-attribute.
	 */
	public Location getLocation();

	/**
	 * Sets the {@link State} of this module.
	 *
	 * @param state A <code>State</code> object.
	 */
	public void setState(State state);

  /**
   * A modules dependencies, formatted as ...
   * 
   * @return
   */
  public String getDependencies() throws KarmaException;

  /**
   * Access to the modules' local directory. The <code>File</code> reference returns a reference to the directory on
   * disk within the context of a manifest.
   *
   * @return
   */
  public File getModuleDirectory();

  /**
	 * <p>Inner class representing the 'state' of a module. Three states exist at the moment : <code>WORKING</code>,
	 * <code>STATIC</code> and <code>DYNAMIC</code>.
	 * <p/>
	 * <ul>
	 * <li/><code>WORKING</code> means that a developer wants to develop on the module; add code, remove code etc. The
	 * local copy of the module will be updated to the reflect the latest versions of files in a particular
	 * branch. <code>WORKING</code> state also implies that a developer can promote a module so that manifests
	 * that have the module in a <code>DYNAMIC</code> state, can
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
