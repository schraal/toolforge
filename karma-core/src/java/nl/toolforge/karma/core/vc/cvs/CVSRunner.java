package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.core.util.file.FileUtils;
import nl.toolforge.karma.core.*;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.ManagedFile;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.SymbolicName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.lib.cvsclient.Client;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.add.AddCommand;
import org.netbeans.lib.cvsclient.command.checkout.CheckoutCommand;
import org.netbeans.lib.cvsclient.command.commit.CommitCommand;
import org.netbeans.lib.cvsclient.command.importcmd.ImportCommand;
import org.netbeans.lib.cvsclient.command.log.LogCommand;
import org.netbeans.lib.cvsclient.command.log.LogInformation;
import org.netbeans.lib.cvsclient.command.update.UpdateCommand;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * <p>Runner class for CVS. Executes stuff on a CVS repository.
 *
 * <p>TODO : the CVSRunner could be made multi-threaded, to use bandwidth to a remote repository much better ...
 *
 * @author D.A. Smedes
 *
 * @version $Id:
 */
public final class CVSRunner implements Runner {

	static {

		// As per the recommendation ...
		//
		System.setProperty("javacvs.multiple_commands_warning", "false");
	}

	private static Log logger = LogFactory.getLog(CVSRunner.class);

	private GlobalOptions globalOptions = new GlobalOptions();
	private Client client = null;

	/**
	 * Constructs a runner to fire commands on a CVS repository. A typical client for a <code>CVSRunner</code> instance is
	 * a {@link Command} implementation, as that one knows what to fire away on CVS.
	 *
	 * @param location A <code>Location</code> instance (typically a <code>CVSLocationImpl</code> instance), containing
	 *                 the location and connection details of the CVS repository.
	 * @param contextDirectory The absolute directory that acts as the local path required by the CVS client.
	 */
	public CVSRunner(Location location, File contextDirectory) throws CVSException {

		CVSLocationImpl cvsLocation = null;
		try {
			cvsLocation = ((CVSLocationImpl) location);
		} catch (ClassCastException e) {
			logger.error("Wrong type for location. Should be CVSLocationImpl.",e);
		}

		logger.debug("Initializing CVS client for location : " + location.toString());

		// Initialize a CVS client
		//
		client = new Client( cvsLocation.getConnection(), new StandardAdminHandler());
		client.setLocalPath(contextDirectory.getPath());

		logger.debug("CVSRunner using CVSROOT : " + cvsLocation.toString());

		globalOptions.setCVSRoot(cvsLocation.getCVSRootAsString());

	}

	/**
	 * Creates a module in a CVS repository. This is done through the CVS <code>import</code> command.
	 */
	public CommandResponse create(Module module) throws CVSException {

		if (existsInRepository(module)) {
			throw new CVSException(CVSException.MODULE_EXISTS_IN_REPOSITORY, new Object[]{module.getName(), module.getLocation().getId()});
		}

		// Step 1 : create an empty module structure
		//
		ImportCommand importCommand = new ImportCommand();
		importCommand.setModule(module.getName());
		importCommand.setLogMessage("Module " + module.getName() + " created automatically by Karma on " + new Date().toString());
		importCommand.setVendorTag("INITIAL");
		importCommand.setReleaseTag("INITIAL_0-0");

		// Create a temporary structure
		//
		File tmp = null;
		try {
			tmp = FileUtils.createTempDirectory();
		} catch (IOException e) {
			throw new KarmaRuntimeException("Panic! Failed to create temporary directory.");
		}

		File moduleDirectory = new File(tmp.getPath(), module.getName());
		if (!moduleDirectory.mkdir()) {
			throw new KarmaRuntimeException("Panic! Failed to create temporary directory for module " + module.getName());
		}

		// Override localpath setting in the CVS client, as we are importing the thing from a temporary location.
		//
		client.setLocalPath(moduleDirectory.getParent());

		CVSResponseAdapter adapter = executeOnCVS(importCommand, module.getName()); // Use module as context directory

		// Remove the temporary structure.
		//
		FileUtils.delete(tmp);

    // Step 2 : checkout the module to be able to create module.info
		//

		// Create another (...) temporary structure
		//
		try {
			tmp = FileUtils.createTempDirectory();
		} catch (IOException e) {
			throw new KarmaRuntimeException("Panic! Failed to create temporary directory.");
		}

		client.setLocalPath(tmp.getPath()); // Point the CVS client to the temp directory.

//		checkout(module);

    CheckoutCommand checkoutCommand = new CheckoutCommand();
		checkoutCommand.setModule(module.getName());
		checkoutCommand.setPruneDirectories(true);

		adapter = executeOnCVS(checkoutCommand, null); // Use module as context directory

		// TODO do more on exception handling from CVS ... You can't be sure it worked
		//

		client.setLocalPath(tmp.getPath()); // Make sure the CVS client points to the current temp directory again.

		add(module, SourceModule.MODULE_INFO);

//		adapter.clearStatus();
//		adapter.addStatusUpdate(CVSResponseAdapter.MODULE_);

		// Remove the temporary structure.
		//
		FileUtils.delete(tmp);

		return adapter;
	}

	/**
	 * Performs the <code>cvs checkout &lt;module&gt;</code>command for a module in a specific
	 * <code>checkoutDirectory</code>. Use {@link #checkout(nl.toolforge.karma.core.Module)} to checkout the module
	 * in the default checkout directory.
	 *
	 * @param module The module.
	 *
	 * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
	 */
	public CommandResponse checkout(Module module, Version version) throws CVSException {

		CheckoutCommand checkoutCommand = new CheckoutCommand();
		checkoutCommand.setModule(module.getName());
//		checkoutCommand.setPruneDirectories(true);

		if (version != null) {
			checkoutCommand.setCheckoutByRevision(Utils.createSymbolicName(module, version).getSymbolicName());
		}

		CVSResponseAdapter adapter = executeOnCVS(checkoutCommand, null);

		if (adapter.hasStatus(CVSResponseAdapter.MODULE_NOT_FOUND)) {
			throw new CVSException(CVSException.NO_SUCH_MODULE_IN_REPOSITORY, new Object[]{module.getName(), module.getLocation().getId()});
		}
		if (adapter.hasStatus(CVSResponseAdapter.INVALID_SYMBOLIC_NAME)) {
			throw new CVSException(CVSException.INVALID_SYMBOLIC_NAME);
		}

		return adapter;
	}

	public CommandResponse checkout(Module module) throws CVSException {
		return checkout(module, null);
	}

	/**
	 * For a module, the <code>cvs -q update -Pd -r &lt;symbolic-name&gt;</code> command is executed.
	 *
	 * @param module The module that should be updated.
	 * @param version The version of the module or <code>null</code> when no specific version applies.
	 *
	 * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
	 */
	public CommandResponse update(Module module, Version version) throws CVSException {

		UpdateCommand updateCommand = new UpdateCommand();
		updateCommand.setRecursive(true);
		updateCommand.setPruneDirectories(true);

		if (version != null) {
			updateCommand.setUpdateByRevision(Utils.createSymbolicName(module, version).getSymbolicName());
		}

		CVSResponseAdapter adapter = executeOnCVS(updateCommand, module.getName());

		if (adapter.hasStatus(CVSResponseAdapter.SYMBOLIC_NAME_NOT_FOUND)) {
			throw new CVSException(CVSException.VERSION_NOT_FOUND, new Object[]{version.getVersionIdentifier(), module.getName()});
		}
		if (adapter.hasStatus(CVSResponseAdapter.INVALID_SYMBOLIC_NAME)) {
			throw new CVSException(CVSException.INVALID_SYMBOLIC_NAME);
		}

		return adapter;
	}

	public CommandResponse update(Module module) throws CVSException {
		return update(module, null);
	}

	/**
	 * For a module, the <code>cvs commit -m &lt;commitMessage&gt;</code>command is executed.
	 *
	 * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
	 */
	public CommandResponse commit(ManagedFile file, String message) throws CVSException {
		return null;
	}

	/**
	 * Adds a file to the CVS repository and implicitely commits the file (well, it tries to do so).
	 *
	 * @param module The module that contains the file.
	 * @param fileName The fileName to add, relative to <code>contextDirectory</code>, which was set when instantiating
	 *   this runner.
	 *
	 * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
	 */
	public CommandResponse add(Module module, String fileName) throws CVSException {

		// Step 1 : Add the file to the CVS repository
		//
		AddCommand addCommand = new AddCommand();
		addCommand.setMessage("Initial checkin in repository.");

		String path = client.getLocalPath() + File.separator + module.getName();
		File fileToAdd = new File(path + File.separator + fileName);
		if (!fileToAdd.exists()) {
			try {
				fileToAdd.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new KarmaRuntimeException("Could not create " + fileName + " in " + path);
			}
		}

		addCommand.setFiles(new File[]{fileToAdd});

		executeOnCVS(addCommand, module.getName());

		// Step 2 : Commit the file to the CVS repository
		//
		CommitCommand commitCommand = new CommitCommand();
		commitCommand.setFiles(new File[]{fileToAdd});
		commitCommand.setMessage("File added automatically by Karma.");

		CVSResponseAdapter adapter = executeOnCVS(commitCommand, null);

		if (adapter.hasStatus(CVSResponseAdapter.FILE_EXISTS)) {
			throw new CVSException(CVSException.FILE_EXISTS_IN_REPOSITORY, new Object[]{module.getName(), module.getLocation().getId()});
		}

		return adapter;
	}

	/**
	 * For a module, the <code>cvs commit -m "&lt;some-message&gt;"</code>command is executed.
	 *
	 * @param module The module. Will be committed recursively.
	 * @param message A commit message.
	 *
	 *
	 * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
	 */
	public CommandResponse commit(Module module, String message) throws CVSException {

		CommitCommand commitCommand = new CommitCommand();

		// To commit the module, the cvs client should start from the module directory, which has been set
		// when constructing this runner ...
		//
		commitCommand.setFiles(new File[]{new File(client.getLocalPath())});
		commitCommand.setRecursive(true);
		commitCommand.setMessage(message);
    CVSResponseAdapter adapter = executeOnCVS(commitCommand, null);

		return adapter;
	}

	public CommandResponse branch(Module module, SymbolicName branch) throws CVSException {
		return null;
	}

	/**
	 * Creates a sticky tag on all files in a module.
	 *
	 * @param module
	 * @param tag
	 * @return
	 */
	public CommandResponse tag(Module module, SymbolicName tag) throws CVSException {
		return null;
	}

	/**
	 *
	 */
	public LogInformation log(Module module) throws KarmaException {

		if (module instanceof SourceModule) {
			LogCommand logCommand = new LogCommand();
			logCommand.setFiles(new File[] {((SourceModule) module).getModuleInfo()});

			executeOnCVS(logCommand, null);
		}
		throw new KarmaRuntimeException("Only instance of type SourceModule can use this method.");
	}


	/**
	 *
	 * @param command The Netbeans Command implementation.
	 * @param contextDirectory The directory relative to Client.getLocalPath(), required by some commands.
	 */
	private CVSResponseAdapter executeOnCVS(org.netbeans.lib.cvsclient.command.Command command, String contextDirectory) throws CVSException{

		try {

			if (contextDirectory != null) {
				client.setLocalPath(client.getLocalPath() + File.separator + contextDirectory);
			}
			logger.debug("CVS command " + command.getCVSCommand() + " in " + client.getLocalPath());

			CVSResponseAdapter adapter = new CVSResponseAdapter();

			// A CVSResponseAdapter is registered as a listener for the response from CVS. This one adapts to Karma
			// specific stuff.
			//
			client.getEventManager().addCVSListener(adapter);

			client.executeCommand(command, globalOptions);

			return adapter;

		} catch (CommandException e) {
			e.printStackTrace(); // todo throw exception
		} catch (AuthenticationException e) {
			throw new CVSException(CVSException.AUTHENTICATION_ERROR, new Object[]{client.getConnection()});
		}

		return null; // Either the adapter has been returned, or we got an exception.
	}

	/**
	 * A check if a module exists is done by trying to checkout the modules' <code>module.info</code> file in a temporary
	 * location. If that succeeds, apparently the module exists in that location and we have a <code>true</code> to
	 * return.
	 */
	private boolean existsInRepository(Module module) throws CVSException {


		if (module == null) {
			throw new NullPointerException("Module should not be null.");
		}

		CheckoutCommand checkoutCommand = new CheckoutCommand();
		checkoutCommand.setModule(module.getName() + "/" + SourceModule.MODULE_INFO);

		File tmp = null;
		try {
			tmp = FileUtils.createTempDirectory();
		} catch (IOException e) {
			throw new KarmaRuntimeException("Panic! Failed to create temporary directory for module " + module.getName());
		}

		// Overrule client.setLocalPath() to a temporary location
		//
		client.setLocalPath(tmp.getPath());

		executeOnCVS(checkoutCommand, null);

		File moduleDirectory = new File(tmp.getPath(), module.getName());
		if (moduleDirectory.exists()) {
			return true;
		} else {
			return false;
		}
	}

}