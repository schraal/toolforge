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
package nl.toolforge.karma.core.cmd.util;

import nl.toolforge.karma.core.manifest.Manifest;
import nl.toolforge.karma.core.module.Module;

import java.io.File;

/**
 * The BuildEnvironment
 *
 * @todo This class is completely java source module specific. We need something more generic.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class BuildEnvironment {

  private Manifest manifest;
  private Module module;
  
  private File moduleSourceDirectory = null;
  private File moduleBuildRootDirectory = null;
  private File moduleBuildDirectory = null;
  private File moduleJavadocDirectory = null;
  private File modulePackageDirectory = null;
  private File moduleTestBuildDirectory = null;
  private File moduleTestSourceDirectory = null;
  

  public BuildEnvironment(Manifest manifest, Module module) {
    this.manifest = manifest;
    this.module = module;
  }

  /**
   * manifest/build/module; location where a modules' artifacts end up, like compiled classes,
   * tests, packages, etc.
   *
   * @return
   */
  public File getModuleBuildRootDirectory() {
    if (moduleBuildRootDirectory == null) {
      moduleBuildRootDirectory = new File(manifest.getBuildBaseDirectory(), module.getName());
    }
    
    return moduleBuildRootDirectory;
  }

  /**
   * manifest/build/module/build; location where a modules' classes have been compiled (the non-test classes).
   *
   * @return
   */
  public File getModuleBuildDirectory() {
    if (moduleBuildDirectory == null) {
      moduleBuildDirectory = new File(getModuleBuildRootDirectory(), "build");
    }
    
    return moduleBuildDirectory;
  }

  /**
   * manifest/build/module/test location where a modules' test classes have been compiled (the test classes).
   *
   * @return
   */
  public File getModuleTestBuildDirectory() {
    if (moduleTestBuildDirectory == null) {
      moduleTestBuildDirectory = new File(getModuleBuildRootDirectory(), "test");
    }
    
    return moduleTestBuildDirectory;
  }

  public File getModuleJavadocDirectory() {
    if (moduleJavadocDirectory == null) {
      moduleJavadocDirectory = new File(getModuleBuildRootDirectory(), "javadoc");
    }
    
    return moduleJavadocDirectory;
  }

  public File getModulePackageDirectory() {
    if (modulePackageDirectory == null) {
      modulePackageDirectory = new File(getModuleBuildRootDirectory(), "package");
    }
    
    return modulePackageDirectory;
  }

  public File getModuleSourceDirectory() {
    if (moduleSourceDirectory == null) {
      moduleSourceDirectory = new File(module.getBaseDir(), "src" + File.separator + "java");
    }
    
    return moduleSourceDirectory;
  }

  public File getModuleTestSourceDirectory() {
    if (moduleTestSourceDirectory == null) {
      moduleTestSourceDirectory = new File(module.getBaseDir(), "test" + File.separator + "java");
    }
    
    return moduleTestSourceDirectory;
  }



}
