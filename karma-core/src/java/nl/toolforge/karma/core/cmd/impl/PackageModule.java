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

import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import nl.toolforge.karma.core.cmd.AntErrorMessage;
import nl.toolforge.karma.core.cmd.util.DescriptorReader;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ModuleDescriptor;
import nl.toolforge.karma.core.LocalEnvironment;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

  private static final Log logger = LogFactory.getLog(PackageModule.class);

  private static final String MODULE_PACKAGE_NAME_PROPERTY = "module-package-name";
  private static final String MODULE_APPXML_PROPERTY = "module-appxml";
  private static final String MODULE_WEBXML_PROPERTY = "module-webxml";
  private static final String MODULE_INCLUDES_PROPERTY = "module-includes";
  private static final String MODULE_EXCLUDES_PROPERTY = "module-excludes";

  private CommandResponse commandResponse = new ActionCommandResponse();

  public PackageModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    Project project = getAntProject();

    try {

      project.setProperty(MODULE_BUILD_DIR_PROPERTY, getModuleBuildDirectory().getPath());
      project.setProperty(MODULE_COMPILE_DIR_PROPERTY, getCompileDirectory().getPath());
      project.setProperty(MODULE_PACKAGE_DIR_PROPERTY, getPackageDirectory().getPath());

      String packageName = new File(getModuleBuildDirectory(), getCurrentManifest().resolveArchiveName(getCurrentModule())).getPath();
      project.setProperty(MODULE_PACKAGE_NAME_PROPERTY, packageName);
      project.setProperty(MODULE_BASEDIR_PROPERTY, getCurrentModule().getBaseDir().getPath());

      if (getCurrentModule().getDeploymentType().equals(Module.WEBAPP)) {

        // Create a war-file
        //
        project.setProperty(MODULE_WEBXML_PROPERTY, new File(getCurrentModule().getBaseDir(), "WEB-INF/web.xml".replace('/', File.separatorChar)).getPath());

        // Relative to Module.getBaseDir().
        //
        project.setProperty(
            MODULE_INCLUDES_PROPERTY,
            "web/**, " +
            "WEB-INF/lib/**, " +
            "WEB-INF/resources/**"
        );

        // The base dir for module dependencies.
        //
        project.setProperty(MANIFEST_BUILD_DIR, getModuleBuildDirectory().getParent());

        // Include all module-dependencies --> copied to WEB-INF/lib
        //
        String moduleDeps = getModuleDependencies(getCurrentModule().getDependencies(), true, DEPENDENCY_SEPARATOR_CHAR);
        project.setProperty(MODULE_MODULE_DEPENDENCIES_PROPERTY, moduleDeps);

        // Set the base location for jar dependencies.
        //
        project.setProperty(KARMA_JAR_REPOSITORY_PROPERTY, LocalEnvironment.getLocalRepository().getPath());

        // Include all jar dependencies --> copied to WEB-INF/lib
        //
        String jarDeps = getJarDependencies(getCurrentModule().getDependencies(), true, DEPENDENCY_SEPARATOR_CHAR);
        project.setProperty(MODULE_JAR_DEPENDENCIES_PROPERTY, jarDeps);

//        project.setProperty(MODULE_EXCLUDES_PROPERTY, "*.war");
        project.executeTarget(BUILD_TARGET_WAR);

      } else if (getCurrentModule().getDeploymentType().equals(Module.EAPP)) {

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
          FileWriter write1 = new FileWriter(new File(getModuleBuildDirectory(), "archives.properties"));
          FileWriter write2 = new FileWriter(new File(getModuleBuildDirectory(), "archives.includes"));

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

        project.setProperty(MODULE_APPXML_PROPERTY, new File(getModuleBuildDirectory(), "META-INF/application.xml".replace('/', File.separatorChar)).getPath());
        project.setProperty(MODULE_INCLUDES_PROPERTY, "META-INF/**");
        project.setProperty(MODULE_EXCLUDES_PROPERTY, "*.ear,archives.*");
        project.executeTarget(BUILD_TARGET_EAR);
      } else {
        // Create a jar-file
        //
        project.setProperty(MODULE_EXCLUDES_PROPERTY, "*.jar");
        project.setProperty(MODULE_INCLUDES_PROPERTY, "META-INF/**");
        project.executeTarget(BUILD_TARGET_JAR);
      }

      CommandMessage message =
          new SuccessMessage(
              getFrontendMessages().getString("message.MODULE_PACKAGED"),
              new Object[] {getCurrentModule().getName(), packageName});
      commandResponse.addMessage(message);

    } catch (ManifestException m) {
      throw new CommandException(m.getErrorCode(), m.getMessageArguments());
    }catch (BuildException e) {
//      e.printStackTrace();
      if (logger.isDebugEnabled()) {
        commandResponse.addMessage(new AntErrorMessage(e));
      }
      throw new CommandException(e, CommandException.BUILD_FAILED, new Object[] {getCurrentModule().getName()});
    }
  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }

  protected File getSourceDirectory() throws ManifestException {
    //package does not need the sources.
    return null;
  }


}
