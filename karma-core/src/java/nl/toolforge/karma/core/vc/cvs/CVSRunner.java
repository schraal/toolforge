package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.ManagedFile;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.SymbolicName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.netbeans.lib.cvsclient.Client;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.connection.Connection;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.add.AddCommand;
import org.netbeans.lib.cvsclient.command.checkout.CheckoutCommand;

import java.io.File;

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

	private static Log logger = LogFactory.getLog(CVSRunner.class);

	private GlobalOptions globalOptions = new GlobalOptions();
	private Client client = null;
	private CVSResponseAdapter adapter = new CVSResponseAdapter();

	/**
	 * Constructs a runner to fire commands on a CVS repository. A typical client for a <code>CVSRunner</code> instance is
	 * a {@link Command} implementation, as that one knows what to fire away on CVS.
	 *
	 * @param location A <code>Location</code> instance (typically a <code>CVSLocationImpl</code> instance), containing
	 *                 the location and connection details of the CVS repository.
	 */
	public CVSRunner(Location location) throws CVSException {

    CVSLocationImpl cvsLocation = null;
		try {
			cvsLocation = ((CVSLocationImpl) location);
		} catch (ClassCastException e) {
			logger.error("Wrong type for location. Should be CVSLocationImpl.",e);
		}

		logger.debug("Initializing CVS client for location : " + location.toString());

		Connection connection = cvsLocation.getConnection();
		try {
			connection.open();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}


		// Initialize a CVS client
		//
		client = new Client(connection, new StandardAdminHandler());

		// A CVSResponseAdapter is registered as a listener for the response from CVS. This one adapts to Karma
		// specific stuff.
		//
		client.getEventManager().addCVSListener(adapter);

		// As per the API. Sets the location path for the client, the cvs command context path.
		//
		//client.setLocalPath(executionDir.getPath());

		logger.debug("CVSRunner using CVSROOT : " + cvsLocation.toString());

		globalOptions.setCVSRoot(cvsLocation.getCVSRootAsString());
	}

	/**
	 * Executes command on a CVS repository.
	 *
	 * @param command The Karma command
	 *
	 * @return A response object containing the execution result of the CVS session.
	 */
//	public CommandResponse execute(Command command) {
//
//		try {
//			logger.debug("Executing : " + cvsCommand.getCVSCommand() + " in " + executionDir.getPath());
//
//			client.executeCommand(cvsCommand, globals);
//
//			logger.debug("Client finished execution with message code : " + listener.getMessageCode() + " (see CVSMessages).");
//
//			// Determine neat exceptions for error codes
//			//
//			switch (adapter.getMessageCode()) {
//
//				case CVSMessages.MESSAGE_CODE_NO_SUCH_TAG :
//					throw new SymbolicNameNotFoundException("This tag has not been found.");
//				case CVSMessages.MESSAGE_CODE_NO_SUCH_MODULE :
//					throw new ModuleNotInRepositoryException("This module is not in the repository.");
//					/*
//					case CVSMessages.MESSAGE_CODE_NON_CVS_ENTRY :
//					throw new UncommittedWorkException("A local file has been found which is not a CVS entry. Resolve the issue and resume.");
//					*/
//			}
//
//		} catch (CommandException ce) {
//			throw new KarmaRuntimeException(ce);
//		} catch (AuthenticationException ae) {
//			throw new KarmaRuntimeException(ae);
//		}
//
//		return new CVSResponseAdapter();
//	}

	/**
	 * Performs the <code>cvs checkout &lt;module&gt;</code>command.
	 *
	 * @param module
	 * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
	 */
	public CommandResponse checkout(Module module, File checkoutDirectory) {

    CheckoutCommand checkoutCommand = new CheckoutCommand();
    checkoutCommand.setModule(module.getName());
		checkoutCommand.setPruneDirectories(true);
		checkoutCommand.setCheckoutDirectory(checkoutDirectory.getPath());

//		executeOnCVS(checkoutCommand, module.getLocalPath());

		try {
			client.setLocalPath(checkoutDirectory.getPath());

			logger.debug("CVS command : " + checkoutCommand.getCVSCommand());

			client.executeCommand(checkoutCommand, globalOptions);

		} catch (CommandException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * For a module, the <code>cvs -q update -Pd</code>command is executed.
	 *
	 * @param module
	 * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
	 */
	public CommandResponse update(Module module) {
		return null;
	}

	/**
	 * For a module, the <code>cvs commit -m &lt;commitMessage&gt;</code>command is executed.
	 *
	 * @param file
	 * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
	 */
	public CommandResponse commit(ManagedFile file) {
		return null;
	}

	/**
	 * Adds a file to the CVS repository.
	 *
	 * @param module The module that contains the file.
	 * @param filePath The path to the file to add, relative to {@link Module#getLocalPath}.
	 *
	 * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
	 */
	public CommandResponse add(Module module, File filePath) {

		AddCommand addCommand = new AddCommand();
		addCommand.setMessage("Testcase message ... ");

		File fileToAdd = new File(module.getLocalPath().getPath() + File.separator + filePath.getPath());
//		File fileToAdd = new File(module.getLocalPath().getPath() + File.separator + filePath.getPath());
		addCommand.setFiles(new File[]{fileToAdd});

//		executeOnCVS(addCommand, fileToAdd);


		try {
			System.out.println("file to add : " + fileToAdd.getPath());

			client.setLocalPath(fileToAdd.getParent());

			logger.debug("CVS command : " + addCommand.getCVSCommand());

			client.executeCommand(addCommand, globalOptions);
		} catch (CommandException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
		return adapter;
	}

	/**
	 * For a module, the <code>cvs -q update -Pd</code>command is executed.
	 *
	 * @param module
	 * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
	 */
	public CommandResponse commit(Module module) {
		return null;
	}

	public CommandResponse branch(Module module, SymbolicName branch) {
		return null;
	}

	/**
	 * Creates a sticky tag on all files in a module.
	 *
	 * @param module
	 * @param tag
	 * @return
	 */
	public CommandResponse tag(Module module, SymbolicName tag) {
		return null;
	}

//	private void executeOnCVS(org.netbeans.lib.cvsclient.command.Command command, File localPath) {
//
//		logger.debug("CVS command " + command.getCVSCommand() + " in " + localPath);
//
//		try {
//
//			client.setLocalPath(localPath.getParent());
//			client.executeCommand(command, globalOptions);
//
//		} catch (CommandException e) {
//			e.printStackTrace();
//		} catch (AuthenticationException e) {
//			e.printStackTrace();
//		}
//	}

}