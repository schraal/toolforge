package nl.toolforge.karma.cli.cmd;

import nl.toolforge.karma.core.cmd.impl.UpdateModuleCommand;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.Module;
import nl.toolforge.karma.core.KarmaException;

/**
 * Implementation for the <code>UpdateModuleCommand</code> for the command-line interface application.
 *
 * @author D.A. Smedes 
 * 
 * @version $Id:
 */
public class UpdateModuleImpl extends UpdateModuleCommand {


  public UpdateModuleImpl(Module module) throws CommandException {
    super(module);
  }

  public CommandResponse execute() throws KarmaException {
    return super.execute();
  }

}