package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.SourceModule;
import nl.toolforge.karma.core.KarmaException;
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
import org.netbeans.lib.cvsclient.command.log.LogInformation;
import org.netbeans.lib.cvsclient.command.log.LogCommand;
import org.netbeans.lib.cvsclient.command.update.UpdateCommand;
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

  static {

    // As per the recommendation ...
    //
    System.setProperty("javacvs.multiple_commands_warning", "false");
  }

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
   //   * @param checkoutDirectory The absolute directory where the module should be checked out. Overwrites the setting
   //   *   done when instantiating this runner (see {@link #CVSRunner}.
   *
   * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
   */
  public CommandResponse checkout(Module module) {
//  public CommandResponse checkout(Module module, File checkoutDirectory) {

//    if (checkoutDirectory == null) {
//      throw new IllegalArgumentException("Parameter checkoutDirectory should not be null.");
//    }

    CheckoutCommand checkoutCommand = new CheckoutCommand();
    checkoutCommand.setModule(module.getName());
    checkoutCommand.setPruneDirectories(true);

    // TODO The manifest-name should prepend the module.

    //checkoutDirectory.mkdirs(); // if not yet existing, it will be created

    //client.setLocalPath(checkoutDirectory.getPath());

    executeOnCVS(checkoutCommand, null);

    return adapter;
  }

  /**
   * Checks out the module in its default checkout directory.
   *
   * @param module
   * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
   */
//  public CommandResponse checkout(Module module) {
//
//    return checkout(module, null);
//  }

  /**
   * For a module, the <code>cvs -q update -Pd</code>command is executed.
   *
   * @param module
   * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
   */
  public CommandResponse update(Module module) {

    UpdateCommand updateCommand = new UpdateCommand();
    updateCommand.setRecursive(true);
    updateCommand.setPruneDirectories(true);

    // TODO The manifest-name should prepend the module.

    //checkoutDirectory.mkdirs(); // if not yet existing, it will be created

    //client.setLocalPath(checkoutDirectory.getPath());

    executeOnCVS(updateCommand, module.getName());

    return adapter;
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
   * @param fileName The fileName to add, relative to <code>contextDirectory</code>, which was set when instantiating
   *   this runner.
   *
   * @return The CVS response wrapped in a <code>CommandResponse</code>. ** TODO extend comments **
   */
  public CommandResponse add(Module module, String fileName) {

    // Step 1 : Add the file to the CVS repository
    //
    AddCommand addCommand = new AddCommand();
    addCommand.setMessage("Testcase message ... ");

    String path = client.getLocalPath() + File.separator + module.getName();
    File fileToAdd = new File(path + File.separator + fileName);
    if (!fileToAdd.exists()) {
      try {
        fileToAdd.createNewFile();
      } catch (IOException e) {
        throw new KarmaRuntimeException("Could not create " + fileName + " in " + path);
      }
    }

    addCommand.setFiles(new File[]{fileToAdd});

    executeOnCVS(addCommand, module.getName());

    // Step 2 : Commit the file to the CVS repository
    //
    CommitCommand commitCommand = new CommitCommand();
    commitCommand.setFiles(new File[]{fileToAdd});

    executeOnCVS(commitCommand, null);

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
  private void executeOnCVS(org.netbeans.lib.cvsclient.command.Command command, String contextDirectory) {

    try {

      if (contextDirectory != null) {
        client.setLocalPath(client.getLocalPath() + File.separator + contextDirectory);
      }
      logger.debug("CVS command " + command.getCVSCommand() + " in " + client.getLocalPath());
      client.executeCommand(command, globalOptions);

    } catch (CommandException e) {
      e.printStackTrace();
    } catch (AuthenticationException e) {
      e.printStackTrace();
    }
  }

}