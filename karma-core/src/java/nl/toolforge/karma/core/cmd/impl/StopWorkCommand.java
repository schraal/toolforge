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
      module = getContext().getCurrent().getModule(moduleName);
    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode());
    }

    if (!(module instanceof SourceModule)) {
      throw new CommandException(CommandException.MODULE_TYPE_MUST_BE_SOURCEMODULE, new Object[] {module.getName()});
    }
    if (((SourceModule) module).hasVersion()) {
      throw new CommandException(CommandException.START_WORK_NOT_ALLOWED_ON_STATIC_MODULE, new Object[] {module.getName()});
    }

    try {

      if (!Module.WORKING.equals(((SourceModule)module).getState())) {

        // todo message to be internationalized.
        //
        // todo message handling to karma-cli ???
        //
        response.addMessage(new SuccessMessage("You are not working on module " + module.getName() + "."));

      } else {

        // todo development-line should be taken into account
        //
        // todo what if user has made changes to files, even if not allowed by the common process ?

        getContext().getCurrent().setState(module, Module.DYNAMIC);

        // todo message to be internationalized.
        //
        // todo message handling to karma-cli ???
        //
        response.addMessage(new SuccessMessage("You have stopped working on module " + module.getName() + "; state changed to DYNAMIC."));
      }
    } catch (ManifestException e) {
      throw new CommandException(e.getErrorCode());
    }
  }

  public CommandResponse getCommandResponse() {
    return response;
  }
}
