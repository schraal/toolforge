package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.*;
import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.vc.Runner;
import org.apache.commons.cli.CommandLine;

/**
 * Creates a module in a repository. The command provides the option to create the module in the current manifest as
 * well.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 *
 * @since 2.0
 */
public class CreateModuleCommand extends DefaultCommand {

  public CreateModuleCommand(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public CommandResponse execute() throws KarmaException {

    CommandLine commandLine = getCommandLine();

    String locationAlias = commandLine.getOptionValue("l");
    String moduleName = commandLine.getOptionValue("m");

    // The manifest itself is responsible for creating new modules.
    //
    Module module = getContext().getCurrent().createModule(moduleName, locationAlias);

    // If we get to this point, creation of the module was succesfull.
    //
    CommandMessage message = new SimpleCommandMessage(getFrontendMessages().getString("message."));



//    if (commandLine.hasOption("i")) {
//      // A manifest must be present for this option to be completed.
//      //
//      if (!getContext().isManifestLoaded()) {
//        throw new CommandException(CommandException.NO_MANIFEST_SELECTED);
//      }
//    }

    return new SimpleCommandResponse();
  }
}