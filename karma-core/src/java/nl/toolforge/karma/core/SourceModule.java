package nl.toolforge.karma.core;

import nl.toolforge.karma.core.expr.Expressions;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.DevelopmentLine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

/**
 * <p>A <code>SourceModule</code> represents a module for which the developer wants to have the sources available to
 * on the local harddisk.
 * <p/>
 * <p>TODO Validation checks on setVersion and setDevelopmentLine
 *
 * @author D.A. Smedes
 * @version $Id$
 * @see nl.toolforge.karma.core.Module
 */
public class SourceModule extends BaseModule {

//	private static Preferences prefs = Preferences.getInstance();

	/**
	 * Element name for a source module in a manifest XML file
	 */
	public static final String ELEMENT_NAME = "sourcemodule";

	/**
	 * The <code>version</code>-attribute for a module.
	 */
	public static final String VERSION_ATTRIBUTE = "version";

	/**
	 * The <code>branch</code>-attribute for a module.
	 */
	public static final String BRANCH_ATTRIBUTE = "branch";

	/**
	 * The name of the mandatory file in a source module. A file with this name is created by Karma or should be created
	 * manually and contain all data (symbolic names) that should be available for existing manifests.
	 */
	public static final String MODULE_INFO = "module.info";

	private Version version = null; // TODO replace by a Version object ?
	private DevelopmentLine developmentLine = null; // TODO replace by a Branch object ?

	/**
	 * Constructs a <code>SourceModule</code> instance.
	 *
	 * @param moduleName
	 * @param location
	 * @throws KarmaException When input parameters don't match their respective patterns
	 */
	protected SourceModule(String moduleName, Location location) throws KarmaException {
		super(moduleName, location);
	}

	/**
	 * Creates a <code>SourceModule</code> instance; the module contains a <code>version</code> attribute.
	 *
	 * @param moduleName The name of the module. Module names are matched against {@link Expressions#MODULE_NAME}.
	 * @param location
	 * @param version    The version of the module. Versionnumbers are matched against {@link Expressions#VERSION}.
	 * @throws KarmaException When input parameters don't match their patterns.
	 */
	protected SourceModule(String moduleName, Location location, Version version) throws KarmaException {

		super(moduleName, location);

		Pattern pattern = Expressions.getPattern("VERSION");
		Matcher matcher = pattern.matcher(version.getVersionNumber());

		if (matcher.matches()) {
			this.version = version;
		} else {
			throw new KarmaException(KarmaException.DATAFORMAT_ERROR);
		}
	}

	/**
	 * Sets the version property of this module
	 *
	 * @param version The <code>version</code> attribute of this module (wrapped in a Version instance) when it is available.
	 */
	public final void setVersion(Version version) {

		// TODO validate before assigment
		//
		this.version = version;
	}

	/**
	 * Sets the branch property of this module
	 *
	 * @param developmentLine The <code>line</code> attribute of this module when it is available.
	 */
	public final void setDevelopmentLine(DevelopmentLine developmentLine) {
		this.developmentLine = developmentLine;
	}

	public final DevelopmentLine getDevelopmentLine() {
		return developmentLine;
	}

	/**
	 * If the module element in the manifest contains a <code>version</code> attribute, this method will return the
	 * value of that attribute.
	 *
	 * @return The module version, or N/A, when no version number exists.
	 * @throws KarmaException When a <code>version</code> attribute is not available for the module.
	 */
	public final Version getVersion() throws KarmaException {
		return version;
	}

	/**
	 * If the module element in the manifest contains a <code>version</code> attribute, this method will return the
	 * value of that attribute.
	 *
	 * @return The module version, or N/A, when no version number exists.
	 */
	public final String getVersionAsString() {
		return (version == null ? "N/A" : version.getVersionNumber());
	}

	/**
	 * Checks if this module has a version number.
	 *
	 * @return <code>true</code> when this module has a version number, <code>false</code> if it hasn't.
	 */
	public boolean hasVersion() {
		return version != null;
	}

	/**
	 * Checks if this module is developed on a development line, other than the
	 * {@link nl.toolforge.karma.core.vc.model.MainLine}.
	 *
	 * @return <code>true</code> when this module is developed on a development line, <code>false</code> if it isn't.
	 */
	public boolean hasDevelopmentLine() {
		return developmentLine != null;
	}

}
