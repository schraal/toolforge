package nl.toolforge.karma.core.cmd.impl;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.manifest.ManifestException;

/**
 * Remove all built stuff of the given module.
 *
 * @author W.H. Schraal
 * @version $Id$
 */
public class CleanModule extends AbstractBuildCommand {

  private CommandResponse commandResponse = new ActionCommandResponse();

  public CleanModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    CommandMessage message = null;

    Project project = getAntProject();

    try {
      File buildBase = getModuleBuildDirectory();
      if (!buildBase.exists()) {
        // No point in removing built stuff if it isn't there
        //
        throw new CommandException(CommandException.NO_MODULE_BUILD_DIR, new Object[] {getCurrentModule().getName(), getModuleBuildDirectory().getPath()});
      }

      // Configure the Ant project
      //
      project.setProperty(MODULE_BUILD_DIR_PROPERTY, getModuleBuildDirectory().getPath());

      try {
        project.executeTarget(CLEAN_MODULE_TARGET);

        // todo: localize message
        message = new SuccessMessage("Module " + getCurrentModule().getName() + " cleaned succesfully.");
        commandResponse.addMessage(message);

      } catch (BuildException e) {
        e.printStackTrace();
        throw new CommandException(CommandException.CLEAN_MODULE_FAILED, new Object[] {getModuleBuildDirectory().getPath()});
      }
    } catch (ManifestException e) {
      e.printStackTrace();
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

  protected File getSourceDirectory() throws ManifestException {
    return null;
  }

}
