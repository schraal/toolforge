package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.DefaultCommand;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ManifestException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Javadoc;
import org.apache.tools.ant.taskdefs.Echo;

import java.io.File;

/**
 * Generates API documentation for the given module.
 *
 * @author W.H. Schraal
 */
public class DocModule extends AbstractBuildCommand {

  protected CommandResponse response = new ActionCommandResponse();

  public DocModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    try {
      Project project = getProjectInstance();

      Echo blaat = new Echo();
      blaat.setProject(project);
      blaat.addText("blaat");
      blaat.execute();

//      Javadoc javadoc = new Javadoc();
//      javadoc.setProject(project);
//      javadoc.setAuthor(true);
//      javadoc.setDestdir(getModuleBuildDirectory());
//      javadoc.setSource(getSourceDirectory().getPath());
//      javadoc.setPackagenames("nl.*");
//
//      javadoc.execute();

    } catch (BuildException e) {
      e.printStackTrace();
//    } catch (ManifestException e) {
//      e.printStackTrace();
    }

    //To change body of implemented methods use File | Settings | File Templates.
  }

  public CommandResponse getCommandResponse() {
    return response;
  }

  protected File getSourceDirectory() throws ManifestException {
    return new File(getCurrentModule().getBaseDir(), "src/java");
  }
}
