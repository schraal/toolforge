package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ModuleDescriptor;
import nl.toolforge.karma.core.manifest.ModuleFactory;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import org.apache.commons.cli.CommandLine;

/**
 * Creates a module in a repository. The command provides the option to create the module in the current manifest as
 * well.
 *
 * @author D.A. Smedes
 * @version $Id:
 * @since 2.0
 */
public class CreateModuleCommand extends DefaultCommand {
  
  private CommandResponse commandResponse = new ActionCommandResponse();

  public CreateModuleCommand(CommandDescriptor descriptor) {
    super(descriptor);
  }

  /**
   * Physical creation of a module in a version control system.
   */
  public void execute() throws CommandException {

    CommandLine commandLine = getCommandLine();

    String locationAlias = commandLine.getOptionValue("l");
    String moduleName = commandLine.getOptionValue("m");

    // Part 1 of the transaction is the creation of a Module instance.
    //
    ModuleDescriptor descriptor = new ModuleDescriptor(moduleName, "src", locationAlias);
    Module module = null;
    try {
      module = ModuleFactory.getInstance().create(descriptor);
    } catch (LocationException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    try {
      // Part 2 of the transaction is the creation in a version control system.
      //
      Runner runner = RunnerFactory.getRunner(module.getLocation(), getContext().getLocalEnvironment().getDevelopmentHome());
      runner.setCommandResponse(getCommandResponse());
      runner.create(module);

      // If we get to this point, creation of the module was succesfull.
      //
      //todo dit moet anders
      CommandMessage message =
          new SimpleCommandMessage(getFrontendMessages().getString("message.MODULE_CREATED"), new Object[]{moduleName, locationAlias});

      // Ensure that only this message is passed back to the client
      //
      commandResponse.addMessage(new SuccessMessage(message.getMessageText()));
    } catch (VersionControlException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      //commandResponse.addMessage(new ErrorMessage(ke));
    } catch (KarmaException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }
}