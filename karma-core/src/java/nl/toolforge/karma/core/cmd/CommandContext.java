package nl.toolforge.karma.core.cmd;

import java.util.Set;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Manifest;
import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.ManifestLoader;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationFactory;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import nl.toolforge.karma.core.vc.cvs.CVSRunner;
import nl.toolforge.karma.core.vc.subversion.SubversionLocationImpl;
import nl.toolforge.karma.core.vc.subversion.SubversionRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>The command context is the class that provides a runtime for commands to run in. The command context maintains
 * access to the current manifest and all commands that are valid. A <code>CommandContext</code> must be initialized
 * through its {@link #init} method so it can initialize all resources it requires to properly run commands. The
 * <code>init</code> method can only be run once.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public final class CommandContext {

	private static Log logger = LogFactory.getLog(CommandContext.class);

	private static ManifestLoader manifestLoader = ManifestLoader.getInstance();

	private Manifest currentManifest = null;

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
	 * @throws KarmaException
	 */
	public synchronized void init() throws KarmaException {

		if (!initialized) {
			// Read in all location data
			//
			LocationFactory.getInstance().load();

			// Try reloading the last manifest that was used.
			//
			currentManifest = manifestLoader.loadFromHistory();
		}
		initialized = true;
	}

	/**
	 * Gets the currently active manifest.
	 *
	 * @return The currently active manifest.
	 * @throws KarmaException See {@link KarmaException#NO_MANIFEST_SELECTED}
	 */
	public Manifest getCurrent() throws KarmaException {

		if (currentManifest == null) {
			throw new KarmaException(KarmaException.NO_MANIFEST_SELECTED);
		}
		return currentManifest;
	}

	/**
	 * Changes the current manifest for this context.
	 *
	 * @param manifestName
	 * @throws KarmaException
	 */
	public void changeCurrent(String manifestName) throws KarmaException {
		currentManifest = manifestLoader.load(manifestName);
	}

	/**
	 * Gets all manifests. Delegate to {@link ManifestLoader}.
	 *
	 * @return See <code>ManifestLoader.getAll()</code>.
	 */
	public Set getAll() throws ManifestException {
		return manifestLoader.getAll();
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

        // A command is extracted from the commandLine
        //
        Command command = (Command) (CommandFactory.getInstance().getCommand(commandLine));

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

		return command.execute();
	}


	/**
	 * A <code>Runner</code> might be required for a command to execute something on a version control system. A module
	 * can determine which implementation of a runner it requires through the
	 * {@link nl.toolforge.karma.core.Module#getLocation} method.
	 *
	 * @param module The module for which a runner is required.
	 * @return A version control specific runner.
	 */
	public Runner getRunner(Module module) throws KarmaException {

		Location location = module.getLocation();

		if (location instanceof CVSLocationImpl) {
			logger.debug("Getting new CVSRunner instance.");
			return new CVSRunner(location);
		}
		if (location instanceof SubversionLocationImpl) {
			logger.debug("Getting new CVSRunner instance.");
			return new SubversionRunner(module.getLocation());
		}
		throw new KarmaRuntimeException("Location instance invalid.");
	}

}
