package nl.toolforge.karma.core.module;

import nl.toolforge.karma.core.location.Location;
import nl.toolforge.karma.core.vc.VersionControlSystem;
import nl.toolforge.karma.core.manifest.Module;

import java.io.File;

import org.apache.tools.ant.DirectoryScanner;

/**
 * <code>AdminModule</code>s are suitable for module-like structures which are not used by any <code>Manifest</code>. A
 * good example is a module that stores manifests itself.
 *
 * @author D.A. Smedes
 * @version $Id$
 */
public abstract class AdminModule implements Module {

  private String moduleName = null;
  private Location location = null;

  private File baseDir = null;
  private File checkoutDir = null;

  public AdminModule(String moduleName, Location location) {
    this.moduleName = moduleName;
    this.location = location;
  }

  public final String getName() {
    return moduleName;
  }

  /**
   * Gets all file entries in this module. An entry is relative to a modules' base directory. Directories are ignored.
   *
   * @param includePatterns A String array containing the file patterns to include in the output array.
   */
  public final String[] getEntries(String[] includePatterns) {

    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(getBaseDir());
    scanner.setIncludes(includePatterns);

    return scanner.getIncludedFiles();
  }

  /**
   * Checks is
   * @return
   */
  public final boolean isVersionControlled() {
    return location instanceof VersionControlSystem;
  }

  public void setBaseDir(File baseDir) {
    this.baseDir = baseDir;
  }

  public File getBaseDir() {
    return baseDir;
  }

  public void setCheckoutDir(File checkoutDir) {
    this.checkoutDir = checkoutDir;
  }

  public File getCheckoutDir() {
    return checkoutDir;
  }

  /**
   * Returns the location for this
   * @return
   */
  public Location getLocation() {
    return location;
  }
}
