package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Version;
import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.history.ModuleHistory;
import nl.toolforge.karma.core.history.ModuleHistoryEvent;
import nl.toolforge.karma.core.history.ModuleHistoryFactory;
import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ModuleDescriptor;
import nl.toolforge.karma.core.manifest.ModuleFactory;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.cvs.CVSLocationImpl;
import nl.toolforge.core.util.file.MyFileUtils;
import org.apache.commons.cli.CommandLine;

import java.util.Date;
import java.io.IOException;
import java.io.File;

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
    boolean include = commandLine.hasOption("i");

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
//      Runner runner = RunnerFactory.getRunner(module.getLocation(), getContext().getLocalEnvironment().getDevelopmentHome());
      runner.setCommandResponse(getCommandResponse());
      runner.create(module);

      //module has been created. Now, create the module history.
      //todo: mave this whole lot to runner.create();
//      try {
          File tmp;
          try {
            tmp = MyFileUtils.createTempDirectory();
          } catch (IOException e) {
            throw new KarmaRuntimeException("Panic! Failed to create temporary directory.");
          }
          runner.checkout(module, tmp);

          ModuleHistoryFactory factory = ModuleHistoryFactory.getInstance(tmp);
          ModuleHistory history = factory.getModuleHistory(module.getName());
          if (history != null) {
            ModuleHistoryEvent event = new ModuleHistoryEvent();
            event.setType(ModuleHistoryEvent.CREATE_MODULE_EVENT);
            event.setVersion(Version.INITIAL_VERSION);
            event.setDatetime(new Date());
            Location location = module.getLocation();
              //todo: this is not funny
            if (location instanceof CVSLocationImpl) {
              event.setAuthor( ((CVSLocationImpl) location).getUsername() );
            }
            history.addEvent(event);
            history.save();
System.out.println(runner);
System.out.println(history);
System.out.println(history.getHistoryLocation());
            runner.add(module, history.getHistoryLocation().getName(), tmp);
          }
//      } catch (ManifestException me) {
        // something went wrong, but this is not a reason to stop creating the module
        // actually, the module had already been created :)
//        me.printStackTrace();
        // todo: give warning.
        // throw new CommandException(me.getErrorCode(), me.getMessageArguments());
//      }

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