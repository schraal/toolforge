package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.CVSVersionExtractor;
import nl.toolforge.karma.core.Version;

/**
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class StopWorkCommand extends DefaultCommand {

  private CommandResponse response = null;
  /**
   *
   * @param descriptor The command descriptor for this command.
   */
  public StopWorkCommand(CommandDescriptor descriptor) throws CommandException {

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

    if (!(module instanceof SourceModule)) {
      throw new CommandException(CommandException.MODULE_TYPE_MUST_BE_SOURCEMODULE, new Object[] {module.getName()});
    }
    if (((SourceModule) module).hasVersion()) {
      throw new CommandException(CommandException.START_WORK_NOT_ALLOWED_ON_STATIC_MODULE, new Object[] {module.getName()});
    }


    if (!Module.WORKING.equals(((SourceModule)module).getState())) {

      // todo message to be internationalized.
      //
      // todo message handling to karma-cli ???
      //
      response.addMessage(new SuccessMessage("You are not working on module " + module.getName() + "."));

    } else {

      try {

        Version version = CVSVersionExtractor.getInstance().getLastVersion(module);

        // todo what if user has made changes to files, even if not allowed by the common process ?

        // Update to the latest available version (in a DevelopmentLine).
        //
        Runner runner = RunnerFactory.getRunner(module.getLocation(), getContext().getCurrentManifest().getDirectory());
        runner.checkout(module, version);

        getContext().getCurrentManifest().setState(module, Module.DYNAMIC);

      } catch (ManifestException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      } catch (VersionControlException e) {
        throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      }
      // todo message to be internationalized.
      //
      // todo message handling to karma-cli ???
      //
      response.addMessage(new SuccessMessage("You have stopped working on module " + module.getName() + "; state changed to DYNAMIC."));
    }
  }

  public CommandResponse getCommandResponse() {
    return response;
  }
}
