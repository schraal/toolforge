package nl.toolforge.karma.core.cmd.impl;

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SimpleCommandMessage;
import nl.toolforge.karma.core.cmd.util.DescriptorReader;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ModuleDescriptor;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public class PackageModule extends AbstractBuildCommand {

  private static final String MODULE_PACKAGE_NAME_PROPERTY = "module-package-name";
  private static  final String MODULE_APPXML_PROPERTY = "module-appxml";
  private static  final String MODULE_WEBXML_PROPERTY = "module-webxml";
  private static  final String MODULE_INCLUDES_PROPERTY = "module-includes";
  private static  final String MODULE_EXCLUDES_PROPERTY = "module-excludes";

  private CommandResponse commandResponse = new ActionCommandResponse();

  public PackageModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    Project project = getAntProject();

    try {

      project.setProperty(MODULE_BUILD_DIR_PROPERTY, getBuildDirectory().getPath());
      project.setProperty(MODULE_PACKAGE_NAME_PROPERTY, new File(getBuildDirectory(), getCurrentManifest().resolveArchiveName(getCurrentModule())).getPath());
      project.setProperty(MODULE_BASEDIR_PROPERTY, getCurrentModule().getBaseDir().getPath());

      if (getCurrentModule().getName().startsWith(Module.WEBAPP_PREFIX)) {

        // Create a war-file
        //
        project.setProperty(MODULE_WEBXML_PROPERTY, new File(getCurrentModule().getBaseDir(), "WEB-INF/web.xml".replace('/', File.separatorChar)).getPath());

        // We always assume that 'web' and 'resources' exist.
        // todo this should be solved like Maven does ???
        project.setProperty(MODULE_INCLUDES_PROPERTY, "web/**,resources/**");

        project.setProperty(MODULE_EXCLUDES_PROPERTY, "*.war");
        project.executeTarget(BUILD_TARGET_WAR);

      } else if (getCurrentModule().getName().startsWith(Module.EAPP_PREFIX)) {

        // Create an ear-file
        //
        DescriptorReader reader = new DescriptorReader(DescriptorReader.APPLICATION_XML);

        try {
          reader.parse(new File(getCurrentModule().getBaseDir(), "META-INF"));
        } catch (IOException e) {
          e.printStackTrace();
        } catch (SAXException e) {
          e.printStackTrace();
        }

        Map map = new Hashtable();
        for (Iterator it = reader.getModuleNames().iterator(); it.hasNext(); ) {
          String moduleName = ((StringBuffer) it.next()).toString();

          Pattern p = Pattern.compile("@("+ModuleDescriptor.NAME_PATTERN_STRING+")@");
          Matcher m = p.matcher(moduleName);

          if (m.matches()) {
            moduleName = m.group(1);
            Module module = getCurrentManifest().getModule(moduleName);
            map.put(moduleName, getCurrentManifest().resolveArchiveName(module));
          } else {
            //todo: throw new Exception();
          }
        }

        try {
          FileWriter write1 = new FileWriter(new File(getBuildDirectory(), "archives.properties"));
          FileWriter write2 = new FileWriter(new File(getBuildDirectory(), "archives.includes"));

          for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            String value = (String) map.get(key);

            write1.write(key+"="+value+"\n");
            write2.write(key+"/"+value+"\n");
          }

          write1.close();
          write2.close();

        } catch (IOException e) {
          e.printStackTrace();
        }

        project.setProperty(MODULE_APPXML_PROPERTY, new File(getBuildDirectory(), "META-INF/application.xml".replace('/', File.separatorChar)).getPath());
        project.setProperty(MODULE_INCLUDES_PROPERTY, "META-INF/**,resources/**");
        project.setProperty(MODULE_EXCLUDES_PROPERTY, "*.ear,archives.*");
        project.executeTarget(BUILD_TARGET_EAR);
      } else {
        // Create a jar-file
        //
        project.setProperty(MODULE_EXCLUDES_PROPERTY, "*.jar");
        project.setProperty(MODULE_INCLUDES_PROPERTY, "META-INF/**,resources/**");
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
