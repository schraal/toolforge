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
import nl.toolforge.karma.core.manifest.Module;

import java.io.File;

/**
 * The BuildEnvironment
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public class BuildEnvironment {

  private Manifest manifest;
  private Module module;

  public BuildEnvironment(Manifest manifest, Module module) {
    this.manifest = manifest;
    this.module = module;
  }

  /**
   * @deprecated Use {@link Manifest#getBuildBaseDirectory()} instead.
   */
  public File getBuildRootDirectory() {
//    return new File(manifest.getBaseDirectory(), "build");
    return manifest.getBuildBaseDirectory();
  }

  /**
   * manifest/build/module; location where a modules' artifacts end up, like compiled classes,
   * tests, packages, etc.
   *
   * @return
   */
  public File getModuleBuildRootDirectory() {
    return new File(getBuildRootDirectory(), module.getName());
  }

  /**
   * manifest/build/module/build; location where a modules' classes have been compiled (the non-test classes).
   *
   * @return
   */
  public File getModuleBuildDirectory() {
    return new File(getModuleBuildRootDirectory(), "build");
  }

  /**
   * manifest/build/module/test location where a modules' test classes have been compiled (the test classes).
   *
   * @return
   */
  public File getModuleTestBuildDirectory() {
    return new File(getModuleBuildRootDirectory(), "test");
  }

  public File getModuleJavadocDirectory() {
    return new File(getModuleBuildRootDirectory(), "javadoc");
  }

  public File getModulePackageDirectory() {
    return new File(getModuleBuildRootDirectory(), "package");
  }

  public File getModuleSourceDirectory() {
    return new File(module.getBaseDir(), "src" + File.separator + "java");
  }

  public File getModuleTestSourceDirectory() {
    return new File(module.getBaseDir(), "test" + File.separator + "java");
  }

  /**
   * @deprecated Use {@link Manifest#getBuildBaseDirectory()} instead.
   */
  public File getManifestBuildDirectory() {
//    return new File(manifest.getBaseDirectory(), "build");
    return manifest.getBuildBaseDirectory();
  }
  
}
