package nl.toolforge.karma.core;

import nl.toolforge.core.regexp.Pattern;
import nl.toolforge.karma.core.expr.ModuleNameExpression;
import nl.toolforge.karma.core.expr.VersionExpression;

/**
 * <p>A <code>SourceModule</code> represents a module for which the developer wants to have the sources available to
 * on the local harddisk.
 *
 *
 * @see Module
 *
 * @author D.A. Smedes
 */
public class JarModule extends DefaultModule {

	/** Element name for a source module in a manifest XML file */
	public static final String ELEMENT_NAME = "jarmodule";

	/** The <code>version</code>-attribute for a module. */
	public static final String VERSION_ATTRIBUTE = "version";

	private String version = null;

    /**
     * Constructs a <code>SourceModule</code> instance.
     *
     * @param moduleName The name of the module. Module names are matched against
     *                   a {@link ModuleNameExpression} instance.
	 *
	 * @throws KarmaException When input parameters don't match their respective patterns
     */
	JarModule(String moduleName) throws KarmaException {

		create(moduleName);
	}

	/**
	 * Creates a <code>Module</code> instance; the module contains a <code>version</code> attribute.
	 *
	 * @param moduleName The name of the module. Module names are matched against
	 *                   a {@link ModuleNameExpression} instance.
	 * @param version    The version of the module. Versionnumbers are matched against
	 *                   a {@link VersionExpression} instance.
	 *
	 * @throws KarmaException When input parameters don't match their respective patterns
	 */
	JarModule (String moduleName, String version) throws KarmaException {

        create(moduleName);

		Pattern pattern = Pattern.compile(new VersionExpression().getPatternString());

		if (pattern.matcher(version).matches()) {
			this.version = version;
		} else {
			// log.debug();
			throw new KarmaException(KarmaException.DATAFORMAT_ERROR);
		}
	}

	private void create(String moduleName) throws KarmaException {

		Pattern pattern = Pattern.compile(new ModuleNameExpression().getPatternString());

		if (pattern.matcher(moduleName).matches()) {
			setName(moduleName);
		} else {
			//log.debug("Modulename " + moduleName +
			//	" does not comply to pattern " + new ModuleNameExpression().getPatternString());
			throw new KarmaException(KarmaException.DATAFORMAT_ERROR);
		}
	}

	/**
	 * @see {@link Module#setVersion}
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @see {@link Module#getVersion}
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @see {@link Module#getController}
	 */
	public ModuleController getController() {
		return null;
	}
}
