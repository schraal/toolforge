package nl.toolforge.karma.core.manifest;

import nl.toolforge.karma.core.LocalEnvironment;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * @author D.A. Smedes
 * @version $Id$
 */
public interface Manifest {

  public static final String DEVELOPMENT_MANIFEST = "development";
  public static final String RELEASE_MANIFEST = "release";

  public static final String HISTORY_KEY = "manifest.history.last";

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public String getName();

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public Map getAllModules();

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public File getDirectory() throws ManifestException;

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public String resolveArchiveName(Module module) throws ManifestException;

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public Module getModule(String moduleName) throws ManifestException;

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public Collection getModuleInterdependencies(Module module) throws ManifestException;

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public Map getInterdependencies() throws ManifestException;

  /**
   * @see AbstractManifest for the time being
   */
  public void load(LocalEnvironment localEnvironment) throws ManifestException;

  /**
   * @see AbstractManifest for the time being
   */
  public void setState(Module module, Module.State working) throws ManifestException;

  /**
   * @see AbstractManifest for the time being
   *
   * @return
   */
  public boolean isLocal(Module module);
}
