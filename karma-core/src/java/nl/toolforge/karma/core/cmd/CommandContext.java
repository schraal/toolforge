package nl.toolforge.karma.core.cmd;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.Manifest;
import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.ManifestLoader;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.CVSException;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import nl.toolforge.karma.core.vc.cvs.CVSRunner;
import nl.toolforge.karma.core.vc.subversion.SVNException;
import nl.toolforge.karma.core.vc.subversion.SubversionLocationImpl;
import nl.toolforge.karma.core.vc.subversion.SubversionRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * <p>The command context is the class that provides a runtime for commands to run in. The command context maintains
 * access to the current manifest and all commands that are valid. A <code>CommandContext</code> must be initialized
 * through its {@link #init} method so it can initialize all resources it requires to properly run commands. The
 * <code>init</code> method can only be run once.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CommandContext {

	private static Log logger = LogFactory.getLog(CommandContext.class);

	private static ManifestLoader manifestLoader = null;

	private Manifest currentManifest = null;
	private LocalEnvironment env = null;

	private boolean initialized = false;

	/**
	 * <p>Checks if this <code>CommandContext</code> has been initialized. A non-initialized context cannot be used and
	 * methods in this class will throw a <code>KarmaException</code> with error code
	 * <code>KarmaException.COMMAND_CONTEXT_NOT_INITIALIZED</code> if a non-initialized context is encountered.
	 *
	 * @return <code>true</code> if this command context has been initialized, false if it isn't
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Initializes the context to run commands. This method can only be called once.
	 *
	 * @param env The users' {@link nl.toolforge.karma.core.LocalEnvironment}.
	 * @throws KarmaException
	 */
	public synchronized void init(LocalEnvironment env) throws KarmaException {

		if (!initialized) {

			this.env = env;

			// Read in all location data
			//
			LocationFactory.getInstance(env).load();

			// Try reloading the last manifest that was used.
			//
			manifestLoader = ManifestLoader.getInstance(this.env);
			currentManifest = manifestLoader.loadFromHistory();
		}
		initialized = true;
	}

	/**
	 * Gets the currently active manifest.
	 *
	 * @return The currently active manifest, or <code>null</code> when no manifest is current.
	 */
	public Manifest getCurrent() {
		return currentManifest;
	}

	/**
	 * Changes the current manifest for this context.
	 *
	 * @param manifestName
	 * @throws ManifestException When the manifest could not be changed. See {@link ManifestException#MANIFEST_LOAD_ERROR}.
	 */
	public void changeCurrent(String manifestName) throws ManifestException, LocationException {
		currentManifest = manifestLoader.load(manifestName);
	}

	/**
	 * Gets all manifests. Delegate to {@link ManifestLoader}.
	 *
	 * @return See <code>ManifestLoader.getAllManifests()</code>.
	 */
	public Set getAllManifests() throws ManifestException {
		return manifestLoader.getAllManifests();
	}

	/**
	 * <p>Executes a command. Interface applications should use this method to actually execute a command. When a
	 * <code>KarmaException</code> is thrown an interface applications should <b>*** NOT ***</b> quit program execution as
	 * a result of this exception. It should be handled nicely.
	 *
	 * @param commandLine The command to execute. A full command line is passed as a parameter.
	 * @return The result of the execution run of the command.
	 * @throws KarmaException A whole lot. Interface applications should <b>*** NOT ***</b> quit program execution as a
	 *                        result of this exception. It should be handled nicely.
	 */
	public CommandResponse execute(String commandLine) throws KarmaException {

		if (!isInitialized()) {
			throw new KarmaException(KarmaException.COMMAND_CONTEXT_NOT_INITIALIZED);
		}

    Command command = CommandFactory.getInstance().getCommand(commandLine);
		return execute(command);
	}

	/**
	 * See {@link #execute(java.lang.String)}.
	 *
	 * @param command The command to execute.
	 * @return See {@link #execute(java.lang.String)}.
	 * @throws KarmaException See {@link #execute(java.lang.String)}.
	 */
	public CommandResponse execute(Command command) throws KarmaException {

		if (!isInitialized()) {
			throw new KarmaException(KarmaException.COMMAND_CONTEXT_NOT_INITIALIZED);
		}
		if (command == null) {
			throw new KarmaException(KarmaException.INVALID_COMMAND);
		}

		// Store a reference to this context in the command
		//
		command.setContext(this);
		CommandResponse response = command.execute();

		return response;
	}

	/**
	 * A <code>Runner</code> might be required for a command to execute something on a version control system. A module
	 * can determine which implementation of a runner it requires through the
	 * {@link nl.toolforge.karma.core.Module#getLocation} method.
	 *
	 * @param module The module for which a runner is required.
	 * @return A version control system specific runner.
	 */
	public Runner getRunner(Module module) throws VersionControlException {

		Location location = module.getLocation();

		try {
			if (location instanceof CVSLocationImpl) {
				logger.debug("Getting new CVSRunner instance.");
//				return new CVSRunner(location, new File(getCurrent().getLocalPath(), module.getName()));
				return new CVSRunner(location, getCurrent().getDirectory());
			}
		} catch (ManifestException m) {
			throw new CVSException(VersionControlException.RUNNER_ERROR);
		}

		try {
			if (location instanceof SubversionLocationImpl) {
				logger.debug("Getting new CVSRunner instance.");
				return new SubversionRunner(module.getLocation());
			}
		} catch (KarmaException k) {
			throw new SVNException(VersionControlException.RUNNER_ERROR);
		}
		throw new KarmaRuntimeException("Location instance invalid.");
	}

	/**
	 * Checks if a manifest is active for this context.
	 *
	 * @return <code>true</code> if a manifest is active for the context, or <code>false</code> if no manifest is active.
	 */
	public boolean isManifestLoaded() {
		return (currentManifest != null);
	}

	public LocalEnvironment getLocalEnvironment() {
		return this.env;
	}


	/**
	 * <p>Some module-types (e.g. source modules) have a physical location on disk where the module can be located. This
	 * method returns a valid reference to that location. When the module-root is located at
	 * <code>/home/jensen/dev/modules/CORE-conversion</code>, <code>getLocalPath()</code> will return a <code>File</code>
	 * handle to that directory.
	 *
	 * @return A <code>File</code> handle to the module directory on a local disk.
	 *
	 * todo consider moving it to Module.
	 */
	public final File getLocalPath(Module module) throws KarmaException {

		File localPath = new File(getBase(), module.getName());
		logger.debug("getLocalPath() = " + localPath.getPath());

		return localPath;
	}

	/**
	 * <p>Gets the build target directory for <code>module</code>, creating it when non existing. The current default
	 * location for a build target directory is determined as follows :
	 *
	 * <pre>{@link LocalEnvironment#DEVELOPMENT_STORE_DIRECTORY} + File.separator + {@link #getCurrent()} + File.separator
	 * + build + module.getName()</pre>
	 *
	 * todo consider moving it to Module.
	 */
	public final File getBuildTarget(Module module) throws KarmaException {

    File localPath = new File(new File(getBase(), "build"), module.getName());

    if (localPath.exists()) {
      try {
        FileUtils.deleteDirectory(new File(localPath.getParent()));
      } catch (IOException e) {
      // todo implement this properly
      }
    }

		if (localPath.mkdirs()) {
			return localPath;
		}
    logger.error("Could not create build directory for module " + module.getName());
		throw new KarmaException(KarmaException.LAZY_BASTARD); // todo proper exception
	}

	/**
	 * Helper to get the module base for the current manifest.
	 */
	// todo what to do with the throws clause ???
	//
	private File getBase() throws KarmaException {
		return new File(getLocalEnvironment().getDevelopmentHome(), getCurrent().getName());
	}

}
