package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.history.ModuleHistory;
import nl.toolforge.karma.core.history.ModuleHistoryEvent;
import nl.toolforge.karma.core.history.ModuleHistoryFactory;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <p>Runner class for CVS. Executes stuff on a CVS repository.
 * <p/>
 * <p>TODO : the CVSRunner could be made multi-threaded, to use bandwidth to a remote repository much better ...
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public final class CVSRunner implements Runner {

  static {

    // As per the recommendation ...
    //
    System.setProperty("javacvs.multiple_commands_warning", "false");
  }

  private CVSResponseAdapter listener = null; // The listener that receives events from this runner.

  private static Log logger = LogFactory.getLog(CVSRunner.class);

  private GlobalOptions globalOptions = new GlobalOptions();

  private File basePoint = null;
  private Connection connection = null;

  /**
   * Constructs a runner to fire commands on a CVS repository. A typical client for a <code>CVSRunner</code> instance is
   * a {@link Command} implementation, as that one knows what to fire away on CVS. The runner is instantiated with a
   * <code>location</code> and a <code>manifest</code>. The location must be a <code>CVSLocationImpl</code> instance,
   * reprenting a CVS repository. The manifest is required because it determines the base point from where CVS commands
   * will be run; modules are checked out in a directory structure, relative to
   * {@link nl.toolforge.karma.core.manifest.Manifest#getDirectory()}.
   *
   * @param location  A <code>Location</code> instance (typically a <code>CVSLocationImpl</code> instance), containing
   *                  the location and connection details of the CVS repository.
   * @param basePoint The basePoint determines the base point where cvs commands should be run. If not used by commands and extended,
   *                  this <code>basePoint.getPath()</code> will be used by the CVS client as the
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

    // The default ...
    //
    this.listener = new CVSResponseAdapter();

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
   * Assigns a CommandResponse instance to the runner to optionally promote interactivity.
   *
   * @param response A - possibly <code>null</code> response instance.
   */
  public void setCommandResponse(CommandResponse response) {
    listener = new CVSResponseAdapter(response);
  }

  /**
   * <p>Creates a module in a CVS repository. This is done through the CVS <code>import</code> command. The basic structure
   * of the module directory is defined by the file <code>module-structure.model</code>, which should be available from
   * the classpath. If the file cannot be located, a basic structure is created:
   * <p/>
   * <ul>
   * <li/>A directory based on <code>module.getName()</code>.
   * <li/>A file in that directory, called <code>module.info</code>.
   * </ul>
   * <p/>
   * <p>After creation, the module is available with the initial version <code>0-0</code>.
   *
   * @param module The module to be created.
   * @throws CVSException Errorcode <code>MODULE_EXISTS_IN_REPOSITORY</code>, when the module already exists on the
   *                      location as specified by the module.
   */
  public void create(Module module, String comment, ModuleLayoutTemplate template) throws CVSException {

    // TODO the initial version should also be made configurable, together with the patterns for modulenames et al.

    if (existsInRepository(module)) {
      throw new CVSException(CVSException.MODULE_EXISTS_IN_REPOSITORY, new Object[]{module.getName(), module.getLocation().getId()});
    }

    // Step 1 : create an empty module structure
    //
    ImportCommand importCommand = new ImportCommand();
    importCommand.setModule(module.getName());
    importCommand.setLogMessage("Module " + module.getName() + " created automatically by Karma on " + new Date().toString());
    importCommand.setVendorTag("Karma");
    importCommand.setReleaseTag("MAINLINE_0-0");

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

    executeOnCVS(importCommand, moduleDirectory); // Use module as context directory

    // Remove the temporary structure.
    //
    try {
      FileUtils.deleteDirectory(tmp);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Step 2 : checkout the module to be able to create module.info
    //

    // Create another (...) temporary structure
    //
    try {
      tmp = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      throw new KarmaRuntimeException("Panic! Failed to create temporary directory.");
    }

    checkout(module, tmp);

    add(module, template.getFileElements(), template.getDirectoryElements(), tmp);

    //module has been created. Now, create the module history.
    String author = ((CVSLocationImpl) module.getLocation()).getUsername();
    addModuleHistoryEvent(tmp, module, ModuleHistoryEvent.CREATE_MODULE_EVENT, Version.INITIAL_VERSION, new Date(), author, comment);

    tag(module, Version.INITIAL_VERSION, new File(tmp, module.getName()));

    // Remove the temporary structure.
    //
    try {
      FileUtils.deleteDirectory(tmp);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Performs the <code>cvs checkout [-r &lt;symbolic-name&gt;] &lt;module&gt;</code>command for a module.
   * <code>version</code> is used when not <code>null</code> to checkout a module with a symbolic name.
   *
   * @param module  The module to check out.
   * @param version The version number for the module to check out.
   * @throws CVSException With errorcodes <code>NO_SUCH_MODULE_IN_REPOSITORY</code> when the module does not exist
   *                      in the repository and <code>INVALID_SYMBOLIC_NAME</code>, when the version does not exists for the module.
   */
  public void checkout(Module module, Version version) throws CVSException {

    checkout(module, version, getBasePoint());
  }

  /**
   * See {@link #checkout(Module, Version)}. This method defaults to the HEAD of the development branch at hand.
   *
   * @param module The module to check out.
   * @throws CVSException With errorcodes <code>NO_SUCH_MODULE_IN_REPOSITORY</code> when the module does not exist
   *                      in the repository and <code>INVALID_SYMBOLIC_NAME</code>, when the version does not exists for the module.
   */
  public void checkout(Module module) throws CVSException {
    checkout(module, null, getBasePoint());
  }

  public void checkout(Module module, File basePoint) throws CVSException {
    checkout(module, null, basePoint);
  }

  //
  // For the time being, private
  //
  private void checkout(Module module, Version version, File basePoint) throws CVSException {

    Map arguments = new Hashtable();
    arguments.put("MODULE", module.getName());
    arguments.put("REPOSITORY", module.getLocation().getId());

    CheckoutCommand checkoutCommand = new CheckoutCommand();
    checkoutCommand.setModule(module.getName());

    if (version != null || ((SourceModule) module).hasPatchLine()) {
      checkoutCommand.setCheckoutByRevision(Utils.createSymbolicName(module, version).getSymbolicName());
      if (version != null) {
        arguments.put("VERSION", version.toString());
      }
    } else {
      checkoutCommand.setResetStickyOnes(true);
    }
    // NOTE : the following may not be included. My theory is there is a bug in the netbeans api.
    // todo to be investigated further.
    //    checkoutCommand.setPruneDirectories(true);

    executeOnCVS(checkoutCommand, basePoint, arguments);
  }

  /**
   * For a module, the <code>cvs -q update -Pd -r &lt;symbolic-name&gt;</code> command is executed.
   *
   * @param module  The module that should be updated.
   * @param version The version of the module or <code>null</code> when no specific version applies.
   */
  public void update(Module module, Version version) throws CVSException {
    update(module, version, basePoint);
  }

  public void update(Module module) throws CVSException {
    update(module, null, basePoint);
  }

  private void update(Module module, Version version, File basePoint) throws CVSException {

    Map arguments = new Hashtable();
    arguments.put("MODULE", module.getName());
    arguments.put("REPOSITORY", module.getLocation().getId());

    UpdateCommand updateCommand = new UpdateCommand();
    updateCommand.setRecursive(true);
    updateCommand.setPruneDirectories(true);

    if (version != null || ((SourceModule) module).hasPatchLine()) {
      updateCommand.setUpdateByRevision(Utils.createSymbolicName(module, version).getSymbolicName());
      if (version != null) {
        arguments.put("VERSION", version.toString());
      }
    } else {
      updateCommand.setResetStickyOnes(true);
    }

    // todo add data to the exception. this sort of business logic should be here, not in CVSResponseAdapter.
    //
    executeOnCVS(updateCommand, new File(basePoint, module.getName()), arguments);
  }

  /**
   * For a module, the <code>cvs commit -m &lt;commitMessage&gt;</code>command is executed.
   *
   */
  public void commit(ManagedFile file, String message) throws CVSException {
//		null;
  }

  public void add(Module module, String[] files, String[] dirs) throws CVSException {
    add(module, files, dirs, getBasePoint());
  }

  public void add(Module module, File[] files, File[] dirs) throws CVSException {

    String[] f = (files == null ? new String[0] : new String[files.length]);
    String[] d = (dirs == null ? new String[0] : new String[dirs.length]);

    for (int i = 0; i < f.length; i++) {
      f[i] = files[i].getPath();
    }
    for (int i = 0; i < d.length; i++) {
      d[i] = dirs[i].getPath();
    }

    add(module, f, d, getBasePoint());
  }

  // todo should possibly be moved to the interface (check the rest as well).
  public synchronized void add(Module module, String[] files, String[] dirs, File basePoint) throws CVSException {

    files = (files == null ? new String[] {} : files);
    dirs = (dirs == null ? new String[] {} : dirs);

    Map arguments = new Hashtable();
    arguments.put("MODULE", module.getName());

    // Step 1 : Add the file to the CVS repository
    //
    AddCommand addCommand = new AddCommand();
    addCommand.setMessage("Initial checkin in repository.");

    File modulePath = new File(basePoint, module.getName());

    // Create temp files
    //
    Collection cvsFilesCollection = new ArrayList();
    int i,j = 0;
    for (i=0; i < files.length; i++) {
      File fileToAdd = new File(modulePath, files[i]);

      if (!fileToAdd.exists()) {
        try {
          File dir = new File(modulePath, files[i]).getParentFile();

          if (dir.mkdirs()) {
            cvsFilesCollection.add(dir);
            j++;
          }

          fileToAdd.createNewFile();
          logger.debug("Created file " + files[i] + " for module " + module.getName() + ".");
        } catch (IOException e) {
          throw new KarmaRuntimeException("Error while creating module layout for module " + module.getName());
        }
      }

      cvsFilesCollection.add(fileToAdd);
      j++;
    }

    // Create temp directories
    //
    for (i=0; i < dirs.length; i++) {
      File dirToAdd = new File(modulePath, dirs[i]);

      if (!dirToAdd.mkdirs()) {
        throw new KarmaRuntimeException("Error while creating module layout for module " + module.getName());
      }
      logger.debug("Created directory " + dirs[i] + " for module " + module.getName() + ".");

      // Ensure that all directories are added correctly
      //
      StringTokenizer tokenizer = new StringTokenizer(dirs[i], "/");
      String base = "";
      while (tokenizer.hasMoreTokens()) {
        String subDir = tokenizer.nextToken();
        base += subDir;
        cvsFilesCollection.add(new File(modulePath, base));
        base += "/";
        j++;
      }
    }

    File[] cvsFiles = (File[]) cvsFilesCollection.toArray(new File[cvsFilesCollection.size()]);
    addCommand.setFiles(cvsFiles);

    // A file is added against a module, thus the contextDirectory is constructed based on the basePoint and the
    // modules' name.
    //
    executeOnCVS(addCommand, new File(basePoint, module.getName()), arguments);

    // Step 2 : Commit the file to the CVS repository
    //
    CommitCommand commitCommand = new CommitCommand();
    commitCommand.setFiles(cvsFiles);
    commitCommand.setMessage("File added automatically by Karma.");

    executeOnCVS(commitCommand, new File(basePoint, module.getName()));

  }

  /**
   * For a module, the <code>cvs commit -m "&lt;some-message&gt;"</code>command is executed.
   *
   * @param module  The module. Will be committed recursively.
   * @param message A commit message.
   */
  public void commit(Module module, String message) throws CVSException {

    commit(module, message, getBasePoint());
  }

  private void commit(Module module, String message, File basePoint) throws CVSException {

    CommitCommand commitCommand = new CommitCommand();

    commitCommand.setFiles(new File[]{new File(basePoint, module.getName())});
    commitCommand.setRecursive(true);
    commitCommand.setMessage(message);

    executeOnCVS(commitCommand, basePoint);
  }


  public void branch(Module module, SymbolicName branch) throws CVSException {
  }

  public void promote(Module module, String comment, Version version) throws CVSException {

    //Add an event to the module history.
    String author = ((CVSLocationImpl) module.getLocation()).getUsername();
    addModuleHistoryEvent(getBasePoint(), module, ModuleHistoryEvent.PROMOTE_MODULE_EVENT, version, new Date(), author, comment);

    File moduleLocation = new File(getBasePoint(), module.getName());
    tag(module, version, moduleLocation);
  }

  private void tag(Module module, Version version, File basePoint) throws CVSException {
    if (hasVersion(module, version)) {
      throw new CVSException(VersionControlException.DUPLICATE_VERSION, new Object[]{module.getName(), version.getVersionNumber()});
    }
    tag(Utils.createSymbolicName(module, version), basePoint);
  }

  //
  // Private for the time being
  //
  private void tag(SymbolicName symbolicName, File basePoint) throws CVSException {

    TagCommand tagCommand = new TagCommand();
    tagCommand.setRecursive(true);
    tagCommand.setTag(symbolicName.getSymbolicName());

    executeOnCVS(tagCommand, basePoint);
  }

  /**
   * Provide log information on a module. This method checks if the
   * {@link #setCommandResponse(nl.toolforge.karma.core.cmd.CommandResponse)} method has been called, which results in
   * this runner initializing the CVS API with the correct objects. This is a requirement for the Netbeans CVS API.
   */
  public LogInformation log(Module module) throws CVSException {

    if (!(this.listener instanceof CVSResponseAdapter)) {
      // This stuff sucks, but is a good reminder.
      // todo aspects ?
      //
      throw new KarmaRuntimeException(
          "Due to the way the Netbeans API works, the CVSRunner must be initialized with a 'CommandResponse' object.");
    }

    if (!(module instanceof SourceModule)) {
      throw new KarmaRuntimeException("Only instances of type SourceModule can use this method.");
    }

    // Logs are run on a temporary checkout of the module.info of a module.
    //
    File tmp = null;
    try {
      tmp = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      throw new KarmaRuntimeException("Panic! Failed to create temporary directory for module " + module.getName());
    }

    Map arguments = new Hashtable();
    arguments.put("MODULE", module.getName());
    arguments.put("REPOSITORY", module.getLocation().getId());

    CheckoutCommand checkoutCommand = new CheckoutCommand();
    checkoutCommand.setModule(module.getName() + "/" + Module.MODULE_INFO);

    executeOnCVS(checkoutCommand, tmp, arguments);

    LogCommand logCommand = new LogCommand();

    // Determine the location of module.info, relative to where we are.
    //
    // Todo a reference to SourceModule is used here. Verify ...
    File moduleInfo = new File(new File(tmp, module.getName()), Module.MODULE_INFO);
    logCommand.setFiles(new File[]{moduleInfo});

    executeOnCVS(logCommand, new File(tmp, module.getName()));

    try {
      FileUtils.deleteDirectory(tmp);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return ((CVSResponseAdapter) this.listener).getLogInformation();
  }

  /**
   * A check if a module exists is done by trying to checkout the modules' <code>module.info</code> file in a temporary
   * location. If that succeeds, apparently the module exists in that location and we have a <code>true</code> to
   * return.
   */
  public boolean existsInRepository(Module module) {

    if (module == null) {
      return false;
    }

    Map arguments = new Hashtable();
    arguments.put("MODULE", module.getName());
    arguments.put("REPOSITORY", module.getLocation().getId());

    CheckoutCommand checkoutCommand = new CheckoutCommand();
    checkoutCommand.setModule(module.getName() + "/" + Module.MODULE_INFO);

    File tmp = null;
    try {
      tmp = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      throw new KarmaRuntimeException("Panic! Failed to create temporary directory for module " + module.getName());
    }

    try {
      executeOnCVS(checkoutCommand, tmp);
    } catch (CVSException e) {
      if (e.getErrorCode().equals(CVSException.MODULE_EXISTS_IN_REPOSITORY)) {
        // If the module already exists in the repository.
        //
        return true;
      }
      return false;
    }

    File moduleDirectory = new File(tmp, module.getName());

    // todo het volgende is ronduit kutcode ....
    //
    if (moduleDirectory.exists()) {
      try {
        FileUtils.deleteDirectory(tmp);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return true;
    } else {
      try {
        FileUtils.deleteDirectory(tmp);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return false;
    }
  }

  // todo hmm, do we want his here ??? For the time being ... yes.
  //
  public final UpdateParser getUpdateParser() {
    return listener.getUpdateParser();
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

  private void executeOnCVS(org.netbeans.lib.cvsclient.command.Command command,
                            File contextDirectory) throws CVSException {
    executeOnCVS(command, contextDirectory, null);
  }

  /**
   * Runs a CVS command on the repository (through the Netbeans API). contextDirectory is assigned to client.setLocalPath()
   *
   * @param command          A command object, representing the CVS command.
   * @param contextDirectory The directory from where the command should be run.
   * @param args             Arguments that can be passed to the CVSRuntimeException thrown by the CVSListener.
   *
   * @throws CVSException    When CVS has reported an error through its listener.
   */
  private void executeOnCVS(org.netbeans.lib.cvsclient.command.Command command,
                            File contextDirectory, Map args) throws CVSException {

    Client client = new Client(getConnection(), new StandardAdminHandler());
    client.setLocalPath(contextDirectory.getPath());

    logger.debug("Running CVS command : '" + command.getCVSCommand() + "' in " + client.getLocalPath());

    try {
      // A CVSResponseAdapter is registered as a listener for the response from CVS. This one adapts to Karma
      // specific stuff.
      //
      listener.setArguments(args);
      client.getEventManager().addCVSListener(listener);
      client.executeCommand(command, globalOptions);

    } catch (CommandException e) {
//      e.printStackTrace();
      logger.debug(e);
      // Trick to get a hold of the exception we threw in the CVSResponseAdapter.
      //
      if (e.getUnderlyingException() instanceof CVSRuntimeException) {

        // todo somehow, messagearguments should be added ...
        ErrorCode code = ((CVSRuntimeException) e.getUnderlyingException()).getErrorCode();
        Object[] messageArgs = ((CVSRuntimeException) e.getUnderlyingException()).getMessageArguments();

        throw new CVSException(code, messageArgs);
      } else {
        throw new CVSException(CVSException.INTERNAL_ERROR, new Object[]{globalOptions.getCVSRoot()});
      }
    } catch (AuthenticationException e) {
      throw new CVSException(CVSException.AUTHENTICATION_ERROR, new Object[]{client.getConnection()});
    } finally {
      // See the static block in this class and corresponding documentation.
      //
      try {
        client.getConnection().close();
      } catch (IOException e) {
        throw new CVSException(CVSException.INTERNAL_ERROR);
      }
    }
  }

  /**
   * Helper method that add a new event to a module's history. When the history does not
   * exist yet (in case of a new module) it is newly created. When the history does exist
   * the event is added to the history.
   *
   * @param moduleCheckoutLocation  The location on disk where the module has been checked out.
   * @param module                  The module involved.
   * @param eventType               The type of {@link ModuleHistoryEvent}.
   * @param version                 The version that the module is promoted to.
   * @param datetime                The timestamp.
   * @param author                  The authentication of the person who has triggered the event.
   * @param comment                 The (optional) comment the author has given.
   * @throws CVSException           Thrown in case something goes wrong with CVS
   */
  private void addModuleHistoryEvent(
      File moduleCheckoutLocation,
      Module module,
      String eventType,
      Version version,
      Date datetime,
      String author,
      String comment) throws CVSException
  {
    ModuleHistoryFactory factory = ModuleHistoryFactory.getInstance(moduleCheckoutLocation);
    ModuleHistory history = factory.getModuleHistory(module.getName());
    if (history != null) {
      ModuleHistoryEvent event = new ModuleHistoryEvent();
      event.setType(eventType);
      event.setVersion(version);
      event.setDatetime(datetime);
      event.setAuthor(author);
      event.setComment(comment);
      history.addEvent(event);
      if (history.getHistoryLocation().exists()) {
        //history already exists. commit changes.
        //todo: add comment
        history.save();
        commit(module, "");
      } else {
        //history did not exist yet. add to CVS and commit it.
        //todo: add comment
        history.save();
        add(module, new String[]{history.getHistoryLocation().getName()}, null, moduleCheckoutLocation);
      }
    }
  }

}
