package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.KarmaException;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.Manifest;
import nl.toolforge.karma.core.cmd.*;
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
   * This command will update the module from the version control system. An update is done when
   * the module is already present, otherwise a checkout will be performed. The checkout directory for the module
   * is relative to the root directory of the <code>active</code> manifest.
   *
   * @throws KarmaException When no manifest is loaded, a {@link CommandException#NO_MANIFEST_SELECTED} is thrown. For
   *   other errors, a more generic {@link KarmaException} is thrown.
   */
  public CommandResponse execute() throws KarmaException {

    // A manifest must be present for this command
    //
    if (!getContext().isManifestLoaded()) {
      throw new CommandException(CommandException.NO_MANIFEST_SELECTED);
    }

    String moduleName = getCommandLine().getOptionValue("m");

    this.module = getContext().getCurrent().getModule(moduleName);

    Runner runner = getContext().getRunner(module);

    CommandResponse response = new SimpleCommandResponse();
    if (getContext().getCurrent().isLocal(module)) {
      response = runner.update(module);
    } else {
      response = runner.checkout(module);
    }

    // Apparently, no exceptions where thrown, so we can continue
    //

    // ...

    return response;
  }
}
