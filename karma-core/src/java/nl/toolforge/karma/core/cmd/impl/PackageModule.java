package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.build.BuildUtil;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.SourceModule;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.File;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class PackageModule extends AbstractBuildCommand {

  private static final String MODULE_PACKAGE_NAME_PROPERTY = "module-package-name";
  private static  final String MODULE_WEBXML_PROPERTY = "module-webxml";

  private CommandResponse commandResponse = new ActionCommandResponse();

  public PackageModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    Project project = getAntProject();

    try {

      project.setProperty(MODULE_BUILD_DIR_PROPERTY, getBuildDirectory().getPath());
      project.setProperty(MODULE_PACKAGE_NAME_PROPERTY, new File(getBuildDirectory(), getCurrentManifest().resolveJarName(getCurrentModule())).getPath());

      if (getCurrentModule().getName().startsWith(Module.WEBAPP_PREFIX)) {
        // Create a war-file
        //
        project.setProperty(MODULE_WEBXML_PROPERTY, new File(getCurrentModule().getBaseDir(), "WEB-INF/web.xml".replace('/', File.separatorChar)).getPath());
        project.setProperty(MODULE_BASEDIR_PROPERTY, getCurrentModule().getBaseDir().getPath());
        project.executeTarget(BUILD_TARGET_WAR);
      } else {
        // Create a jar-file
        //
        project.executeTarget(BUILD_TARGET_JAR);
      }
      CommandMessage message = new SimpleCommandMessage("Module " + getCurrentModule().getName() + " packaged succesfully."); // todo localize message
      commandResponse.addMessage(message);

    } catch (ManifestException m) {
      throw new CommandException(m.getErrorCode(), m.getMessageArguments());
    }catch (BuildException e) {
      e.printStackTrace();
      throw new CommandException(CommandException.BUILD_FAILED, new Object[] {getCurrentModule().getName()});
    }
  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }

}
