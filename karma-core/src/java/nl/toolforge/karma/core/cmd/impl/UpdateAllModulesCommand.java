package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.ManifestException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.ModuleMap;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.ErrorMessage;

import java.util.Iterator;

/**
 * This command updates all modules in the active manifest on a developers' local system.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class UpdateAllModulesCommand extends DefaultCommand {

  private CommandResponse commandResponse = new ActionCommandResponse();

  /**
   * Creates a <code>UpdateAllModulesCommand</code> for module <code>module</code> that should be updated.
   *
   * @param descriptor The command descriptor for this command.
   */
  public UpdateAllModulesCommand(CommandDescriptor descriptor) throws CommandException {
    super(descriptor);
  }

  /**
   * This command will update all modules in the active manifest from the version control system. An update is done when
   * the module is already present, otherwise a checkout will be performed. The checkout directory for the module
   * is relative to the root directory of the <code>active</code> manifest.
   *
   */
  public void execute() {
    try {
      // A manifest must be present for this command
      //
      if (!getContext().isManifestLoaded()) {
        throw new ManifestException(ManifestException.NO_MANIFEST_SELECTED);
      }

      // todo what to do about jarmodules etc ?
      //
      ModuleMap modules = getContext().getCurrent().getModules();

      // Loop through all modules and use UpdateModuleCommand on each module.
      //
      for (Iterator i = modules.keySet().iterator(); i.hasNext();) {

        Module module = (Module) modules.get(i.next());

        // todo hmm, the commandname is hardcoded whilst we have it dynamically in a file ...
        //
        getContext().execute("update-module -m ".concat(module.getName()));
      }
    } catch (CommandException ce) {
      commandResponse.addMessage(new ErrorMessage(ce));
    } catch (KarmaException ke) {
      commandResponse.addMessage(new ErrorMessage(ke));
    }
  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }

}
