package nl.toolforge.karma.core.test;

import nl.toolforge.karma.core.BaseModule;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.location.Location;

import java.io.File;

/**
 * Simple extension to <code>BaseModule</code>, specifically usefull for unit testing.
 *
 * @author D.A. Smedes
 * @version $Id:
 */
public class FakeModule extends BaseModule {

	private File localPath = null;

	public FakeModule(String moduleName, Location location) throws KarmaException {
		super(moduleName, location);
	}

	public void setLocalPath(File localPath) {
		this.localPath = localPath;
	}

	public File getLocalPath() {
		return localPath;
	}
}