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


import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Ear;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.War;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.ZipFileSet;
import org.xml.sax.SAXException;

import nl.toolforge.karma.core.cmd.Command;
import nl.toolforge.karma.core.cmd.CommandDescriptor;
import nl.toolforge.karma.core.cmd.CommandException;
import nl.toolforge.karma.core.cmd.CommandFactory;
import nl.toolforge.karma.core.cmd.CommandLoadException;
import nl.toolforge.karma.core.cmd.CommandResponse;
import nl.toolforge.karma.core.cmd.event.ErrorEvent;
import nl.toolforge.karma.core.cmd.event.ExceptionEvent;
import nl.toolforge.karma.core.cmd.event.MessageEvent;
import nl.toolforge.karma.core.cmd.event.SimpleMessage;
import nl.toolforge.karma.core.cmd.util.DependencyException;
import nl.toolforge.karma.core.cmd.util.DependencyHelper;
import nl.toolforge.karma.core.cmd.util.DependencyPath;
import nl.toolforge.karma.core.cmd.util.DescriptorReader;
import nl.toolforge.karma.core.manifest.ManifestException;
import nl.toolforge.karma.core.module.Module;
import nl.toolforge.karma.core.module.ModuleDigester;
import nl.toolforge.karma.core.module.ModuleTypeException;

/**
 * @author D.A. Smedes
 * @author W.H. Schraal
 *
 * @version $Id$
 */
public class PackageModule extends AbstractBuildCommand {

  public static final String COMMAND_NAME = "package-module";

  private static final Log logger = LogFactory.getLog(PackageModule.class);

  private CommandResponse commandResponse = new CommandResponse();

  public PackageModule(CommandDescriptor descriptor) {
    super(descriptor);
  }

  public void execute() throws CommandException {

    super.execute();

    DependencyHelper helper = new DependencyHelper(getCurrentManifest());

    try {
      boolean dependenciesChecked = false;
      while (!dependenciesChecked) {
        try {
          helper.getModuleDependencies(getCurrentModule(), false, true);
          dependenciesChecked = true;
        } catch (DependencyException de) {
          if (de.getErrorCode().equals(DependencyException.DEPENDENCY_NOT_FOUND)) {
            //a dependency was not found.
            //if it's a module, package it.
            //else, rethrow the exception, since we can do nothing about it.
            String dep = (String) de.getMessageArguments()[0];
            try {
              Module module = getCurrentManifest().getModule(dep);
              Command command = null;
              try {
                logger.info("Module "+module.getName()+" is needed, but is not packaged yet. Doing that now.");

                String commandLineString;
                if (!getCommandLine().hasOption("n")) {
                  commandLineString = "pam -m " + module.getName();
                } else {
                  commandLineString = "pam -n -m " + module.getName();
                }
                logger.debug("Going to: "+commandLineString);
                command = CommandFactory.getInstance().getCommand(commandLineString);
                command.setContext(getContext());
                command.registerCommandResponseListener(getResponseListener());
                command.execute();

                //the dependency built successfully.
              } catch (CommandLoadException e) {
                throw new CommandException(e.getErrorCode(), e.getMessageArguments());
              } finally {
                if ( command != null ) {
                  command.deregisterCommandResponseListener(getResponseListener());
                }
              }
            } catch (ManifestException me) {
              //obviously it was not a module...
              throw de;
            }
          } else {
            //rethrow the exception. Don't know what to do with it here.
            throw de;
          }
        }
      }
    } catch (DependencyException e) {
      logger.error(e);
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    } catch (ModuleTypeException e) {
      logger.error(e);
      throw new CommandException(e.getErrorCode(), e.getMessageArguments());
    }
    //test and build the module.
    try {
      //first, when not explicitly set off, run the unit tests
      if ( ! getCommandLine().hasOption("n") ) {

        logger.info("Going to run the unit tests before packaging.");

        Command command = null;
        try {
          //test module, but do test test or build recursively
          String commandLineString = "tm -n -m " + module.getName();

          command = CommandFactory.getInstance().getCommand(commandLineString);
          command.setContext(getContext());
          command.registerCommandResponseListener(getResponseListener());
          command.execute();

        } catch (CommandException ce) {
          if (ce.getErrorCode().equals(CommandException.TEST_FAILED)) {
            commandResponse.addEvent(new ErrorEvent(this, ce.getErrorCode(), ce.getMessageArguments()));
            throw new CommandException(ce, CommandException.PACKAGE_FAILED, new Object[]{module.getName()});
          } else if (ce.getErrorCode().equals(CommandException.NO_TEST_DIR)) {
            //do not log. this has already been done.
            commandResponse.addEvent(new ErrorEvent(this, ce.getErrorCode(), ce.getMessageArguments()));
          } else {
            commandResponse.addEvent(new ErrorEvent(this, ce.getErrorCode(), ce.getMessageArguments()));
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
        Command command = null;
        try {
          //build the module first, but do not recursivily build dependencies
          String commandLineString = "bm -n -m " + module.getName();
          logger.debug("Going to: "+commandLineString);
          command = CommandFactory.getInstance().getCommand(commandLineString);
          command.setContext(getContext());
          command.registerCommandResponseListener(getResponseListener());
          command.execute();
        } catch (CommandException ce) {
          if (ce.getErrorCode().equals(CommandException.DEPENDENCY_DOES_NOT_EXIST) ||
              ce.getErrorCode().equals(CommandException.BUILD_FAILED) ||
              ce.getErrorCode().equals(DependencyException.DEPENDENCY_NOT_FOUND) ) {
            commandResponse.addEvent(new ErrorEvent(this, ce.getErrorCode(), ce.getMessageArguments()));
            throw new CommandException(ce, CommandException.PACKAGE_FAILED, new Object[]{module.getName()});
          } else if (ce.getErrorCode().equals(CommandException.NO_SRC_DIR)) {
            //do not log. this has already been done.
          } else {
            commandResponse.addEvent(new ErrorEvent(this, ce.getErrorCode(), ce.getMessageArguments()));
          }
        } catch (CommandLoadException e) {
          throw new CommandException(e.getErrorCode(), e.getMessageArguments());
        } finally {
          if ( command != null ) {
            command.deregisterCommandResponseListener(getResponseListener());
          }
        }
      }

      //do the actual packaging
      String archiveName = helper.resolveArchiveName(getCurrentModule());
      File moduleBuildRoot = getBuildEnvironment().getModuleBuildRootDirectory();
      moduleBuildRoot.mkdirs();
      File packageName = new File(moduleBuildRoot, archiveName);

      if (getCurrentModule().getType().equals(Module.JAVA_WEB_APPLICATION)) {
        packageWar(packageName);
      } else if (getCurrentModule().getType().equals(Module.JAVA_ENTERPRISE_APPLICATION)) {
        packageEar(packageName);
      } else if (getCurrentModule().getType().equals(Module.JAVA_SOURCE_MODULE)) {
        packageJar(packageName);
      } else if (getCurrentModule().getType().equals(Module.OTHER_MODULE)) {
        packageOther(packageName);
      } else {
        throw new CommandException(CommandException.PACKAGE_FAILED_WRONG_MODULE_TYPE, new Object[]{getCurrentModule()});
      }

      SimpleMessage message =
          new SimpleMessage(
              getFrontendMessages().getString("message.MODULE_PACKAGED"),
              new Object[] {getCurrentModule().getName(), archiveName});
      commandResponse.addEvent(new MessageEvent(this, message));

    } catch (DependencyException d) {
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    } catch (ModuleTypeException d) {
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    }
  }

  private void packageOther(File packageName) throws CommandException {
    Project project = getProjectInstance();

    Target target = new Target();
    target.setName("run");
    target.setProject(project);

    project.addTarget(target);

    executeDelete(getBuildEnvironment().getModuleBuildDirectory(), "*.zip");

    Copy copy = null;
    FileSet fileSet = null;

    //copy everything as-is
    if (getCurrentModule().getBaseDir().exists()) {
      copy = (Copy) project.createTask("copy");
      copy.setProject(getProjectInstance());
      copy.setTodir(getBuildEnvironment().getModulePackageDirectory());
      copy.setOverwrite(true);
      copy.setIncludeEmptyDirs(false);

      fileSet = new FileSet();
      fileSet.setDir(new File(getCurrentModule().getBaseDir(), "contents"));
      fileSet.setIncludes("**/*");

      copy.addFileset(fileSet);
      target.addTask(copy);
    } else {
      commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("No resources available.")));
    }
    project.executeTarget("run");

    if (getBuildEnvironment().getModulePackageDirectory().exists()) {
      Target target2 = new Target();
      target2.setName("zip");
      target2.setProject(project);

      project.addTarget(target2);
      Zip zip = (Zip) project.createTask("zip");
      zip.setProject(getProjectInstance());
      zip.setDestFile(packageName);
      zip.setBasedir(getBuildEnvironment().getModulePackageDirectory());
      target2.addTask(zip);
      project.executeTarget("zip");
    } else {
      throw new CommandException(CommandException.PACKAGE_FAILED_NOTHING_TO_PACKAGE, new Object[]{getCurrentModule()});
    }
  }

  private void packageJar(File packageName) throws CommandException {
    try {
      Project project = getProjectInstance();

      commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Packaging...")));
      
      Target target = new Target();
      target.setName("jar");
      target.setProject(project);

      project.addTarget(target);
      
      Jar jar = (Jar) project.createTask("jar");
      
      jar.setProject(getProjectInstance());
      jar.setDestFile(packageName);
      jar.setExcludes("*.jar");
      
      // META-INF
      // If the module has a MANIFEST.MF file, this file will be copied
      // using a filter to replace any dependency placeholders with their
      // "versioned" names to create a valid jar manifest.
      if (new File(getCurrentModule().getBaseDir(), "src/META-INF/MANIFEST.MF").exists()) {
        DependencyHelper helper          = new DependencyHelper(getCurrentManifest());
        Copy             copyMetaInf     = (Copy) project.createTask("copy");
        FilterSet        filterSet       = copyMetaInf.createFilterSet();
        FileSet          fileSet         = new FileSet();
        Target           enhanceManifest = new Target();
        
        fileSet.setDir(new File(getCurrentModule().getBaseDir(), "src"));
        fileSet.setIncludes("META-INF/MANIFEST.MF");
        
        filterSet.setFiltersfile(helper.createModuleDependenciesFilter(module));
        
        copyMetaInf.setProject(getProjectInstance());
        copyMetaInf.setTodir(getBuildEnvironment().getModulePackageDirectory());
        copyMetaInf.setOverwrite(true);
        copyMetaInf.setIncludeEmptyDirs(false);
        copyMetaInf.addFileset(fileSet);
        
        enhanceManifest.setName("enhance-manifest");
        enhanceManifest.setProject(project);
        enhanceManifest.addTask(copyMetaInf);
        
        project.addTarget(enhanceManifest);
        project.executeTarget("enhance-manifest");
        
        jar.setManifest(new File(getBuildEnvironment().getModulePackageDirectory(), "META-INF/MANIFEST.MF"));
      }
      
      FileSet metainf = new FileSet();
      
      metainf.setDir(new File(getCurrentModule().getBaseDir(), "src"));
      metainf.setIncludes("META-INF/**/*");
      metainf.setExcludes("META-INF/MANIFEST.MF");
      
      jar.addFileset(metainf);
      
      // resources
      if (new File(getCurrentModule().getBaseDir(), "src/resources").exists()) {
        FileSet resources = new FileSet();
        resources.setDir(new File(getCurrentModule().getBaseDir(), "src/resources"));
        resources.setIncludes("**/*");
        jar.addFileset(resources);
      }
      
      // .class files
      if (getCompileDirectory().exists()) {
        FileSet classes = new FileSet();
        
        classes.setDir(getCompileDirectory());
        classes.setIncludes("**/*.class");
        
        jar.addFileset(classes);
      }

      target.addTask(jar);

      project.executeTarget("jar");      
    } catch (Exception e) {
      e.printStackTrace();
      
      if (logger.isDebugEnabled()) {
        commandResponse.addEvent(new ExceptionEvent(this, e));
      }
      
      throw new CommandException(e, CommandException.PACKAGE_FAILED, new Object[] {getCurrentModule().getName()});
    }

  }

  private void packageWar(File packageName) throws CommandException {

    Project project = getProjectInstance();

    Target target = new Target();
    target.setName("run");
    target.setProject(project);

    project.addTarget(target);

    DependencyHelper helper = new DependencyHelper(getCurrentManifest());

    try {
      executeDelete(getBuildEnvironment().getModuleBuildDirectory(), "*.war");

      // Create a war file.
      //
      War war = (War) project.createTask("war");
      war.setProject(getProjectInstance());
      war.setDestFile(packageName);
      war.setWebxml(new File(getCurrentModule().getBaseDir(), "src/web/WEB-INF/web.xml".replace('/', File.separatorChar)));

      ///src/web
      File webdir = new File(new File(getCurrentModule().getBaseDir(), "src"), "web");
      if (webdir.exists()) {
        FileSet srcWeb = new FileSet();
        srcWeb.setDir(webdir);
        srcWeb.setIncludes("**");
        srcWeb.setExcludes("WEB-INF/web.xml");
        war.addFileset(srcWeb);
      }
      //classes
      if (getCompileDirectory().exists()) {
        ZipFileSet classes = new ZipFileSet();
        classes.setDir(getCompileDirectory());
        war.addClasses(classes);
      }
      //dependencies
      Set deps = helper.getAllDependencies(getCurrentModule(), false, true);
      if (!deps.isEmpty()) {
        ZipFileSet libs;
        Iterator it = deps.iterator();
        while (it.hasNext()) {
          DependencyPath path = (DependencyPath) it.next();
          libs = new ZipFileSet();
          libs.setFile(path.getFullPath());
          war.addLib(libs);
        }
      }

      target.addTask(war);
      project.executeTarget("run");

    } catch (BuildException e) {
      e.printStackTrace();
      if (logger.isDebugEnabled()) {
        commandResponse.addEvent(new ExceptionEvent(this, e));
      }
      throw new CommandException(e, CommandException.PACKAGE_FAILED, new Object[] {getCurrentModule().getName()});
    } catch (DependencyException d) {
      d.printStackTrace();
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    } catch (ModuleTypeException d) {
      d.printStackTrace();
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    }

  }

  private void packageEar(File packageName) throws CommandException {

    Project project = getProjectInstance();

    Target target = new Target();
    target.setName("run");
    target.setProject(project);

    project.addTarget(target);

    DependencyHelper helper = new DependencyHelper(getCurrentManifest());

    try {

      // Create an ear-file
      //
      //reading the application.xml
      DescriptorReader reader = new DescriptorReader(DescriptorReader.APPLICATION_XML);
      try {
        reader.parse(new File(getCurrentModule().getBaseDir(), "src/META-INF"));
      } catch (IOException e) {
        throw new CommandException(e, CommandException.PACKAGE_FAILED_NO_APPLICATION_XML, new Object[]{module.getName()});
      } catch (SAXException e) {
        throw new CommandException(e, CommandException.PACKAGE_FAILED_INVALID_APPLICATION_XML, new Object[]{module.getName()});
      }

      //the application.xml is parsed for included modules.
      //modules are included as follows: @<module_name>@
      //these inclusions are replaced with the name of the packaged module.
      Map map = new Hashtable();
      for (Iterator it = reader.getModuleNames().iterator(); it.hasNext(); ) {
        String moduleName = ((StringBuffer) it.next()).toString();
        Pattern p = Pattern.compile("@("+ModuleDigester.NAME_PATTERN_STRING+")@");
        Matcher m = p.matcher(moduleName);

        if (m.matches()) {
          moduleName = m.group(1);
          Module mod;
          try {
            mod = getCurrentManifest().getModule(moduleName);
          } catch (ManifestException me) {
            throw new DependencyException(DependencyException.EAR_DEPENDENCY_NOT_FOUND, new Object[]{moduleName});
          }

          map.put(moduleName, helper.resolveArchiveName(mod));
          //check whether the module is in the dependencies
          if (!helper.hasModuleDependency(getCurrentModule(), mod, true)) {
            throw new DependencyException(DependencyException.EAR_DEPENDENCY_NOT_DEFINED, new Object[]{moduleName});
          }
        } else {
          //todo: throw new Exception();
        }
      }

      commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Deleting previous ear file.")));
      executeDelete(getBuildEnvironment().getModuleBuildDirectory(), "*.ear");

      commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Copying META-INF dir.")));
      Copy copyMetaInf = (Copy) project.createTask("copy");
      copyMetaInf.setProject(getProjectInstance());
      copyMetaInf.setTodir(getBuildEnvironment().getModulePackageDirectory());
      copyMetaInf.setOverwrite(true);
      copyMetaInf.setIncludeEmptyDirs(false);

      // Filtering
      FilterSet filterSet = copyMetaInf.createFilterSet();
      filterSet.setFiltersfile(helper.createModuleDependenciesFilter(module));

      FileSet fileSet = new FileSet();
      fileSet.setDir(new File(getCurrentModule().getBaseDir(), "src"));
      fileSet.setIncludes("META-INF/**");
      
      copyMetaInf.addFileset(fileSet);
      target.addTask(copyMetaInf);

      //copy the module dependencies from the application.xml
      commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Copying module dependencies")));
      
      Set moduleDeps = helper.getModuleDependencies(getCurrentModule(), false, true);
      if (!moduleDeps.isEmpty()) {
        Copy copy = (Copy) project.createTask("copy");
        copy.setProject(getProjectInstance());
        copy.setTodir(getBuildEnvironment().getModulePackageDirectory());
        copy.setFlatten(true);
        copy.setIncludeEmptyDirs(false);

        Iterator it = moduleDeps.iterator();
        while (it.hasNext()) {
          DependencyPath path = (DependencyPath) it.next();
          fileSet = new FileSet();
          fileSet.setFile(path.getFullPath());
          copy.addFileset(fileSet);
        }
        target.addTask(copy);
      } else {
        logger.info("No module dependencies to package.");
      }

      //copy the non-module dependencies to /lib
      commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Copying jar dependencies")));
      Set jarDeps = helper.getJarDependencies(getCurrentModule(), true);
      if (!jarDeps.isEmpty()) {
        Copy copy = (Copy) project.createTask("copy");
        copy.setProject(getProjectInstance());
        copy.setTodir(new File(getBuildEnvironment().getModulePackageDirectory(), "lib"));
        copy.setFlatten(true);
        copy.setIncludeEmptyDirs(false);

        Iterator it = jarDeps.iterator();
        while (it.hasNext()) {
          DependencyPath path = (DependencyPath) it.next();
          fileSet = new FileSet();
          fileSet.setFile(path.getFullPath());
          copy.addFileset(fileSet);
        }
        target.addTask(copy);
      } else {
        logger.info("No jar dependencies to package.");
      }
      project.executeTarget("run");

      commandResponse.addEvent(new MessageEvent(this, new SimpleMessage("Creating ear")));
      Target target2 = new Target();
      target2.setName("ear");
      target2.setProject(project);

      project.addTarget(target2);

      Ear ear = (Ear) project.createTask("ear");
      ear.setProject(getProjectInstance());
      ear.setDestFile(packageName);
      ear.setBasedir(getBuildEnvironment().getModulePackageDirectory());
      ear.setExcludes("META-INF/application.xml");
      ear.setAppxml(new File(getBuildEnvironment().getModulePackageDirectory(), "META-INF/application.xml".replace('/', File.separatorChar)));

      target2.addTask(ear);
      project.executeTarget("ear");
    } catch (BuildException e) {
      e.printStackTrace();
      if (logger.isDebugEnabled()) {
        commandResponse.addEvent(new ExceptionEvent(this, e));
      }
      throw new CommandException(e, CommandException.PACKAGE_FAILED, new Object[] {getCurrentModule().getName()});
    } catch (DependencyException d) {
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    } catch (ModuleTypeException d) {
      throw new CommandException(d.getErrorCode(), d.getMessageArguments());
    }

  }

  /**
   * Gets the commands' response object.
   *
   * @return The commands' response object.
   */
  public CommandResponse getCommandResponse() {
    return commandResponse;
  }

}
