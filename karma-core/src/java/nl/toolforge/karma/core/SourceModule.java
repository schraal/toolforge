package nl.toolforge.karma.core;

import nl.toolforge.core.regexp.Pattern;
import nl.toolforge.karma.core.expr.ModuleNameExpression;
import nl.toolforge.karma.core.expr.VersionExpression;

/**
 * <p>A <code>SourceModule</code> represents a module for which the developer wants to have the sources available to
 * on the local harddisk.
 *
 *
 * @see nl.toolforge.karma.core.Module
 *
 * @author D.A. Smedes
 */
public class SourceModule implements Module {

    private static State state = null;

    private String name = null;
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

        create(moduleName);

		Pattern pattern = Pattern.compile(new VersionExpression().getPatternString());

		if (pattern.matcher(version).matches()) {
			this.version = version;
		} else {
			throw new KarmaException("Version in module " + moduleName +
				" does not comply to pattern " + new VersionExpression().getPatternString());
		}
	}

	private void create(String moduleName) throws KarmaException {

		Pattern pattern = Pattern.compile(new ModuleNameExpression().getPatternString());

		if (pattern.matcher(moduleName).matches()) {
			this.name = moduleName;
		} else {
			throw new KarmaException("Modulename " + moduleName +
				" does not comply to pattern " + new ModuleNameExpression().getPatternString());
		}
	}

	/**
	 * Gets the <artifact-id
	 *
	 * @see {@link nl.toolforge.karma.core.Module#getName}
	 */
    public String getName() {
        return name;
    }

	/**
	 * @see {@link nl.toolforge.karma.core.Module#setVersion}
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	public Location getRepository() {
		return null;
	}

	/**
	 * @see {@link nl.toolforge.karma.core.Module#getVersion}
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @see {@link nl.toolforge.karma.core.Module#getController}
	 */
	public ModuleController getController() {
		return null;
	}

	/**
	 * A <code>SourceModule</code> can be in the three different states as defined in {@link Module}.
	 *
	 * @param state
	 */
	public final void setState(State state) {

	}

}
