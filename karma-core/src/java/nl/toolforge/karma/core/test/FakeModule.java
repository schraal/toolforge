package nl.toolforge.karma.core.test;

import nl.toolforge.karma.core.BaseModule;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.location.Location;

/**
 * Simple extension to <code>BaseModule</code>, specifically usefull for unit testing.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class FakeModule extends BaseModule {

	public FakeModule(String moduleName, Location location) throws KarmaException {
		super(moduleName, location);
	}
}