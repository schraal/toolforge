package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Manifest;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.SourceModule;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.ManagedFile;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.SymbolicName;
import nl.toolforge.karma.core.vc.VersionControlException;
import org.apache.commons.io.FileUtils;
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
import org.netbeans.lib.cvsclient.command.tag.TagCommand;
import org.netbeans.lib.cvsclient.command.update.UpdateCommand;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.connection.Connection;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

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

//	private Client client = null;
	private GlobalOptions globalOptions = new GlobalOptions();

	private File basePoint = null;
	private Connection connection = null;

	/**
	 * Constructs a runner to fire commands on a CVS repository. A typical client for a <code>CVSRunner</code> instance is
	 * a {@link Command} implementation, as that one knows what to fire away on CVS. The runner is instantiated with a
	 * <code>location</code> and a <code>manifest</code>. The location must be a <code>CVSLocationImpl</code> instance,
	 * reprenting a CVS repository. The manifest is required because it determines the base point from where CVS commands
	 * will be run; modules are checked out in a directory structure, relative to {@link Manifest#getLocalPath()}.
	 *
	 * @param location
	 *   A <code>Location</code> instance (typically a <code>CVSLocationImpl</code> instance), containing
	 *   the location and connection details of the CVS repository.
	 * @param basePoint
	 *   The basePoint determines the base point where cvs commands should be run. If not used by commands and extended,
	 *   this <code>basePoint.getPath()</code> will be used by the CVS client as the
	 */
	public CVSRunner(Location location, File basePoint) throws CVSException {

		CVSLocationImpl cvsLocation = null;
		try {
			cvsLocation = ((CVSLocationImpl) location);
		} catch (ClassCastException e) {
			logger.error("Wrong type for location. Should be CVSLocationImpl.", e);
			throw new KarmaRuntimeException("Wrong type for location. Should be CVSLocationImpl.", e);
		}

		if (basePoint == null) {
			throw new IllegalArgumentException("basePoint cannot be null.");
		}
		this.connection = cvsLocation.getConnection();
		this.basePoint = basePoint;

//		client = new Client(getConnection(), new StandardAdminHandler());

		logger.debug("CVSRunner using CVSROOT : " + cvsLocation.toString());
		globalOptions.setCVSRoot(cvsLocation.getCVSRootAsString());
	}

	private File getBasePoint() {
		return basePoint;
	}

	private Connection getConnection() {
		return connection;
	}

	/**
	 * <p>Creates a module in a CVS repository. This is done through the CVS <code>import</code> command. The basic structure
	 * of the module directory is defined by the file <code>module-structure.model</code>, which should be available from
	 * the classpath. If the file cannot be located, a basic structure is created:
	 *
	 * <ul>
	 *   <li/>A directory based on <code>module.getName()</code>.
	 *   <li/>A file in that directory, called <code>module.info</code>.
	 * </ul>
	 *
	 * <p>After creation, the module is available with the initial version <code>0-0</code>.
	 *
	 * @param module The module to be created.
	 *
	 * @return The result of the create command, wrapped in a <code>CommandResponse</code> object.
	 *
	 * @throws CVSException Errorcode <code>MODULE_EXISTS_IN_REPOSITORY</code>, when the module already exists on the
	 *         location as specified by the module.
	 */
	public CommandResponse create(Module module) throws CVSException {

		// TODO the initial version should also be made configurable, together with the patterns for modulenames et al.

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
			tmp = MyFileUtils.createTempDirectory();
		} catch (IOException e) {
			throw new KarmaRuntimeException("Panic! Failed to create temporary directory.");
		}

		File moduleDirectory = new File(tmp, module.getName());
		if (!moduleDirectory.mkdir()) {
			throw new KarmaRuntimeException("Panic! Failed to create temporary directory for module " + module.getName());
		}

		CVSResponseAdapter adapter = executeOnCVS(importCommand, moduleDirectory); // Use module as context directory

		// Remove the temporary structure.
		//
		try { FileUtils.deleteDirectory(tmp); } catch (IOException e) { e.printStackTrace(); }

		// Step 2 : checkout the module to be able to create module.info
		//

		// Create another (...) temporary structure
		//
		try {
			tmp = MyFileUtils.createTempDirectory();
		} catch (IOException e) {
			throw new KarmaRuntimeException("Panic! Failed to create temporary directory.");
		}

//		client.setLocalPath(tmp.getPath()); // Point the CVS client to the temp directory.
		checkout(module, tmp);

//		client.setLocalPath(tmp.getPath()); // Make sure the CVS client points to the current temp directory again.
		add(module, SourceModule.MODULE_INFO, tmp);

//		client.setLocalPath(tmp.getPath());
		tag(module, new Version("0-0"), new File(tmp, module.getName()));

		// Remove the temporary structure.
		//
		try { FileUtils.deleteDirectory(tmp); } catch (IOException e) { e.printStackTrace(); }

		return adapter;
	}

	/**
	 * Performs the <code>cvs checkout [-r &lt;symbolic-name&gt;] &lt;module&gt;</code>command for a module.
	 * <code>version</code> is used when not <code>null</code> to checkout a module with a symbolic name.
	 *
	 * @param module The module to check out.
	 * @param version The version number for the module to check out.
	 *
	 * @return The CVS response wrapped in a <code>CommandResponse</code>.
	 *
	 * @throws CVSException With errorcodes <code>NO_SUCH_MODULE_IN_REPOSITORY</code> when the module does not exist
	 *         in the repository and <code>INVALID_SYMBOLIC_NAME</code>, when the version does not exists for the module.
	 */
	public CommandResponse checkout(Module module, Version version) throws CVSException {

		return checkout(module, version, getBasePoint());
	}

	/**
	 * See {@link #checkout(nl.toolforge.karma.core.Module, nl.toolforge.karma.core.Version)}. This method defaults
	 * to the HEAD of the development branch at hand.
	 *
	 * @param module The module to check out.
	 *
	 * @return The CVS response wrapped in a <code>CommandResponse</code>.
	 *
	 * @throws CVSException With errorcodes <code>NO_SUCH_MODULE_IN_REPOSITORY</code> when the module does not exist
	 *         in the repository and <code>INVALID_SYMBOLIC_NAME</code>, when the version does not exists for the module.
	 */
	public CommandResponse checkout(Module module) throws CVSException {
		return checkout(module, null, getBasePoint());
	}

	//
	// For the time being, private
	//
	private CommandResponse checkout(Module module, File basePoint) throws CVSException {
		return checkout(module, null, basePoint);
	}

	//
	// For the time being, private
	//
	private CommandResponse checkout(Module module, Version version, File basePoint) throws CVSException {

		CheckoutCommand checkoutCommand = new CheckoutCommand();
		checkoutCommand.setModule(module.getName());

		if (version != null) {
			checkoutCommand.setCheckoutByRevision(Utils.createSymbolicName(module, version).getSymbolicName());
		}

		CVSResponseAdapter adapter = executeOnCVS(checkoutCommand, basePoint);

		if (adapter.hasStatus(CVSResponseAdapter.MODULE_NOT_FOUND)) {
			throw new CVSException(CVSException.NO_SUCH_MODULE_IN_REPOSITORY, new Object[]{module.getName(), module.getLocation().getId()});
		}
		if (adapter.hasStatus(CVSResponseAdapter.INVALID_SYMBOLIC_NAME)) {
			throw new CVSException(CVSException.INVALID_SYMBOLIC_NAME);
		}

		return adapter;
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
		return update(module, version, basePoint);
	}

	public CommandResponse update(Module module) throws CVSException {
		return update(module, null, basePoint);
	}

	private CommandResponse update(Module module, Version version, File basePoint) throws CVSException {

		UpdateCommand updateCommand = new UpdateCommand();
		updateCommand.setRecursive(true);
		updateCommand.setPruneDirectories(true);

		if (version != null) {
			updateCommand.setUpdateByRevision(Utils.createSymbolicName(module, version).getSymbolicName());
		}

		CVSResponseAdapter adapter = executeOnCVS(updateCommand, new File(basePoint, module.getName()));

		if (adapter.hasStatus(CVSResponseAdapter.SYMBOLIC_NAME_NOT_FOUND)) {
			throw new CVSException(CVSException.VERSION_NOT_FOUND, new Object[]{version.getVersionNumber(), module.getName()});
		}
		if (adapter.hasStatus(CVSResponseAdapter.INVALID_SYMBOLIC_NAME)) {
			throw new CVSException(CVSException.INVALID_SYMBOLIC_NAME);
		}

		return adapter;
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
		return add(module, fileName, getBasePoint());
	}

	private CommandResponse add(Module module, String fileName, File basePoint) throws CVSException {

		// Step 1 : Add the file to the CVS repository
		//
		AddCommand addCommand = new AddCommand();
		addCommand.setMessage("Initial checkin in repository.");

//		String path = basePoint + File.separator + module.getName();
		File modulePath = new File(basePoint, module.getName());
		File fileToAdd = new File(modulePath, fileName);
		if (!fileToAdd.exists()) {
			try {
				fileToAdd.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new KarmaRuntimeException("Could not create " + fileName + " in " + modulePath.getPath());
			}
		}

		addCommand.setFiles(new File[]{fileToAdd});

		// A file is added against a module, thus the contextDirectory is constructed based on the basePoint and the
		// modules' name.
		//
		executeOnCVS(addCommand, new File(basePoint, module.getName()));

		// Step 2 : Commit the file to the CVS repository
		//
		CommitCommand commitCommand = new CommitCommand();
		commitCommand.setFiles(new File[]{fileToAdd});
		commitCommand.setMessage("File added automatically by Karma.");

		CVSResponseAdapter adapter = executeOnCVS(commitCommand, new File(basePoint, module.getName()));

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

		return commit(module, message, getBasePoint());
	}

	private CommandResponse commit(Module module, String message, File basePoint) throws CVSException {

		CommitCommand commitCommand = new CommitCommand();

		commitCommand.setFiles(new File[]{new File(basePoint, module.getName())});
		commitCommand.setRecursive(true);
		commitCommand.setMessage(message);

		CVSResponseAdapter adapter = executeOnCVS(commitCommand, basePoint);

		return adapter;
	}


	public CommandResponse branch(Module module, SymbolicName branch) throws CVSException {
		return null;
	}

	public CommandResponse tag(Module module, SymbolicName tag) throws CVSException {
		return tag(tag, new File(getBasePoint(), module.getName()));
	}

	public CommandResponse tag(Module module, Version version) throws CVSException {
		return tag(module, version, new File(getBasePoint(), module.getName()));
	}

	private CommandResponse tag(Module module, Version version, File basePoint) throws CVSException {
		if (hasVersion(module, version)) {
			throw new CVSException(VersionControlException.DUPLICATE_VERSION, new Object[]{module.getName(), version.getVersionNumber()});
		}
		return tag(Utils.createSymbolicName(module, version), basePoint);
	}

	//
	// Private for the time being
	//
	private CommandResponse tag(SymbolicName symbolicName, File basePoint) throws CVSException {

		TagCommand tagCommand = new TagCommand();
		tagCommand.setRecursive(true);
		tagCommand.setTag(symbolicName.getSymbolicName());

		CVSResponseAdapter adapter = executeOnCVS(tagCommand, basePoint);

		return adapter;
	}

	/**
	 * Provide log information on a module.
	 */
	public LogInformation log(Module module) throws CVSException {

		CVSResponseAdapter adapter = null;

		if (module instanceof SourceModule) {
			try {

				// Logs are run on a temporary checkout of the module.info of a module.
				//
				File tmp = null;
				try {
					tmp = MyFileUtils.createTempDirectory();
				} catch (IOException e) {
					throw new KarmaRuntimeException("Panic! Failed to create temporary directory for module " + module.getName());
				}

				CheckoutCommand checkoutCommand = new CheckoutCommand();
				checkoutCommand.setModule(module.getName() + "/" + SourceModule.MODULE_INFO);

				adapter = executeOnCVS(checkoutCommand, tmp);

				if (adapter.hasStatus(CVSResponseAdapter.MODULE_NOT_FOUND)) {
					throw new CVSException(VersionControlException.FILE_NOT_FOUND);
				}

				LogCommand logCommand = new LogCommand();

				// Determine the location of module.info, relative to where we are.
				//
				// Todo a reference to SourceModule is used here. Verify ...
				File moduleInfo = new File(new File(tmp, module.getName()), SourceModule.MODULE_INFO);
				logCommand.setFiles(new File[] {moduleInfo});

				adapter = executeOnCVS(logCommand, new File(tmp, module.getName()));

				try { FileUtils.deleteDirectory(tmp); } catch (IOException e) { e.printStackTrace(); }

				return adapter.getLogInformation();

			} catch (KarmaException k) {
				throw new CVSException(k.getErrorCode());
			}
		}
		throw new KarmaRuntimeException("Only instance of type SourceModule can use this method.");
	}

	/**
	 * A check if a module exists is done by trying to checkout the modules' <code>module.info</code> file in a temporary
	 * location. If that succeeds, apparently the module exists in that location and we have a <code>true</code> to
	 * return.
	 */
	private boolean existsInRepository(Module module) throws CVSException {

		if (module == null) {
			return false;
		}

		CheckoutCommand checkoutCommand = new CheckoutCommand();
		checkoutCommand.setModule(module.getName() + "/" + SourceModule.MODULE_INFO);

		File tmp = null;
		try {
			tmp = MyFileUtils.createTempDirectory();
		} catch (IOException e) {
			throw new KarmaRuntimeException("Panic! Failed to create temporary directory for module " + module.getName());
		}

		executeOnCVS(checkoutCommand, tmp);

		File moduleDirectory = new File(tmp, module.getName());
		if (moduleDirectory.exists()) {
			try { FileUtils.deleteDirectory(tmp); } catch (IOException e) { e.printStackTrace(); }
			return true;
		} else {
			try { FileUtils.deleteDirectory(tmp); } catch (IOException e) { e.printStackTrace(); }
			return false;
		}
	}

	private boolean hasVersion(Module module, Version version) throws CVSException {
		return hasSymbolicName(module, Utils.createSymbolicName(module, version));
	}

	private boolean hasSymbolicName(Module module, SymbolicName symbolicName) throws CVSException {

		if (symbolicName == null) {
			return false;
		}

		LogInformation logInfo = log(module);
		List symbolicNames = logInfo.getAllSymbolicNames();

		return symbolicNames.contains(symbolicName.getSymbolicName());
	}

	/**
	 * Runs a CVS command on the repository (through the Netbeans API). contextDirectory is assigned to client.setLocalPath()
	 *
	 * @param command A command object, representing the CVS command.
	 * @param contextDirectory The directory from where the command should be run.
	 *
	 * @return The response from the CVS command, wrapped in a nice little object, which can be queried for results.
	 */
	private CVSResponseAdapter executeOnCVS(
		org.netbeans.lib.cvsclient.command.Command command,
		File contextDirectory) throws CVSException {

		Client client = new Client(getConnection(), new StandardAdminHandler());
		client.setLocalPath(contextDirectory.getPath());

		logger.debug("Running CVS command : '" + command.getCVSCommand() + "' in " + client.getLocalPath());

		CVSResponseAdapter adapter = new CVSResponseAdapter();

		try {
			// A CVSResponseAdapter is registered as a listener for the response from CVS. This one adapts to Karma
			// specific stuff.
			//
			client.getEventManager().addCVSListener(adapter);
			client.executeCommand(command, globalOptions);

			// See the static block in this class and corresponding documentation.
			//
			client.getConnection().close();

			return adapter;

		} catch (IOException e) {
			throw new CVSException(CVSException.INTERNAL_ERROR);
		} catch (CommandException e) {
			throw new CVSException(CVSException.INTERNAL_ERROR);
		} catch (AuthenticationException e) {
			throw new CVSException(CVSException.AUTHENTICATION_ERROR, new Object[]{client.getConnection()});
		}
	}
}