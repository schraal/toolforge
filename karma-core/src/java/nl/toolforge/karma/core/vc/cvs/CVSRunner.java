package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.KarmaRuntimeException;
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
import org.netbeans.lib.cvsclient.command.commit.CommitCommand;
import org.netbeans.lib.cvsclient.command.add.AddCommand;
import org.netbeans.lib.cvsclient.command.checkout.CheckoutCommand;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;

import java.io.File;
import java.io.IOException;

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

    // Initialize a CVS client
    //
    client = new Client( cvsLocation.getConnection(), new StandardAdminHandler());

    // A CVSResponseAdapter is registered as a listener for the response from CVS. This one adapts to Karma
    // specific stuff.
    //
    client.getEventManager().addCVSListener(adapter);

    logger.debug("CVSRunner using CVSROOT : " + cvsLocation.toString());
    globalOptions.setCVSRoot(cvsLocation.getCVSRootAsString());
  }

  /**
   * Performs the <code>cvs checkout &lt;module&gt;</code>command for a module in a specific
   * <code>checkoutDirectory</code>. Use {@link #checkout(nl.toolforge.karma.core.Module)} to checkout the module
   * in the default checkout directory.
   *
   * @param module The module.
   * @param checkoutDirectory The absolute directory where the module should be checked out.
   *
   * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
   */
  public CommandResponse checkout(Module module, File checkoutDirectory) {

    CheckoutCommand checkoutCommand = new CheckoutCommand();
    checkoutCommand.setModule(module.getName());
    checkoutCommand.setPruneDirectories(true);

    executeOnCVS(checkoutCommand, module.getLocalPath().getPath());

    return adapter;
  }

  /**
   * Checks out the module in its default checkout directory.
   *
   * @param module
   * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
   */
  public CommandResponse checkout(Module module) {

    return checkout(module, module.getLocalPath());
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
   * Adds a file to the CVS repository and implicitely commits the file (well, it tries to do so).
   *
   * @param module The module that contains the file.
   * @param fileName The fileName to add, relative to {@link Module#getLocalPath}.
   *
   * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
   */
  public CommandResponse add(Module module, String fileName) {

    // Step 1 : Add the file to the CVS repository
    //
    AddCommand addCommand = new AddCommand();
    addCommand.setMessage("Testcase message ... ");

    File fileToAdd = new File(module.getLocalPath().getPath() + File.separator + fileName);
    if (!fileToAdd.exists()) {
      try {
        fileToAdd.createNewFile();
      } catch (IOException e) {
        throw new KarmaRuntimeException("Could not create " + fileName + " in " + module.getLocalPath().getPath());
      }
    }

    addCommand.setFiles(new File[]{fileToAdd});

    executeOnCVS(addCommand, fileToAdd.getParent());

    // Step 2 : Commit the file to the CVS repository
    //
    CommitCommand commitCommand = new CommitCommand();
    commitCommand.setFiles(new File[]{fileToAdd});

    executeOnCVS(commitCommand, fileToAdd.getParent());

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

  private void executeOnCVS(org.netbeans.lib.cvsclient.command.Command command, String contextDirectory) {

    logger.debug("CVS command " + command.getCVSCommand() + " in " + contextDirectory);

    try {

      client.setLocalPath(contextDirectory);
      client.executeCommand(command, globalOptions);

    } catch (CommandException e) {
      e.printStackTrace();
    } catch (AuthenticationException e) {
      e.printStackTrace();
    }
  }

}