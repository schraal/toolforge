/*
Karma core - Core of the Karma application
Copyright (C) 2004  Toolforge <www.toolforge.nl>

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package nl.toolforge.karma.core.vc.cvs;

import nl.toolforge.core.util.file.MyFileUtils;
import nl.toolforge.karma.core.ErrorCode;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.history.ModuleHistory;
import nl.toolforge.karma.core.history.ModuleHistoryEvent;
import nl.toolforge.karma.core.history.ModuleHistoryException;
import nl.toolforge.karma.core.history.ModuleHistoryFactory;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.manifest.util.FileTemplate;
import nl.toolforge.karma.core.manifest.util.ModuleLayoutTemplate;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.PatchLine;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
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
  private Connection connection = null;

  /**
   * Constructs a runner to fire commands on a CVS repository. A typical client for a <code>CVSRunner</code> instance is
   * a {@link Command} implementation, as that one knows what to fire away on CVS. The runner is instantiated with a
   * <code>location</code> and a <code>manifest</code>. The location must be a <code>CVSLocationImpl</code> instance,
   * reprenting a CVS repository. The manifest is required because it determines the base point from where CVS commands
   * will be run; modules are checked out in a directory structure, relative to
   * {@link nl.toolforge.karma.core.manifest.Manifest#getBaseDirectory()}.
   *
   * @param location  A <code>Location</code> instance (typically a <code>CVSLocationImpl</code> instance), containing
   *   the location and connection details of the CVS repository.
   * @throws CVSException <code>CONNECTION_EXCEPTION</code> is thrown when <code>location</code> cannot be reached
   *   (remote locations).
   */
  public CVSRunner(Location location) throws CVSException {

    CVSLocationImpl cvsLocation = null;
    try {
      cvsLocation = ((CVSLocationImpl) location);
    } catch (ClassCastException e) {
      logger.error("Wrong type for location. Should be CVSLocationImpl.", e);
      throw new KarmaRuntimeException("Wrong type for location. Should be CVSLocationImpl.", e);
    }

    this.connection = cvsLocation.getConnection();

    // The default ...
    //
    this.listener = new CVSResponseAdapter();

    logger.debug("CVSRunner using CVSROOT : " + cvsLocation.toString());
    globalOptions.setCVSRoot(cvsLocation.getCVSRootAsString());
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

    executeOnCVS(importCommand, moduleDirectory, null); // Use module as context directory

    // Remove the temporary structure.
    //
    try {
      FileUtils.deleteDirectory(tmp);
    } catch (IOException e) {
      throw new KarmaRuntimeException(e.getMessage());
    }

    // Step 2 : checkout the module to be able to create module.info
    //
    try {
      tmp = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      throw new KarmaRuntimeException("Panic! Failed to create temporary directory.");
    }
    module.setBaseDir(new File(tmp, module.getName()));

    checkout(module, null, null);

    //copy the file templates here
    try {
      FileTemplate[] fileTemplates = template.getFileElements();
      String[] templateFiles = new String[fileTemplates.length];
      for (int i = 0; i < fileTemplates.length; i++) {
        FileTemplate fileTemplate = fileTemplates[i];
        Reader input = new BufferedReader(new InputStreamReader(CVSRunner.class.getResourceAsStream(fileTemplate.getSource().toString())));
        File outputFile = new File(module.getBaseDir() + File.separator + fileTemplate.getTarget());
        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();
        FileOutputStream output = new FileOutputStream(outputFile);
        while (input.ready()) {
          output.write(input.read());
        }
        templateFiles[i] = fileTemplate.getTarget().getPath();
      }

      add(module, templateFiles, template.getDirectoryElements());
    } catch (Exception e) {
      logger.error(e);
      throw new CVSException(CVSException.TEMPLATE_CREATION_FAILED);
    }

    //module has been created. Now, create the module history.
    try {
      String author = ((CVSLocationImpl) module.getLocation()).getUsername();
      addModuleHistoryEvent(null, module, ModuleHistoryEvent.CREATE_MODULE_EVENT, Version.INITIAL_VERSION, new Date(), author, comment);

      tag(module, Version.INITIAL_VERSION);
    } catch (ModuleHistoryException mhe) {
      //writing the module history failed.
      //the module will not be tagged, since it is invalid by default.
      logger.error("Creating the module history failed.", mhe);
      throw new CVSException(CVSException.MODULE_HISTORY_ERROR, new Object[]{mhe.getMessage()});
    } finally {
      try {
        FileUtils.deleteDirectory(tmp);
      } catch (IOException e) {
        throw new KarmaRuntimeException(e.getMessage());
      }
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

    checkout(module, null, version);
  }

  /**
   * See {@link #checkout(Module, Version)}. This method defaults to the HEAD of the development branch at hand.
   *
   * @param module The module to check out.
   * @throws CVSException With errorcodes <code>NO_SUCH_MODULE_IN_REPOSITORY</code> when the module does not exist
   *                      in the repository and <code>INVALID_SYMBOLIC_NAME</code>, when the version does not exists for the module.
   */
  public void checkout(Module module) throws CVSException {
    checkout(module, null, null);
  }

  public void checkout(Module module, DevelopmentLine developmentLine, Version version) throws CVSException {
//    checkout(module, developmentLine, version, module.getBaseDir());
//  }
//
//  private void checkout(Module module, DevelopmentLine developmentLine, Version version, File baseDir) throws CVSException {

    Map arguments = new Hashtable();
    arguments.put("MODULE", module.getName());
    arguments.put("REPOSITORY", module.getLocation().getId());

    CheckoutCommand checkoutCommand = new CheckoutCommand();
    checkoutCommand.setModule(module.getName());

    if ((version != null) || (developmentLine != null)) {
      checkoutCommand.setCheckoutByRevision(Utils.createSymbolicName(module, developmentLine, version).getSymbolicName());
      if (version != null) {
        arguments.put("VERSION", version.toString());
      }
    } else {
      checkoutCommand.setResetStickyOnes(true);
    }
    // NOTE : the following may not be included. My theory is there is a bug in the netbeans api.
    // todo to be investigated further.
    //    checkoutCommand.setPruneDirectories(true);

    executeOnCVS(checkoutCommand, module.getBaseDir().getParentFile(), arguments);
  }


  public void update(Module module) throws CVSException {
    update(module, null);
  }

  /**
   * For a module, the <code>cvs -q update -Pd -r &lt;symbolic-name&gt;</code> command is executed.
   *
   * @param module  The module that should be updated.
   * @param version The version of the module or <code>null</code> when no specific version applies.
   */
  public void update(Module module, Version version) throws CVSException {

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
    executeOnCVS(updateCommand, module.getBaseDir(), arguments);
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

    add(module, f, d);
  }

  public void add(Module module, String[] files, String[] dirs) throws CVSException {

    files = (files == null ? new String[] {} : files);
    dirs = (dirs == null ? new String[] {} : dirs);

    Map arguments = new Hashtable();
    arguments.put("MODULE", module.getName());

    // Step 1 : Add the file to the CVS repository
    //
    AddCommand addCommand = new AddCommand();
    addCommand.setMessage("Initial checkin in repository by Karma.");

    File modulePath = module.getBaseDir();

    // Create temp files
    //
    Collection cvsFilesCollection = new ArrayList();
    int i = 0;
    for (i=0; i < files.length; i++) {
      File fileToAdd = new File(modulePath, files[i]);

      if (!fileToAdd.exists()) {
        try {
          File dir = new File(modulePath, files[i]).getParentFile();

          if (dir.mkdirs()) {
            cvsFilesCollection.add(dir);
          }

          fileToAdd.createNewFile();
          logger.debug("Created file " + files[i] + " for module " + module.getName() + ".");
        } catch (IOException e) {
          throw new KarmaRuntimeException("Error while creating module layout for module " + module.getName());
        }
      }

      cvsFilesCollection.add(fileToAdd);
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
      }
    }

    File[] cvsFiles = (File[]) cvsFilesCollection.toArray(new File[cvsFilesCollection.size()]);
    addCommand.setFiles(cvsFiles);

    // A file is added against a module, thus the contextDirectory is constructed based on the basePoint and the
    // modules' name.
    //
    executeOnCVS(addCommand, module.getBaseDir(), arguments);

    // Step 2 : Commit the file to the CVS repository
    //
    CommitCommand commitCommand = new CommitCommand();
    commitCommand.setFiles(cvsFiles);
    commitCommand.setMessage("File added automatically by Karma.");

    executeOnCVS(commitCommand, module.getBaseDir(), arguments);
  }

  /**
   * For a module, the <code>cvs commit -m "&lt;some-message&gt;"</code>command is executed.
   *
   * @param module  The module. Will be committed recursively.
   * @param message A commit message.
   */
  private void commit(Module module, String message) throws CVSException {

    CommitCommand commitCommand = new CommitCommand();

    commitCommand.setFiles(new File[]{module.getBaseDir()});
    commitCommand.setRecursive(true);
    commitCommand.setMessage(message);

    executeOnCVS(commitCommand, module.getBaseDir(), null);
  }

  private void commit(Module module, File file, String message) throws CVSException {
    commit(null, module, file, message);
  }

  private void commit(DevelopmentLine developmentLine, Module module, File file, String message) throws CVSException {

    CommitCommand commitCommand = new CommitCommand();

    commitCommand.setFiles(new File[]{file});
    if (developmentLine != null) {
      commitCommand.setToRevisionOrBranch(developmentLine.getName());
    }
    commitCommand.setMessage(message);

    executeOnCVS(commitCommand, module.getBaseDir(), null);
  }

  public void promote(Module module, String comment, Version version) throws CVSException {

    try {
      //Add an event to the module history.
      String author = ((CVSLocationImpl) module.getLocation()).getUsername();
      addModuleHistoryEvent(null, module, ModuleHistoryEvent.PROMOTE_MODULE_EVENT, version, new Date(), author, comment);

      tag(module, version);
    } catch (ModuleHistoryException mhe) {
      logger.error("Writing the history.xml failed", mhe);
      throw new CVSException(CVSException.MODULE_HISTORY_ERROR, new Object[]{mhe.getMessage()});
    }
  }

  private void tag(Module module, Version version) throws CVSException {
    if (hasVersion(module, version)) {
      throw new CVSException(VersionControlException.DUPLICATE_VERSION, new Object[]{module.getName(), version.getVersionNumber()});
    }
    tag(module, Utils.createSymbolicName(module, version), false);
  }

  //
  // Private for the time being
  //
  private void tag(Module module, SymbolicName symbolicName, boolean branch) throws CVSException {

    TagCommand tagCommand = new TagCommand();
    tagCommand.setRecursive(true);
    tagCommand.setTag(symbolicName.getSymbolicName());
    tagCommand.setMakeBranchTag(branch);

    executeOnCVS(tagCommand, module.getBaseDir(), null);
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

      Map arguments = new Hashtable();
      arguments.put("MODULE", module.getName());
      arguments.put("REPOSITORY", module.getLocation().getId());

      CheckoutCommand checkoutCommand = new CheckoutCommand();
//      checkoutCommand.setModule(module.getName() + "/" + Module.MODULE_INFO);
      checkoutCommand.setModule(module.getName() + "/" + Module.MODULE_DESCRIPTOR);

      executeOnCVS(checkoutCommand, tmp, arguments);

      LogCommand logCommand = new LogCommand();

      // Determine the location of module.info, relative to where we are.
      //
      // Todo a reference to SourceModule is used here. Verify ...
//      File moduleInfo = new File(new File(tmp, module.getName()), Module.MODULE_INFO);
      File moduleInfo = new File(new File(tmp, module.getName()), Module.MODULE_DESCRIPTOR);
      logCommand.setFiles(new File[]{moduleInfo});

      executeOnCVS(logCommand, new File(tmp, module.getName()), arguments);

    } catch (IOException e) {
      throw new KarmaRuntimeException("Panic! Failed to create temporary directory for module " + module.getName());
    } finally {
      try {
        FileUtils.deleteDirectory(tmp);
      } catch (IOException e) {
        throw new KarmaRuntimeException(e.getMessage());
      }
    }

    return ((CVSResponseAdapter) this.listener).getLogInformation();
  }

  /**
   * Checks if the module has a CVS branch tag <code>module.getPatchLine().getName()</code> attached.
   *
   * @param module The module for which the patch line should be checked.
   * @return <code>true</code> of the module has a patch line attached in the CVS repository, <code>false</code>
   *         otherwise.
   */
  public boolean hasPatchLine(Module module) {
    try {
      return hasSymbolicName(module, new CVSTag(new PatchLine(module.getVersion()).getName()));
    } catch (CVSException c) {
      return false;
    }
  }

  public void createPatchLine(Module module) throws CVSException {
    try {
      //Add an event to the module history.
      String author = ((CVSLocationImpl) module.getLocation()).getUsername();
      tag(module, new CVSTag(module.getPatchLine().getName()), true);

      addModuleHistoryEvent(module.getPatchLine(), module, ModuleHistoryEvent.CREATE_PATCH_LINE_EVENT, module.getVersion(), new Date(), author, "Patch line created by Karma");

    } catch (ModuleHistoryException mhe) {
      logger.error("Writing the history.xml failed", mhe);
      throw new CVSException(CVSException.MODULE_HISTORY_ERROR, new Object[]{mhe.getMessage()});
    }
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
//    checkoutCommand.setModule(module.getName() + "/" + Module.MODULE_INFO);
    checkoutCommand.setModule(module.getName() + "/" + Module.MODULE_DESCRIPTOR);

    File tmp = null;
    try {
      tmp = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      throw new KarmaRuntimeException("Panic! Failed to create temporary directory for module " + module.getName());
    }

    try {
      executeOnCVS(checkoutCommand, tmp, arguments);
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
        throw new KarmaRuntimeException(e);
      }
      return true;
    } else {
      try {
        FileUtils.deleteDirectory(tmp);
      } catch (IOException e) {
        throw new KarmaRuntimeException(e);
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
    List symbolicNames = new ArrayList();

    for (Iterator i = logInfo.getAllSymbolicNames().iterator(); i.hasNext();) {
      symbolicNames.add(((LogInformation.SymName) i.next()).getName());
    }

    return symbolicNames.contains(symbolicName.getSymbolicName());
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
        throw new CVSException(CVSException.INTERNAL_ERROR, new Object[]{globalOptions.getCVSRoot()});
      }
    }
  }

  /**
   * Helper method that add a new event to a module's history. When the history does not
   * exist yet (in case of a new module) it is newly created. When the history does exist
   * the event is added to the history.
   *
   * @param developmentLine  The location on disk where the module has been checked out.
   * @param module                  The module involved.
   * @param eventType               The type of {@link ModuleHistoryEvent}.
   * @param version                 The version that the module is promoted to.
   * @param datetime                The timestamp.
   * @param author                  The authentication of the person who has triggered the event.
   * @param comment                 The (optional) comment the author has given.
   * @throws CVSException           Thrown in case something goes wrong with CVS
   */
  private void addModuleHistoryEvent(
      DevelopmentLine developmentLine,
      Module module,
      String eventType,
      Version version,
      Date datetime,
      String author,
      String comment) throws CVSException, ModuleHistoryException
  {
    ModuleHistoryFactory factory = ModuleHistoryFactory.getInstance(module.getBaseDir());
    ModuleHistory history = factory.getModuleHistory(module);
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
        history.save();
//        commit(module, "History updated by Karma");
        commit(developmentLine, module, new File(module.getBaseDir(), ModuleHistory.MODULE_HISTORY_FILE_NAME), "History updated by Karma");
      } else {
        //history did not exist yet. add to CVS and commit it.
        history.save();
        add(module, new String[]{history.getHistoryLocation().getName()}, null);
      }
    }
  }

}
