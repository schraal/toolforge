package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.util.BuildUtil;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.manifest.AbstractManifest;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class StartWorkCommand extends DefaultCommand {

  private static Log logger = LogFactory.getLog(AbstractManifest.class);

  private CommandResponse response = null;

  /**
   *
   * @param descriptor The command descriptor for this command.
   */
  public StartWorkCommand(CommandDescriptor descriptor) throws CommandException {

    super(descriptor);

    response = new ActionCommandResponse();
  }

  public void execute() throws CommandException {

    String moduleName = "";
    Module module = null;

    // A manifest must be present for this command
    //
    // todo move to aspect; this type of checking can be done by one aspect.
    //
    if (!getContext().isManifestLoaded()) {
      throw new CommandException(ManifestException.NO_ACTIVE_MANIFEST);
    }

    moduleName = getCommandLine().getOptionValue("m");
    try {
      module = getContext().getCurrentManifest().getModule(moduleName);
    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    // Criteria :
    //
    // 1. The module has to be a SourceModule
    // 2. The module cannot be STATIC
    // 3. The module should not be WORKING (makes no sense ...)
    // 4. The module should already be local
    //
    if (!(module instanceof SourceModule)) {
      throw new CommandException(CommandException.MODULE_TYPE_MUST_BE_SOURCEMODULE, new Object[] {module.getName()});
    }
    if (((SourceModule) module).getState().equals(Module.STATIC)) {
      throw new CommandException(CommandException.START_WORK_NOT_ALLOWED_ON_STATIC_MODULE, new Object[] {module.getName()});
    }
    if (Module.WORKING.equals(((SourceModule)module).getState())) {

      // todo message to be internationalized.
      //
      // todo message handling to karma-cli ???
      //
      response.addMessage(new SuccessMessage("Module " + module.getName() + " is already WORKING."));

    } else {

      Manifest currentManifest = getContext().getCurrentManifest();

      if (!currentManifest.isLocal(module)) {
        // todo Hmm, mixing functionality of two exceptions.
        //
        throw new CommandException(ManifestException.MODULE_NOT_LOCAL, new Object[] {module.getName()});
      }

      try {

        // Step 1 : make the module WORKING
        //

        // A developer always works on the HEAD of a DevelopmentLine.
        //
        Runner runner = RunnerFactory.getRunner(module.getLocation(), getContext().getCurrentManifest().getDirectory());
        runner.checkout(module);

//        Command command = CommandFactory.getInstance().getCommand(CommandDescriptor.UPDATE_MODULE_COMMAND + " -m ".concat(module.getName()));
//        getContext().execute(command);

        // todo development-line should be taken into account
        //
        // todo what if user has made changes to files, even if not allowed by the common process ?

        currentManifest.setState(module, Module.WORKING);

        // Step 2 : clean up any 'old' build directories for this module.
        //
        File buildDir = new File(new File(currentManifest.getDirectory(), "build"), moduleName);
        try {
          FileUtils.deleteDirectory(buildDir);
        } catch (IOException e) {
          // todo check this ! these sort of warning's should be handled differently.
          //
          response.addMessage(
              new SuccessMessage("WARNING : Build directory could not be deleted. Possible synchronization error."));
        }

        // Step 3 : determine which modules depend on the module which has just changed state. Based
        //          on a switch build.check-dependencies-on-state-change (whatever), this process is started.
        // todo the switch has to be implemented.

        BuildUtil util = new BuildUtil(currentManifest);

        try {
          util.cleanDependencies(module);
        } catch (ManifestException e) {
          // Ignore. When this process fails, we continue anyway.
          // todo view warning in another console
          logger.warn("Could not clean dependencies for module " + moduleName + "; dependencies.xml is invalid.");
        }

        // todo message to be internationalized.
        //
        // todo message handling to karma-cli ???
        //
        response.addMessage(
            new SuccessMessage("You can start working on module " + module.getName() + "; state changed to WORKING."));

      } catch (ManifestException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
//      }
      } catch (VersionControlException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      }
    }
  }

  public CommandResponse getCommandResponse() {
    return response;
  }
}
