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
package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaRuntimeException;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.location.LocationException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ModuleDigester;
import nl.toolforge.karma.core.manifest.ModuleFactory;
import nl.toolforge.karma.core.manifest.util.EappModuleLayoutTemplate;
import nl.toolforge.karma.core.manifest.util.LibModuleLayoutTemplate;
import nl.toolforge.karma.core.manifest.util.SourceModuleLayoutTemplate;
import nl.toolforge.karma.core.manifest.util.WebappModuleLayoutTemplate;
import nl.toolforge.karma.core.vc.Runner;
import nl.toolforge.karma.core.vc.RunnerFactory;
import nl.toolforge.karma.core.vc.VersionControlException;
import nl.toolforge.karma.core.vc.AuthenticationException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.regex.PatternSyntaxException;

/**
 * Creates a module in a repository. Modules are created using a layout template (instances of 
 * <code>ModuleLayoutTemplate</code>). 
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class CreateModuleCommand extends DefaultCommand {

  private static Log logger = LogFactory.getLog(CreateModuleCommand.class);

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

    // todo this bit sucks. Since renamed to ModuleDigester, it doesn't make sense anymore.
    ModuleDigester digester = null;
    try {
      digester = new ModuleDigester(moduleName, locationAlias);
    } catch (PatternSyntaxException e) {
      throw new CommandException(CommandException.INVALID_ARGUMENT, new Object[]{moduleName, e.getMessage()});
    }

    Module.Type moduleType = new Module.Type();
    try {
      moduleType.setType(commandLine.getOptionValue("t"));
    } catch (IllegalArgumentException e1) {
      throw new CommandException(CommandException.INVALID_ARGUMENT);
    }

    Module module = null;
    try {
      ModuleFactory factory = new ModuleFactory(getWorkingContext());
      module = factory.create(digester, moduleType);
    } catch (LocationException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

    CommandMessage message = new SuccessMessage(getFrontendMessages().getString("message.CREATE_MODULE_STARTED"), new Object[]{moduleName, locationAlias});
    commandResponse.addMessage(message);

    try {

      module.createRemote(comment);

      // Ensure that only this message is passed back to the client
      //
      message = new SuccessMessage(getFrontendMessages().getString("message.CREATE_MODULE_SUCCESSFULL"), new Object[]{moduleName, locationAlias});
      commandResponse.addMessage(new SuccessMessage(message.getMessageText()));

    } catch (VersionControlException e) {
      logger.error(e);
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    } catch (AuthenticationException e) {
      // todo moet anders.
      logger.error(e.getMessage());
      // throw new CommandException();
    }
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }
}
