package nl.toolforge.karma.core;

/**
 * A <code>Version</code> is the container object for the <code>version</code> attribute of a module. The implementation
 * is independent of the version control system.
 *
 * @author D.A. Smedes
 */
public final class Version {

  private String versionIdentifier = null;

	public Version(String versionIdentifier) {

		// todo validate against the correct pattern
		//
		this.versionIdentifier = versionIdentifier;
	}

	public String getVersionIdentifier() {
		return versionIdentifier;
	}

	/**
	 * Gets the string representation of this version.
	 *
	 * @return A string representation of this version.
	 */
	public String toString() {
		return versionIdentifier;
	}
}
