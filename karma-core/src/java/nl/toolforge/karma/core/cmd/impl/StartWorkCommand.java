package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;

/**
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class StartWorkCommand extends DefaultCommand {

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
      module = getContext().getCurrent().getModule(moduleName);
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

      Manifest currentManifest = getContext().getCurrent();

      if (!currentManifest.isLocal(module)) {
        // todo Hmm, mixing functionality of two exceptions.
        //
        throw new CommandException(ManifestException.MODULE_NOT_LOCAL, new Object[] {module.getName()});
      }

      try {

        // A developer always works on the HEAD of a DevelopmentLine.
        //
        Runner runner = RunnerFactory.getRunner(module.getLocation(), getContext().getCurrent().getDirectory());
        runner.checkout(module);

//        Command command = CommandFactory.getInstance().getCommand(CommandDescriptor.UPDATE_MODULE_COMMAND + " -m ".concat(module.getName()));
//        getContext().execute(command);

        // todo development-line should be taken into account
        //
        // todo what if user has made changes to files, even if not allowed by the common process ?

        currentManifest.setState(module, Module.WORKING);

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
