package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.vc.Runner;

/**
 * This command updates a module on a developers' local system.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class UpdateModuleCommand extends DefaultCommand {

  private Module module = null;

  /**
   * Creates a <code>UpdateModuleCommand</code> for module <code>module</code> that should be updated. This module
   * requires an <code>Option</code>
   *
   * @param descriptor The command descriptor for this command.
   */
  public UpdateModuleCommand(CommandDescriptor descriptor) throws CommandException {

    super(descriptor);
  }

  /**
   * Creates an UpdateModuleCommand.
   *
   * @param descriptor See {@link DefaultCommand}.
   */
//	public UpdateModuleCommand(CommandDescriptor descriptor) throws KarmaException {
//		super(descriptor);
//	}

  public CommandResponse execute() throws KarmaException {

    String moduleName = getOptions().getOption("m").getValue();

    try {
      this.module = getContext().getCurrent().getModule(moduleName);
    } catch (KarmaException e) {
      throw (CommandException) e;
    }


    Runner runner = getContext().getRunner(module);

    if (getContext().getCurrent().isLocal(module)) {
      return runner.update(module);
    } else {
      return runner.checkout(module);
    }
  }
}
