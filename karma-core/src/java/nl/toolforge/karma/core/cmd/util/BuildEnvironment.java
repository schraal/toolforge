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

  public File getBuildRootDirectory() {
    return new File(manifest.getBaseDirectory(), "build");
  }

  /**
   * manifest/build/module; location where a modules' artifacts end up, like compiled classes,
   * tests, packages, etc.
   *
   * @return
   */
  public File getModuleBuildRootDirectory() {

    File moduleBuildDir = new File(getBuildRootDirectory(), module.getName());
//    moduleBuildDir.mkdirs();

    return moduleBuildDir;
  }

  /**
   * manifest/build/module/build; location where a modules' classes have been compiled (the non-test classes).
   *
   * @return
   */
  public File getModuleBuildDirectory() {

    File moduleBuildDir = new File(getModuleBuildRootDirectory(), "build");
//    moduleBuildDir.mkdirs();

    return moduleBuildDir;
  }

  /**
   * manifest/build/module/test location where a modules' test classes have been compiled (the test classes).
   *
   * @return
   */
  public File getModuleTestBuildDirectory() {

    File moduleTestDirectory = new File(getModuleBuildRootDirectory(), "test");
//    moduleTestDirectory.mkdirs();

    return moduleTestDirectory;
  }

  public File getModuleJavadocDirectory() {

    File moduleJavadocDirectory = new File(getModuleBuildRootDirectory(), "javadoc");
//    moduleJavadocDirectory.mkdirs();

    return moduleJavadocDirectory;
  }

  public File getModulePackageDirectory() {

    File modulePackageDirectory = new File(getModuleBuildRootDirectory(), "package");
//    modulePackageDirectory.mkdirs();

    return modulePackageDirectory;
  }

  public File getModuleSourceDirectory() {
    return new File(module.getBaseDir(), "src" + File.separator + "java");
  }

  public File getModuleTestSourceDirectory() {
    return new File(module.getBaseDir(), "test" + File.separator + "java");
  }

  public File getManifestBuildDirectory() {
    return new File(manifest.getBaseDirectory(), "build");
  }
}
