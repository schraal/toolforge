package nl.toolforge.karma.core;

import nl.toolforge.core.regexp.Pattern;
import nl.toolforge.karma.core.expr.VersionExpression;
import nl.toolforge.karma.core.location.Location;

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

	/** Element name for a source module in a manifest XML file */
	public static final String ELEMENT_NAME = "sourcemodule";

	/** The <code>version</code>-attribute for a module. */
	public static final String VERSION_ATTRIBUTE = "version";

	/** The <code>branch</code>-attribute for a module. */
	public static final String BRANCH_ATTRIBUTE = "branch";

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
	 * If the module element in the manifest contains a <code>version</code> attribute, this method will return the
	 * value of that attribute.
	 *
	 * @return The module version, if that exists.
	 *
	 * @throws KarmaException When a <code>version</code> attribute is not available for the module.
	 */
	public final String getVersion() throws KarmaException {
		return version;
	}

	/**
	 * @see {@link nl.toolforge.karma.core.Module#getController}
	 */
	public final ModuleController getController() {
		return null;
	}
}
