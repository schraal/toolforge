package nl.toolforge.karma.core;

import nl.toolforge.core.regexp.Pattern;
import nl.toolforge.karma.core.expr.VersionExpression;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.prefs.Preferences;

import java.io.File;

/**
 * <p>A <code>SourceModule</code> represents a module for which the developer wants to have the sources available to
 * on the local harddisk.
 *
 * <p>TODO Validation checks on setVersion and setBranch
 *
 * @see nl.toolforge.karma.core.Module
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class SourceModule extends BaseModule {

	private static Preferences prefs = Preferences.getInstance();

	/** Element name for a source module in a manifest XML file */
	public static final String ELEMENT_NAME = "sourcemodule";

	/** The <code>version</code>-attribute for a module. */
	public static final String VERSION_ATTRIBUTE = "version";

	/** The <code>branch</code>-attribute for a module. */
	public static final String BRANCH_ATTRIBUTE = "branch";

  /**
   * The name of the mandatory file in a source module. A file with this name is created by Karma or should be created
   * manually and contain all data (symbolic names) that should be available for existing manifests.
   */
  public static final String MODULE_INFO = "module.info";

	private String version = null; // TODO replace by a Version object ?
	private String branch = null; // TODO replace by a Branch object ?

	/**
	 * Constructs a <code>SourceModule</code> instance.
	 *
	 * @param moduleName
	 * @param location
	 *
	 * @throws KarmaException When input parameters don't match their respective patterns
	 */
	SourceModule(String moduleName, Location location) throws KarmaException {
		super(moduleName, location);
	}

	/**
	 * Creates a <code>SourceModule</code> instance; the module contains a <code>version</code> attribute.
	 *
	 * @param moduleName The name of the module. Module names are matched against
	 *                   a {@link nl.toolforge.karma.core.expr.ModuleNameExpression} instance.
	 * @param location
	 * @param version    The version of the module. Versionnumbers are matched against a {@link VersionExpression}.
	 *
	 * @throws KarmaException When input parameters don't match their patterns.
	 */
	SourceModule (String moduleName, Location location, String version) throws KarmaException {

		super(moduleName, location);

		Pattern pattern = Pattern.compile(new VersionExpression().getPatternString());

		if (pattern.matcher(version).matches()) {
			this.version = version;
		} else {
			throw new KarmaException(KarmaException.DATAFORMAT_ERROR);
		}
	}

	/**
	 * Sets the version property of this module
	 *
	 * @param version The <code>version</code> attribute of this module when it is available.
	 */
	public final void setVersion(String version) {

		// TODO validate before assigment
		//
		this.version = version;
	}

	/**
	 * Sets the branch property of this module
	 *
	 * @param branch The <code>branch</code> attribute of this module when it is available.
	 */
	public final void setBranch(String branch) {

		// TODO validate before assigment
		//
		this.branch = branch;
	}

	/**
	 * Returns the branch name as a string. The phrase "development line" is used throughout ui implementations."
	 *
	 * @return The branch name as a string.
	 */
	public final String getBranchAsString() {
		 return (branch == null ? "N/A" : branch);
	}

	/**
	 * If the module element in the manifest contains a <code>version</code> attribute, this method will return the
	 * value of that attribute.
	 *
	 * @return The module version, or N/A, when no version number exists.
	 *
	 * @throws KarmaException When a <code>version</code> attribute is not available for the module.
	 */
	public final String getVersion() throws KarmaException {

		// TODO Version should be made into a class, and the toString() method should replace getVersionAsString()

		return version;
	}

	/**
	 * If the module element in the manifest contains a <code>version</code> attribute, this method will return the
	 * value of that attribute.
	 *
	 * @return The module version, or N/A, when no version number exists.
	 */
	public final String getVersionAsString() {
		return (version == null ? "N/A" : version);
	}

	/**
	 * Returns the full path, based on {@link Preferences#getDevelopmentHome} and the modules' name {@link #getName}.
	 */
	public File getLocalPath() {

    String localPath = null;
		try {
			localPath = prefs.getDevelopmentHome().getPath().concat(File.separator).concat(this.getName());
			logger.debug("getLocalPath() = " + localPath);
		} catch (KarmaException e) {
			e.printStackTrace();
		}

		return new File(localPath);
	}

  /**
   * Checks if this module has a <code>module.info</code> file (to be more exact, a file by the name identified by
   * {@link #MODULE_INFO}.
   *
   * @return <code>true</code> if that file is present, false if it isn't.
   */
  public boolean hasModuleInfo() {

    try {
        new File(getLocalPath(), MODULE_INFO);
        return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Gets a file reference to the <code>module.info</code> file for this module.
   */
  public File getModuleInfo() throws KarmaException {

    if (hasModuleInfo()) {
      return new File(getLocalPath(), MODULE_INFO);
    }
    throw new KarmaException(KarmaException.NO_MODULE_INFO);
  }

}
