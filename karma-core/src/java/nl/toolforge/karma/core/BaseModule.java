package nl.toolforge.karma.core;

import nl.toolforge.core.regexp.Pattern;
import nl.toolforge.karma.core.expr.ModuleNameExpression;
import nl.toolforge.karma.core.location.Location;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The name says it all. This class is the base (template) for a module.
 *
 * @author D.A. Smedes
 *
 * @version $Id:
 */
public abstract class BaseModule implements Module {

	protected static Log logger = LogFactory.getLog(BaseModule.class);

	private State state = null;
	private Location location = null;
	private String name = null;

	/**
	 * Constructs the basis for a module.
	 *
	 * @param moduleName The name of the module. Module names are matched against
	 *                   a {@link nl.toolforge.karma.core.expr.ModuleNameExpression} instance.
	 * @param location The location descriptor for the module.
	 *
	 * @throws KarmaException When input parameters don't match their respective patterns
	 */
	public BaseModule(String moduleName, Location location) throws KarmaException {

		if (location == null) {
			throw new IllegalArgumentException("Location cannot be null.");
		}

		Pattern pattern = Pattern.compile(new ModuleNameExpression().getPatternString());

		if (pattern.matcher(moduleName).matches()) {
			this.name = moduleName;
		} else {
			throw new KarmaException(KarmaException.DATAFORMAT_ERROR);
		}

		this.location = location;
	}

	/**
	 * Gets the modules' name.
	 *
	 * @see {@link Module#getName}
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the modules' location.
	 *
	 * @return See {@link Location}, and all implementing classes.
	 */
	public final Location getLocation() {
		return this.location;
	}

	/**
	 * A <code>SourceModule</code> can be in the three different states as defined in {@link Module}.
	 *
	 * @param state The (new) state of the module.
	 */
	public void setState(State state) {

		// TODO : this one should probably update the file on disk
		//
		this.state = state;
	}

	/**
	 * Gets the modules' current state.
	 *
	 * @return
	 */
	public final State getState() {
		return state;
	}
}