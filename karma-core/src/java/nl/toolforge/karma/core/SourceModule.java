package nl.toolforge.karma.core;

import nl.toolforge.core.regexp.Pattern;
import nl.toolforge.karma.core.expr.ModuleNameExpression;
import nl.toolforge.karma.core.expr.VersionExpression;

/**
 * <p>A <code>SourceModule</code> represents a module for which the developer wants to have the sources available to
 * on the local harddisk.
 *
 * <p>TODO Validation checks on setVersion and setBranch
 *
 * @see nl.toolforge.karma.core.Module
 *
 * @author D.A. Smedes
 */
public class SourceModule extends DefaultModule {

	/** Element name for a source module in a manifest XML file */
	public static final String ELEMENT_NAME = "sourcemodule";

	/** The <code>version</code>-attribute for a module. */
	public static final String VERSION_ATTRIBUTE = "version";

	/** The <code>branch</code>-attribute for a module. */
	public static final String BRANCH_ATTRIBUTE = "branch";

	private String version = null;

    /**
     * Constructs a <code>SourceModule</code> instance.
     *
     * @param moduleName The name of the module. Module names are matched against
     *                   a {@link nl.toolforge.karma.core.expr.ModuleNameExpression} instance.
	 *
	 * @throws KarmaException When input parameters don't match their respective patterns
     */
	SourceModule(String moduleName) throws KarmaException {

		create(moduleName);
	}

	/**
	 * Creates a <code>SourceModule</code> instance; the module contains a <code>version</code> attribute.
	 *
	 * @param moduleName The name of the module. Module names are matched against
	 *                   a {@link nl.toolforge.karma.core.expr.ModuleNameExpression} instance.
	 * @param version    The version of the module. Versionnumbers are matched against
	 *                   a {@link nl.toolforge.karma.core.expr.VersionExpression} instance.
	 *
	 * @throws KarmaException When input parameters don't match their respective patterns
	 */
	SourceModule (String moduleName, String version) throws KarmaException {

		// TODO : refactor out to DefaultModule

        create(moduleName);

		Pattern pattern = Pattern.compile(new VersionExpression().getPatternString());

		if (pattern.matcher(version).matches()) {
			this.version = version;
		} else {
			// log.debug("Version in module " + moduleName +
			//	" does not comply to pattern " + new VersionExpression().getPatternString());
			throw new KarmaException(KarmaException.DATAFORMAT_ERROR);
		}
	}

	private void create(String moduleName) throws KarmaException {

		Pattern pattern = Pattern.compile(new ModuleNameExpression().getPatternString());

		if (pattern.matcher(moduleName).matches()) {
			setName(moduleName);
		} else {
			// log.debug("Version in module " + moduleName +
			//	" does not comply to pattern " + new VersionExpression().getPatternString());
			throw new KarmaException(KarmaException.DATAFORMAT_ERROR);
		}
	}

	/**
	 * @see {@link nl.toolforge.karma.core.Module#setVersion}
	 */
	public final void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @see {@link nl.toolforge.karma.core.Module#getVersion}
	 */
	public final String getVersion() {
		return version;
	}

	/**
	 * @see {@link nl.toolforge.karma.core.Module#getController}
	 */
	public final ModuleController getController() {
		return null;
	}
}
