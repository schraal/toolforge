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


import nl.toolforge.core.util.collection.CollectionUtil;
import nl.toolforge.karma.core.boot.WorkingContext;
import nl.toolforge.karma.core.cmd.ActionCommandResponse;
import nl.toolforge.karma.core.cmd.AntErrorMessage;
import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandLoadException;
import nl.toolforge.karma.core.cmd.CommandMessage;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.ErrorMessage;
import nl.toolforge.karma.core.cmd.StatusMessage;
import nl.toolforge.karma.core.cmd.SuccessMessage;
import nl.toolforge.karma.core.cmd.util.DependencyException;
import nl.toolforge.karma.core.cmd.util.DependencyHelper;
import nl.toolforge.karma.core.cmd.util.DescriptorReader;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.manifest.Module;
import nl.toolforge.karma.core.manifest.ModuleDigester;
import nl.toolforge.karma.core.manifest.ModuleTypeException;
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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author D.A. Smedes
 * @author W.H. Schraal
 *
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

    DependencyHelper helper = new DependencyHelper(getCurrentManifest());

    try {
      //first, when not explicitly set off, run the unit tests
      if ( ! getCommandLine().hasOption("n") ) {

        logger.info("Going to run the unit tests before packaging.");

        Command command = null;
        try {
          String commandLineString = "tm -m " + module.getName();

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
        } catch (CommandLoadException e) {
          throw new CommandException(e.getErrorCode(), e.getMessageArguments());
        } finally {
          if ( command != null ) {
            command.deregisterCommandResponseListener(getResponseListener());
          }
        }

      } else {
        logger.info("User has explicitly disabled running the unit tests.");
      }

      File packageName = new File(getBuildEnvironment().getModuleBuildRootDirectory(), helper.resolveArchiveName(getCurrentModule()));

      if (getCurrentModule().getType().equals(Module.JAVA_WEB_APPLICATION)) {
        packageWar(packageName);
      } else if (getCurrentModule().getType().equals(Module.JAVA_ENTERPRISE_APPLICATION)) {
        packageEar(packageName);
      } else {
        packageJar(packageName);
      }

      CommandMessage message =
          new SuccessMessage(
              getFrontendMessages().getString("message.MODULE_PACKAGED"),
              new Object[] {getCurrentModule().getName(), packageName});
      commandResponse.addMessage(message);

    } catch (DependencyException d) {
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    } catch (ModuleTypeException d) {
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    }
  }

  private void packageJar(File packageName) throws CommandException {

    try {


      Project project = getProjectInstance();

      Target target = new Target();
      target.setName("project");
      target.setProject(project);

      project.addTarget(target);

      executeDelete(getBuildEnvironment().getModuleBuildDirectory(), "*.jar");

      Copy copy = null;
      FileSet fileSet = null;

      //copy resources
      commandResponse.addMessage(new StatusMessage("Copying the resources..."));
      if (new File(getCurrentModule().getBaseDir(), "resources").exists()) {
        copy = new Copy();
        copy.setProject(getProjectInstance());
        copy.setTodir(getBuildEnvironment().getModulePackageDirectory());
        copy.setOverwrite(true);

        fileSet = new FileSet();
        fileSet.setDir(new File(getCurrentModule().getBaseDir(), "resources"));
        fileSet.setIncludes("**/*");

        copy.addFileset(fileSet);
        target.addTask(copy);
      } else {
        commandResponse.addMessage(new StatusMessage("No resources available."));
      }

      //copy META-INF
      commandResponse.addMessage(new StatusMessage("Copying the META-INF..."));
      copy = new Copy();
      copy.setProject(getProjectInstance());
      copy.setTodir(getBuildEnvironment().getModulePackageDirectory());
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
        copy.setTodir(getBuildEnvironment().getModulePackageDirectory());
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
      jar.setBasedir(getBuildEnvironment().getModulePackageDirectory());
      jar.setExcludes("*.jar");
      target.addTask(jar);

      project.executeTarget("project");

    } catch (BuildException e) {
e.printStackTrace();
      if (logger.isDebugEnabled()) {
        commandResponse.addMessage(new AntErrorMessage(e));
      }
      throw new CommandException(e, CommandException.PACKAGE_FAILED, new Object[] {getCurrentModule().getName()});
    } catch (ModuleTypeException e) {
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }

  }

  private void packageWar(File packageName) throws CommandException {

    DependencyHelper helper = new DependencyHelper(getCurrentManifest());

    try {
      executeDelete(getBuildEnvironment().getModuleBuildDirectory(), "*.war");

      Copy copy = new Copy();
      copy.setProject(getProjectInstance());
      copy.setTodir(getBuildEnvironment().getModulePackageDirectory());
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

        copy.addFileset(fileSet);
        copy.execute();
      }

      // Copy dependencies
      //

      Set moduleDeps = helper.getModuleDependencies(getCurrentModule());
      Set jarDeps = helper.getJarDependencies(getCurrentModule());

      if (moduleDeps.size() > 0 || jarDeps.size() > 0) {

        copy = new Copy();
        copy.setProject(getProjectInstance());
        copy.setTodir(new File(getBuildEnvironment().getModulePackageDirectory(), "WEB-INF/lib"));
        copy.setFlatten(true);

        // Module dependencies
        //
        if (moduleDeps.size() > 0) {
          fileSet = new FileSet();
          fileSet.setDir(getBuildEnvironment().getModuleBuildDirectory());
          fileSet.setIncludes(CollectionUtil.concat(moduleDeps, ','));
          copy.addFileset(fileSet);
        }

        // Jar dependencies
        //
        try {
          if (jarDeps != null && !"".equals(jarDeps)) {
            fileSet = new FileSet();
            fileSet.setDir(WorkingContext.getLocalRepository());
            fileSet.setIncludes(CollectionUtil.concat(jarDeps, ','));
            copy.addFileset(fileSet);
          }
        } catch (IOException e) {
          throw new CommandException(e, CommandException.PACKAGE_FAILED);
        }

        copy.execute();
      }

      // Create a war file.
      //
      War war = new War();
      war.setProject(getProjectInstance());
      war.setDestFile(packageName);
      war.setBasedir(getBuildEnvironment().getModulePackageDirectory());
      war.setWebxml(new File(getCurrentModule().getBaseDir(), "WEB-INF/web.xml".replace('/', File.separatorChar)));

      war.execute();

    } catch (BuildException e) {
//      e.printStackTrace();
      if (logger.isDebugEnabled()) {
        commandResponse.addMessage(new AntErrorMessage(e));
      }
      throw new CommandException(e, CommandException.PACKAGE_FAILED, new Object[] {getCurrentModule().getName()});
    } catch (DependencyException d) {
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    } catch (ModuleTypeException d) {
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    }

  }

  private void packageEar(File packageName) throws CommandException {

    DependencyHelper helper = new DependencyHelper(getCurrentManifest());

    final String ARCHIVES_PROPERTIES = "archives.properties";
    final String ARCHIVES_INCLUDES   = "archives.includes";

    try {

      // Create an ear-file
      //
      //reading the application.xml
      DescriptorReader reader = new DescriptorReader(DescriptorReader.APPLICATION_XML);
      try {
        reader.parse(new File(getCurrentModule().getBaseDir(), "META-INF"));
      } catch (IOException e) {
        throw new CommandException(CommandException.PACKAGE_FAILED_NO_APPLICATION_XML, new Object[]{module.getName()});
      } catch (SAXException e) {
        throw new CommandException(CommandException.PACKAGE_FAILED_INVALID_APPLICATION_XML, new Object[]{module.getName()});
      }

      //the application.xml is parsed for included modules.
      //modules are included as follows: @<module_name>@
      //these inclusions are replaced with the name of the packaged module.
      Map map = new Hashtable();
      for (Iterator it = reader.getModuleNames().iterator(); it.hasNext(); ) {
        String moduleName = ((StringBuffer) it.next()).toString();
//System.out.println("FOund module name: "+moduleName);
        Pattern p = Pattern.compile("@("+ModuleDigester.NAME_PATTERN_STRING+")@");
        Matcher m = p.matcher(moduleName);

        if (m.matches()) {
          moduleName = m.group(1);
          Module module = getCurrentManifest().getModule(moduleName);
          map.put(moduleName, helper.resolveArchiveName(module));
        } else {
          //todo: throw new Exception();
        }
      }

      //create a archive.properties and a archives.includes.
      //the first one is used for replacing the special tokens in the application.xml
      //the second one is used for packaging purposes.
      try {
        File moduleBuildDir = getBuildEnvironment().getModuleBuildDirectory();
        moduleBuildDir.mkdirs();
        File archivesProperties = new File(moduleBuildDir, ARCHIVES_PROPERTIES);
        File archivesIncludes = new File(moduleBuildDir, ARCHIVES_INCLUDES);
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

      commandResponse.addMessage(new StatusMessage("Deleting previous ear file."));
      executeDelete(getBuildEnvironment().getModuleBuildDirectory(), "*.ear");

      commandResponse.addMessage(new StatusMessage("Copying META-INF dir."));
      Copy copy = new Copy();
      copy.setProject(getProjectInstance());
      copy.setTodir(getBuildEnvironment().getModulePackageDirectory());
      copy.setOverwrite(true);

      FileSet fileSet = new FileSet();
      fileSet.setDir(getCurrentModule().getBaseDir());
      fileSet.setIncludes("META-INF/**");
      fileSet.setExcludes("META-INF/application.xml");

      // Filtering
      //
      FilterSet filterSet = copy.createFilterSet();
      filterSet.setFiltersfile(new File(getBuildEnvironment().getModuleBuildDirectory(), ARCHIVES_PROPERTIES));

      copy.addFileset(fileSet);
      copy.execute();

      commandResponse.addMessage(new StatusMessage("Copying module dependencies"));
      copy = new Copy();
      copy.setProject(getProjectInstance());
      copy.setTodir(getBuildEnvironment().getModulePackageDirectory());
      copy.setFlatten(true);

      //copy the module dependencies from the application.xml
      fileSet = new FileSet();

      fileSet.setDir(getBuildEnvironment().getManifestBuildDirectory());
      fileSet.setIncludesfile(new File(getBuildEnvironment().getModuleBuildDirectory(), ARCHIVES_INCLUDES));
      copy.addFileset(fileSet);
      copy.execute();

      //copy the non-module dependencies to /lib
      copy = new Copy();
      copy.setProject(getProjectInstance());
      copy.setTodir(new File(getBuildEnvironment().getModulePackageDirectory(), "lib"));
      copy.setFlatten(true);

      fileSet = new FileSet();
//System.out.println("repo root: "+ WorkingContext.getLocalRepository());
      fileSet.setDir(WorkingContext.getLocalRepository());
//System.out.println("jar deps: "+CollectionUtil.concat(helper.getJarDependencies(getCurrentModule()), ','));
      fileSet.setIncludes(CollectionUtil.concat(helper.getJarDependencies(getCurrentModule(), true), ','));
      copy.addFileset(fileSet);
      copy.execute();

      commandResponse.addMessage(new StatusMessage("Creating ear"));

      Ear ear = new Ear();
      ear.setProject(getProjectInstance());
      ear.setDestFile(packageName);
      ear.setBasedir(getBuildEnvironment().getModulePackageDirectory());
      ear.setAppxml(new File(getBuildEnvironment().getModulePackageDirectory(), "META-INF/application.xml".replace('/', File.separatorChar)));

      ear.execute();

    } catch (ManifestException m) {
m.printStackTrace();
      throw new CommandException(m.getErrorCode(), m.getMessageArguments());
    } catch (BuildException e) {
e.printStackTrace();
      if (logger.isDebugEnabled()) {
        commandResponse.addMessage(new AntErrorMessage(e));
      }
      throw new CommandException(e, CommandException.PACKAGE_FAILED, new Object[] {getCurrentModule().getName()});
    } catch (DependencyException d) {
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    } catch (IOException e) {
      throw new CommandException(e, CommandException.PACKAGE_FAILED);
    } catch (ModuleTypeException d) {
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    }

  }

  public CommandResponse getCommandResponse() {
    return commandResponse;
  }
  
}
