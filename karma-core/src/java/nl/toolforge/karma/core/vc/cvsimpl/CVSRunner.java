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
package nl.toolforge.karma.core.vc.cvsimpl;

import net.sf.sillyexceptions.OutOfTheBlueException;
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
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.vc.Authenticator;
import nl.toolforge.karma.core.vc.Authenticators;
import nl.toolforge.karma.core.vc.DevelopmentLine;
import nl.toolforge.karma.core.vc.PatchLine;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.SymbolicName;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.VersionControlSystem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.FileUtils;
import org.netbeans.lib.cvsclient.Client;
import org.netbeans.lib.cvsclient.admin.StandardAdminHandler;
import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.command.GlobalOptions;
import org.netbeans.lib.cvsclient.command.add.AddCommand;
import org.netbeans.lib.cvsclient.command.checkout.CheckoutCommand;
import org.netbeans.lib.cvsclient.command.commit.CommitCommand;
import org.netbeans.lib.cvsclient.command.importcmd.ImportCommand;
import org.netbeans.lib.cvsclient.command.log.LogInformation;
import org.netbeans.lib.cvsclient.command.log.RlogCommand;
import org.netbeans.lib.cvsclient.command.tag.TagCommand;
import org.netbeans.lib.cvsclient.command.update.UpdateCommand;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;
import org.netbeans.lib.cvsclient.connection.Connection;
import org.netbeans.lib.cvsclient.connection.ConnectionFactory;
import org.netbeans.lib.cvsclient.connection.PServerConnection;
import org.netbeans.lib.cvsclient.event.CVSListener;

import java.io.File;
import java.io.IOException;
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

  private CVSListener listener = null; // The listener that receives events from this runner.

  private static Log logger = LogFactory.getLog(CVSRunner.class);

  private GlobalOptions globalOptions = new GlobalOptions();
  private Connection connection = null;

  private CVSRepository location = null;

  private boolean isExt = false;

  /**
   * Constructs a runner to fire commands on a CVS repository. A typical client for a <code>CVSRunner</code> instance is
   * a {@link Command} implementation, as that one knows what to fire away on CVS. The runner is instantiated with a
   * <code>location</code> and a <code>manifest</code>. The location must be a <code>CVSLocationImpl</code> instance,
   * reprenting a CVS repository. The manifest is required because it determines the base point from where CVS commands
   * will be run; modules are checked out in a directory structure, relative to
   * {@link nl.toolforge.karma.core.manifest.Manifest#getModuleBaseDirectory()}.
   *
   * @param location       A <code>Location</code> instance (typically a <code>CVSLocationImpl</code> instance), containing
   *                       the location and connection details of the CVS repository.
   * @throws CVSException  <code>AUTHENTICATION_ERROR</code> is thrown when <code>location</code> cannot be authenticated.
   * @throws nl.toolforge.karma.core.vc.AuthenticationException If the location cannot be authenticated.
   */
  public CVSRunner(Location location) throws CVSException, nl.toolforge.karma.core.vc.AuthenticationException {

    if (location == null) {
      throw new CVSException(VersionControlException.MISSING_LOCATION);
    }

    CVSRepository cvsLocation = null;
    try {
      cvsLocation = ((CVSRepository) location);
      setLocation(cvsLocation);
    } catch (ClassCastException e) {
      logger.error("Wrong type for location. Should be CVSRepository.", e);
      throw new KarmaRuntimeException("Wrong type for location. Should be CVSRepository.", e);
    }

    //If we don't have a
    //
    Authenticator a = Authenticators.getAuthenticator(cvsLocation.getAuthenticatorKey());
    cvsLocation.setUsername(a.getUsername());

    if (cvsLocation.getProtocol().equals(CVSRepository.EXT)) {

      isExt = true;

      this.listener = new LogParser();

    } else {

      this.listener = new CVSResponseAdapter();

      connection = ConnectionFactory.getConnection(cvsLocation.getCVSRoot());
      if (connection instanceof PServerConnection) {
        ((PServerConnection) connection).setEncodedPassword(a.getPassword());
      }

      logger.debug("CVSRunner using CVSROOT : " + cvsLocation.getCVSRoot());
      globalOptions.setCVSRoot(cvsLocation.getCVSRoot());
    }

    // The default ...
    //
  }

  private void setLocation(CVSRepository location) {
    this.location = location;
  }

  private CVSRepository getLocation() {
    return location;
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
   * Checks if the module is located in CVS within a subdirectory (or subdirs, any amount is possible). If so, the
   * module-name is prefixed with that offset.
   *
   * @param module
   * @return        The module-name, or - when applicable - the module-name prefixed with the value of
   *                {@link VersionControlSystem#getModuleOffset()}.
   */
  private String getModuleOffset(Module module) {

    // todo when modules from different locations have the same name, they should be given a namespace in front
    // of the module.

    if (((VersionControlSystem) module.getLocation()).getModuleOffset() == null) {
      return module.getName();
    } else {
      return ((VersionControlSystem) module.getLocation()).getModuleOffset() + "/" + module.getName();
    }
  }

  public void commit(File file) throws VersionControlException {

    // todo why not use the add()-method ????

    StandardAdminHandler adminHandler = new StandardAdminHandler();

    boolean isEntry = false;

    try {
      isEntry = (adminHandler.getEntry(file) != null);
    } catch (IOException e) {
      isEntry = false;
    }

    if (isEntry) {

      CommitCommand commitCommand = new CommitCommand();
      commitCommand.setFiles(new File[]{file});
      commitCommand.setMessage("<undocumented> File committed by Karma");

      executeOnCVS(commitCommand, file.getParentFile(), null);

    } else {
      AddCommand addCommand = new AddCommand();
      addCommand.setFiles(new File[]{file});

      executeOnCVS(addCommand, file.getParentFile(), null);

      CommitCommand commitCommand = new CommitCommand();
      commitCommand.setFiles(new File[]{file});
      commitCommand.setMessage("<undocumented> File committed by Karma");

      executeOnCVS(commitCommand, file.getParentFile(), null);
    }

  }

  public void addModule(Module module, String comment) throws CVSException {

    // Step 1 : check if the module doesn't yet exist
    //
    if (existsInRepository(module)) {
      throw new CVSException(CVSException.MODULE_EXISTS_IN_REPOSITORY, new Object[]{module.getName(), module.getLocation().getId()});
    }

    // Step 2 : import the module, including its full structure.
    //
    ImportCommand importCommand = new ImportCommand();
    importCommand.setModule(getModuleOffset(module));
    importCommand.setLogMessage("Module " + module.getName() + " created automatically by Karma on " + new Date().toString());
    importCommand.setVendorTag("Karma");
    importCommand.setReleaseTag("MAINLINE_0-0");

    executeOnCVS(importCommand, module.getBaseDir(), null);
  }

  /**
   * Performs the <code>cvs checkout [-r &lt;symbolic-name&gt;] &lt;module&gt;</code>command for a module.
   * <code>version</code> is used when not <code>null</code> to checkout a module with a symbolic name.
   *
   * @param module        The module to check out.
   * @param version       The version number for the module to check out.
   * @throws CVSException With errorcodes <code>NO_SUCH_MODULE_IN_REPOSITORY</code> when the module does not exist
   *                      in the repository and <code>INVALID_SYMBOLIC_NAME</code>, when the version does not exists for the module.
   */
  public void checkout(Module module, Version version) throws CVSException {

    checkout(module, null, version);
  }


  public void checkout(Module module) throws CVSException {
    checkout(module, null, null);
  }

  /**
   * See {@link #checkout(Module, Version)}. This method defaults to the HEAD of the development branch at hand.
   *
   * @param module          The module to check out.
   * @param developmentLine The development line or <code>null</code> when the TRUNK is the context line.
   * @throws CVSException   With errorcodes <code>NO_SUCH_MODULE_IN_REPOSITORY</code> when the module does not exist
   *                        in the repository and <code>INVALID_SYMBOLIC_NAME</code>, when the version does not exists
   *                        for the module.
   */
  public void checkout(Module module, DevelopmentLine developmentLine, Version version) throws CVSException {

    //todo: proper exception handling
    try {
      MyFileUtils.makeWriteable(module.getBaseDir(), true);
    } catch (Exception e) {
      logger.error("Exception when making module writeable just before checking it out.", e);
    }

    boolean readonly = false;

    Map arguments = new Hashtable();
    arguments.put("MODULE", module.getName());
    arguments.put("REPOSITORY", module.getLocation().getId());

    CheckoutCommand checkoutCommand = new CheckoutCommand();
    checkoutCommand.setModule(getModuleOffset(module));
    checkoutCommand.setCheckoutDirectory(module.getName()); // Flatten to module name as the checkoutdir.
    checkoutCommand.setPruneDirectories(true); //-P

    if ((version != null) || (developmentLine != null)) {
      checkoutCommand.setCheckoutByRevision(Utils.createSymbolicName(module, developmentLine, version).getSymbolicName());
      if (version != null) {
        arguments.put("VERSION", version.toString());
        readonly = true;
      }
    } else {
      checkoutCommand.setResetStickyOnes(true);
    }

    // The checkout directory for a module has to be relative to

    executeOnCVS(checkoutCommand, module.getBaseDir().getParentFile(), arguments);

    updateModuleHistoryXml(module);

    if (readonly) {
      MyFileUtils.makeReadOnly(module.getBaseDir());
    }
  }

  private void updateModuleHistoryXml(Module module) throws CVSException {
    logger.debug("Updating history.xml to HEAD.");
    UpdateCommand command = new UpdateCommand();
    try {
      ModuleHistoryFactory factory = ModuleHistoryFactory.getInstance(module.getBaseDir());
      File historyLocation = factory.getModuleHistory(module).getHistoryLocation();
      command.setFiles(new File[]{historyLocation});
      command.setResetStickyOnes(true);  //-A
      executeOnCVS(command, module.getBaseDir(), null);
      logger.debug("Done updating history.xml to HEAD.");
    } catch (ModuleHistoryException mhe) {
      logger.error("ModuleHistoryException when updating module history to HEAD", mhe);
    }
  }


  /**
   * @see #update(Module, DevelopmentLine, Version)
   */
  public void update(Module module) throws CVSException {
    update(module, null);
  }

  /**
   * @see #update(Module, DevelopmentLine, Version)
   */
  public void update(Module module, Version version) throws CVSException {
    update(module, null, version);
  }

  /**
   * For a module, the <code>cvs -q update -d -r &lt;symbolic-name&gt;</code> command is executed. Note that empty
   * directories are not pruned.
   *
   * @param module          The module to update.
   * @param developmentLine The development line or <code>null</code> when the TRUNK is the context line.
   * @param version         The version of the module or <code>null</code> when no specific version applies.
   */
  public void update(Module module, DevelopmentLine developmentLine, Version version) throws CVSException {

    //todo: proper exception handling
    try {
      MyFileUtils.makeWriteable(module.getBaseDir(), true);
    } catch (Exception e) {
      logger.error("Exception when making module writeable just before updating it.", e);
    }

    boolean readonly = false;

    Map arguments = new Hashtable();
    arguments.put("MODULE", module.getName());
    arguments.put("REPOSITORY", module.getLocation().getId());

    UpdateCommand updateCommand = new UpdateCommand();
    updateCommand.setRecursive(true);
    updateCommand.setBuildDirectories(true); //-d
    updateCommand.setPruneDirectories(false); //-P

    if (version != null || module.hasPatchLine()) {
      updateCommand.setUpdateByRevision(Utils.createSymbolicName(module, developmentLine, version).getSymbolicName());
      if (version != null) {
        arguments.put("VERSION", version.toString());
        readonly = true;
      }
    } else {
      updateCommand.setResetStickyOnes(true);
    }

    // todo add data to the exception. this sort of business logic should be here, not in CVSResponseAdapter.
    //
    executeOnCVS(updateCommand, module.getBaseDir(), arguments);

    updateModuleHistoryXml(module);

    if (readonly) {
      MyFileUtils.makeReadOnly(module.getBaseDir());
    }
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

      //try to create the dirs
      if (!dirToAdd.mkdirs() && !dirToAdd.exists()) {
        //failed to create the dirs and they do not exist yet.
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
      String author = ((CVSRepository) module.getLocation()).getUsername();
      addModuleHistoryEvent(module, ModuleHistoryEvent.PROMOTE_MODULE_EVENT, version, new Date(), author, comment);

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

    Map arguments = new Hashtable();
    arguments.put("MODULE", module.getName());
    arguments.put("REPOSITORY", module.getLocation().getId());

    RlogCommand logCommand = new RlogCommand();
    logCommand.setModule(getModuleOffset(module) + "/" + Module.MODULE_DESCRIPTOR);
    logCommand.setRecursive(false);


    File tmp = null;
    try {
      tmp = MyFileUtils.createTempDirectory();
    } catch (IOException e) {
      //
    }

    executeOnCVS(logCommand, tmp, arguments);

    try {
      FileUtils.deleteDirectory(tmp);
    } catch (IOException e) {
      //
    }

    // Another hook into ext support.
    //
    if (isExt) {
      return ((LogParser) this.listener).getLogInformation();
    } else {
      return ((CVSResponseAdapter) this.listener).getLogInformation();
    }
  }

  /*

  DISABLED, DO NOT DELETE, EXPERIMENTAL

  public LogInformation log(Manifest manifest) throws CVSException {


    RlogCommand logCommand = new RlogCommand();

    String[] modules = new String[manifest.getAllModules().size()];
    int j = 0;
    for (Iterator i = manifest.getAllModules().values().iterator(); i.hasNext();) {
      modules[j] = getModuleOffset((Module) i.next()) + "/" + Module.MODULE_DESCRIPTOR;
      j++;
    }

    logCommand.setModules(modules);

    executeOnCVS(logCommand, new File("."), null);

    // Another hook into ext support.
    //
    return ((CVSResponseAdapter) this.listener).getLogInformation();
  }

  */


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

  /**
   * Creates a patchline for the module, given the modules' current version.
   *
   * @param module The module.
   * @throws CVSException When an error occurred during the creation process.
   */
  public void createPatchLine(Module module) throws CVSException {
    try {
      // Add an event to the module history.
      //
      String author = ((CVSRepository) module.getLocation()).getUsername();
      tag(module, new CVSTag(module.getPatchLine().getName()), true);

      addModuleHistoryEvent(module, ModuleHistoryEvent.CREATE_PATCH_LINE_EVENT, module.getVersion(), new Date(), author, "Patch line created by Karma");

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

    try {
      RlogCommand logCommand = new RlogCommand();
      logCommand.setModule(getModuleOffset(module));
      executeOnCVS(logCommand, new File("."), null);

    } catch (CVSException e) {
      return false;
    }
    return true;
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
    // Switch ...
    //
    if (isExt) {

      ExtClient client = new ExtClient();
      client.addCVSListener((LogParser) listener);
      client.runCommand(command, contextDirectory, getLocation());

    } else {

      if (contextDirectory == null) {
        throw new NullPointerException("Context directory cannot be null.");
      }

      Client client = new Client(getConnection(), new StandardAdminHandler());
      client.setLocalPath(contextDirectory.getPath());

      logger.debug("Running CVS command : '" + command.getCVSCommand() + "' in " + client.getLocalPath());

      try {
        // A CVSResponseAdapter is registered as a listener for the response from CVS. This one adapts to Karma
        // specific stuff.
        //

        long start = System.currentTimeMillis();

        ((CVSResponseAdapter) listener).setArguments(args);
        client.getEventManager().addCVSListener(listener);
        client.executeCommand(command, globalOptions);

        logger.debug("CVS command finished in " + (System.currentTimeMillis() - start) + " ms.");

      } catch (CommandException e) {
        logger.error(e.getMessage(), e);
        if (e.getUnderlyingException() instanceof CVSRuntimeException) {
          ErrorCode code = ((CVSRuntimeException) e.getUnderlyingException()).getErrorCode();
          Object[] messageArgs = ((CVSRuntimeException) e.getUnderlyingException()).getMessageArguments();
          throw new CVSException(code, messageArgs);
        } else {
          throw new CVSException(CVSException.INTERNAL_ERROR, new Object[]{globalOptions.getCVSRoot()});
        }
      } catch (AuthenticationException e) {
        logger.error(e.getMessage(), e);
        throw new CVSException(CVSException.AUTHENTICATION_ERROR, new Object[]{client.getGlobalOptions().getCVSRoot()});
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
  }

  /**
   * Helper method that add a new event to a module's history. When the history does not
   * exist yet (in case of a new module) it is newly created. When the history does exist
   * the event is added to the history.
   *
   * @param module                  The module involved.
   * @param eventType               The type of {@link ModuleHistoryEvent}.
   * @param version                 The version that the module is promoted to.
   * @param datetime                The timestamp.
   * @param author                  The authentication of the person who has triggered the event.
   * @param comment                 The (optional) comment the author has given.
   * @throws CVSException           Thrown in case something goes wrong with CVS
   */
  private void addModuleHistoryEvent(
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

      try {
        MyFileUtils.makeWriteable(new File(module.getBaseDir(), "CVS"), false);
        if (history.getHistoryLocation().exists()) {
          //history already exists. commit changes.
          MyFileUtils.makeWriteable(history.getHistoryLocation(), false);
          history.save();

          //development line is null, since the history.xml is always committed in the HEAD.
          commit(null, module, new File(module.getBaseDir(), ModuleHistory.MODULE_HISTORY_FILE_NAME), "History updated by Karma");
        } else {
          //history did not exist yet. add to CVS and commit it.
          history.save();
          add(module, new String[]{history.getHistoryLocation().getName()}, null);
        }
        MyFileUtils.makeReadOnly(module.getBaseDir());
      } catch (IOException ioe) {
        logger.error("Error when making history.xml readonly/writeable", ioe);
      } catch (InterruptedException ie) {
        logger.error("Error when making history.xml readonly/writeable", ie);
      }
    } else {
      //history wel null. EN NU?! todo
      throw new OutOfTheBlueException("If this happens something rrreally went wrong with the history");
    }
  }

}
