package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.location.Location;

import java.util.regex.PatternSyntaxException;


/**
 * <p>A module is a collection of files, representing some block of functionality. This definition is probably highly
 * subjective, but for Karma, that's what it is. A module is part of a container, called a
 * <code>Manifest</code>. System's theory tells us that a system is separated into subsystems. Well, that's what we
 * do in the Karma context as well. An application system consists of one or more (generally more) modules.</p>
 *
 * <p>Karma <code>Module</code>s are maintained in a version management system and grouped together in a
 * <code>Manifest</code>. The manifest is managing the modules.</p>
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public interface Module {

  public static final State WORKING = new State("WORKING");
  public static final State DYNAMIC = new State("DYNAMIC");
  public static final State STATIC = new State("STATIC");

	/**
   * Retrieves a modules' name, the <code>name</code> attribute of the module in the manifest XML file.
   *
   * @return The modules' name.
   */
  public String getName();

  /**
   * Returns the <code>Location</code> instance, which is derived from the <code>location</code>-attribute.
   */
  public Location getLocation();

  /**
   * <p>Sets a modules' state.</p>
   *
   * <p>This method currently only applies to SourceModule implementations, but has been included here to allow for
   * easier future enhancement to Karma.</p>
   *
   * @param state
   */
  public void setState(State state);

  /**
   * <p>This method currently only applies to SourceModule implementations, but has been included here to allow for
   * easier future enhancement to Karma.</p>
   *
   * @return
   */
  public String getStateAsString();

  /**
   * Iets met artifact-name.
   * @return
   */
  public String getDependencyName();

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
  final class State {

    // todo unit test should be written

    private String state = null;

    /**
     * Constructor. Initializes the <code>State</code> instance with the correct state string.
     *
     * @param stateString
     */
    public State(String stateString) {

      if (!stateString.matches("WORKING|DYNAMIC|STATIC")) {
        throw new PatternSyntaxException(
            "Pattern mismatch for 'state'; pattern must match 'WORKING|DYNAMIC|STATIC'", stateString, -1);
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

    /**
     * Returns the filename for this state on disk (generally something like <code>.WORKING</code> or
     * <code>.STATIC</code>.
     *
     * @return
     */
    public String getHiddenFileName() {
      return "." + state;
    }
  }
}
