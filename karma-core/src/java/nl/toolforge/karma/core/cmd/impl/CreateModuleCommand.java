package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.LocalEnvironment;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ModuleDescriptor;
import nl.toolforge.karma.core.manifest.ModuleFactory;
import nl.toolforge.karma.core.manifest.util.EappModuleLayoutTemplate;
import nl.toolforge.karma.core.manifest.util.SourceModuleLayoutTemplate;
import nl.toolforge.karma.core.manifest.util.WebappModuleLayoutTemplate;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import org.apache.commons.cli.CommandLine;

import java.util.regex.PatternSyntaxException;

/**
 * Creates a module in a repository. Modules are created using a layout template (instances of 
 * <code>ModuleLayoutTemplate</code>). 
 *
 * @author D.A. Smedes
 * @version $Id$
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
    String comment = commandLine.getOptionValue("c");

    // Part 1 of the transaction is the creation of a Module instance.
    //

    ModuleDescriptor descriptor = null;
    try {
      descriptor = new ModuleDescriptor(moduleName, "src", locationAlias);
    } catch (PatternSyntaxException e) {
      throw new CommandException(CommandException.INVALID_ARGUMENT, new Object[]{moduleName});
    }

    Module module = null;
    try {
      module = ModuleFactory.getInstance().create(descriptor);
    } catch (LocationException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    try {
      // Part 2 of the transaction is the creation in a version control system.
      //
      Runner runner = RunnerFactory.getRunner(module.getLocation(), LocalEnvironment.getDevelopmentHome());
      runner.setCommandResponse(getCommandResponse());

      CommandMessage message = null;
      if (module.getDeploymentType().equals(Module.WEBAPP)) {
        runner.create(module, comment, new WebappModuleLayoutTemplate());
        message = new SuccessMessage(getFrontendMessages().getString("message.WEBAPP_MODULE_CREATED"), new Object[]{moduleName, locationAlias});
      } else if (module.getDeploymentType().equals(Module.EAPP)) {
        runner.create(module, comment, new EappModuleLayoutTemplate());
        message = new SuccessMessage(getFrontendMessages().getString("message.EAPP_MODULE_CREATED"), new Object[]{moduleName, locationAlias});
      } else {
        runner.create(module, comment, new SourceModuleLayoutTemplate());
        message = new SuccessMessage(getFrontendMessages().getString("message.SRC_MODULE_CREATED"), new Object[]{moduleName, locationAlias});
      }

      // Ensure that only this message is passed back to the client
      //
      commandResponse.addMessage(new SuccessMessage(message.getMessageText()));

    } catch (VersionControlException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
      //commandResponse.addMessage(new ErrorMessage(ke));
    }
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }
}
