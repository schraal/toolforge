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
import nl.toolforge.karma.core.cmd.AntErrorMessage;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.util.DescriptorReader;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ModuleDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Ear;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.War;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterSet;
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

  private CommandResponse commandResponse = new ActionCommandResponse();

  public PackageModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    try {
      //first, when not explicitly set off, run the unit tests
      if ( ! getCommandLine().hasOption("nt") ) {
        logger.info("Going to run the unit tests before packaging.");

        Command command = null;
        try {
          String commandLineString = "tm -m " + module.getName();
System.out.println("Going to: "+commandLineString);
          command = CommandFactory.getInstance().getCommand(commandLineString);
          command.setContext(getContext());
          command.registerCommandResponseListener(getResponseListener());
          command.execute();
        } catch (CommandException ce) {
          if (ce.getErrorCode().equals(CommandException.TEST_FAILED)) {
            commandResponse.addMessage(new ErrorMessage(ce.getErrorCode(), ce.getMessageArguments()));
            throw new CommandException(ce, CommandException.PACKAGE_FAILED, new Object[]{module.getName()});
          } else {
            CommandMessage message = new ErrorMessage(ce.getErrorCode(), ce.getMessageArguments());
            commandResponse.addMessage(message);
            message = new ErrorMessage(CommandException.TEST_WARNING);
            commandResponse.addMessage(message);
          }
        } finally {
          if ( command != null ) {
            command.deregisterCommandResponseListener(getResponseListener());
          }
        }

      } else {
        logger.info("User has explicitly disabled running the unit tests.");
      }


      File packageName = new File(getModuleBuildDirectory(), getCurrentManifest().resolveArchiveName(getCurrentModule()));

      if (getCurrentModule().getDeploymentType().equals(Module.WEBAPP)) {
        packageWar(packageName);
      } else if (getCurrentModule().getDeploymentType().equals(Module.EAPP)) {
        packageEar(packageName);
      } else {
        packageJar(packageName);
      }

      CommandMessage message =
          new SuccessMessage(
              getFrontendMessages().getString("message.MODULE_PACKAGED"),
              new Object[] {getCurrentModule().getName(), packageName});
      commandResponse.addMessage(message);

    } catch (ManifestException m) {
      throw new CommandException(m.getErrorCode(), m.getMessageArguments());
    }
  }

  private void packageJar(File packageName) throws CommandException {

    try {

      Project project = getProjectInstance();

      Target target = new Target();
      target.setName("project");
      target.setProject(project);

      project.addTarget(target);

      executeDelete(getModuleBuildDirectory(), "*.jar");

      Copy copy = null;

      copy = new Copy();
      copy.setProject(getProjectInstance());
      copy.setTodir(getPackageDirectory());
      copy.setOverwrite(true);

      FileSet fileSet = new FileSet();
      fileSet.setDir(new File(getCurrentModule().getBaseDir(), "resources"));
      fileSet.setIncludes("**/*");

      copy.addFileset(fileSet);
      target.addTask(copy);

      copy = new Copy();
      copy.setProject(getProjectInstance());
      copy.setTodir(getPackageDirectory());
      copy.setOverwrite(true);

      fileSet = new FileSet();
      fileSet.setDir(getCurrentModule().getBaseDir());
      fileSet.setIncludes("META-INF/**");
      fileSet.setExcludes("resources");

      copy.addFileset(fileSet);
      target.addTask(copy);

      // Copy all class files to the package directory.
      //
      if (getCompileDirectory().exists()) {
        copy = new Copy();
        copy.setProject(getProjectInstance());
        copy.setTodir(getPackageDirectory());
        copy.setOverwrite(true);

        fileSet = new FileSet();
        fileSet.setDir(getCompileDirectory());
        fileSet.setIncludes("**/*.class");

        copy.addFileset(fileSet);
        target.addTask(copy);
      }

      Jar jar = new Jar();
      jar.setProject(getProjectInstance());
      jar.setDestFile(packageName);
      jar.setBasedir(getPackageDirectory());
      jar.setExcludes("*.jar");
      target.addTask(jar);

      project.executeTarget("project");

    } catch (BuildException e) {
      e.printStackTrace();
      if (logger.isDebugEnabled()) {
        commandResponse.addMessage(new AntErrorMessage(e));
      }
      throw new CommandException(e, CommandException.PACKAGE_FAILED, new Object[] {getCurrentModule().getName()});
    }

  }

  private void packageWar(File packageName) throws CommandException {

    try {
      executeDelete(getModuleBuildDirectory(), "*.war");
      executeDelete(getPackageDirectory());
      executeMkdir(getPackageDirectory());

      Copy copy = null;

      copy = new Copy();
      copy.setProject(getProjectInstance());
      copy.setTodir(getPackageDirectory());
      copy.setOverwrite(true);

      FileSet fileSet = new FileSet();
      fileSet.setDir(getCurrentModule().getBaseDir());
      fileSet.setIncludes("WEB-INF/**");
      fileSet.setExcludes("WEB-INF/web.xml");

      copy.addFileset(fileSet);
      copy.execute();

      // Fileset that copies contents of 'web' to the package directory.
      //
      File webdir = new File(getCurrentModule().getBaseDir(), "web");
      if (webdir.exists()) {
        fileSet = new FileSet();
        fileSet.setDir(webdir);
        fileSet.setIncludes("**");
//      fileSet.setExcludes("web");

        copy.addFileset(fileSet);
        copy.execute();
      }

      // Copy dependencies
      //

      String moduleDeps = getModuleDependencies(getCurrentModule().getDependencies(), true, DEPENDENCY_SEPARATOR_CHAR);
      String jarDeps = getJarDependencies(getCurrentModule().getDependencies(), true, DEPENDENCY_SEPARATOR_CHAR);

      if ( (moduleDeps != null && !"".equals(moduleDeps)) ||
          (jarDeps != null && !"".equals(jarDeps))) {

        copy = new Copy();
        copy.setProject(getProjectInstance());
        copy.setTodir(new File(getPackageDirectory(), "WEB-INF/lib"));
        copy.setFlatten(true);

        // Module dependencies
        //
        if (moduleDeps != null && !"".equals(moduleDeps)) {
          fileSet = new FileSet();
          fileSet.setDir(getBuildDirectory());
          fileSet.setIncludes(moduleDeps);
          copy.addFileset(fileSet);
        }

        // Jar dependencies
        //
        if (jarDeps != null && !"".equals(jarDeps)) {
          fileSet = new FileSet();
          fileSet.setDir(getWorkingContext().getLocalRepository());
          fileSet.setIncludes(jarDeps);
          copy.addFileset(fileSet);
        }

        copy.execute();
      }

      // Create a war file.
      //
      War war = new War();
      war.setProject(getProjectInstance());
      war.setDestFile(packageName);
      war.setBasedir(getPackageDirectory());
      war.setWebxml(new File(getCurrentModule().getBaseDir(), "WEB-INF/web.xml".replace('/', File.separatorChar)));

      war.execute();
    } catch (ManifestException m) {
      throw new CommandException(m.getErrorCode(), m.getMessageArguments());
    } catch (BuildException e) {
//      e.printStackTrace();
      if (logger.isDebugEnabled()) {
        commandResponse.addMessage(new AntErrorMessage(e));
      }
      throw new CommandException(e, CommandException.PACKAGE_FAILED, new Object[] {getCurrentModule().getName()});
    }

  }

  private void packageEar(File packageName) throws CommandException {

    try {


      // Create an ear-file
      //
      DescriptorReader reader = new DescriptorReader(DescriptorReader.APPLICATION_XML);

      try {
        reader.parse(new File(getCurrentModule().getBaseDir(), "META-INF"));
      } catch (IOException e) {
        throw new CommandException(CommandException.PACKAGE_FAILED_NO_APPLICATION_XML, new Object[]{module.getName()});
      } catch (SAXException e) {
        throw new CommandException(CommandException.PACKAGE_FAILED_INVALID_APPLICATION_XML, new Object[]{module.getName()});
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
        File moduleBuildDir = getModuleBuildDirectory();
        moduleBuildDir.mkdirs();
        File archivesProperties = new File(moduleBuildDir, "archives.properties");
        File archivesIncludes = new File(moduleBuildDir, "archives.includes");
        archivesProperties.createNewFile();
        archivesIncludes.createNewFile();
        FileWriter write1 = new FileWriter(archivesProperties);
        FileWriter write2 = new FileWriter(archivesIncludes);

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

      executeDelete(getModuleBuildDirectory(), "*.ear;archives.*");

      Copy copy = null;

      copy = new Copy();
      copy.setProject(getProjectInstance());
      copy.setTodir(getPackageDirectory());
      copy.setOverwrite(true);

      FileSet fileSet = new FileSet();
      fileSet.setDir(getCurrentModule().getBaseDir());
      fileSet.setIncludes("META-INF/**");

      // Filtering
      //
      FilterSet filterSet = copy.createFilterSet();
      filterSet.setFiltersfile(new File(getModuleBuildDirectory(), "archives.properties"));

      copy.addFileset(fileSet);
      copy.execute();


      copy = new Copy();
      copy.setProject(getProjectInstance());
      copy.setTodir(getPackageDirectory());
      copy.setFlatten(true);

      fileSet = new FileSet();
      fileSet.setDir(getModuleBuildDirectory());
      fileSet.setIncludesfile(new File(getModuleBuildDirectory(), "arvices.includes"));

      copy.addFileset(fileSet);
      copy.execute();


      Ear ear = new Ear();
      ear.setProject(getProjectInstance());
      ear.setDestFile(packageName);
      ear.setBasedir(getPackageDirectory());
      ear.setAppxml(new File(getModuleBuildDirectory(), "META-INF/application.xml".replace('/', File.separatorChar)));

      ear.execute();

    } catch (ManifestException m) {
      throw new CommandException(m.getErrorCode(), m.getMessageArguments());
    } catch (BuildException e) {
//      e.printStackTrace();
      if (logger.isDebugEnabled()) {
        commandResponse.addMessage(new AntErrorMessage(e));
      }
      throw new CommandException(e, CommandException.PACKAGE_FAILED, new Object[] {getCurrentModule().getName()});
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
