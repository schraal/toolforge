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
import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.manifest.ManifestException;

/**
 * Remove all built stuff.
 *
 * @author W.H. Schraal
 * @version $Id$
 */
public class CleanAll extends DefaultCommand {

  private CommandResponse commandResponse = new ActionCommandResponse();

  public CleanAll(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

//    super.execute();

    CommandMessage message = null;

//    Project project = getAntProject();

//    try {
//      File buildBase = getBuildDirectory();
//      if (!buildBase.exists()) {
        // No point in removing built stuff if it isn't there
        //
//        throw new CommandException(CommandException.NO_BUILD_DIR);
//      }

      // Configure the Ant project
      //
      // The base dir
      //
//      project.setProperty(MANIFEST_BUILD_DIR, getBuildDirectory().getParent());

      try {
//        project.executeTarget(CLEAN_ALL_TARGET);

        // todo: localize message
        message = new SuccessMessage("All modules cleaned succesfully.");
        commandResponse.addMessage(message);

      } catch (BuildException e) {
        e.printStackTrace();
        throw new CommandException(CommandException.CLEAN_ALL_FAILED);
      }
//    } catch (ManifestException e) {
//      e.printStackTrace();
//      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
//    }
  }

  public CommandResponse getCommandResponse() {
    return this.commandResponse;
  }

}